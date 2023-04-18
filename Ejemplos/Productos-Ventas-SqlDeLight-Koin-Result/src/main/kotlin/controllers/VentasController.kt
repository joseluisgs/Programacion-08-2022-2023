package controllers

import exceptions.CarritoNoValidoException
import exceptions.ProductoNoEncontradoException
import exceptions.ProductoNoValidoException
import io.getstream.result.Error
import io.getstream.result.Result
import models.LineaVenta
import models.Venta
import mu.KotlinLogging
import org.koin.core.annotation.Singleton
import repositories.productos.ProductosRepository
import repositories.ventas.VentasRepository
import services.storage.ventas.VentasStorageService
import validators.validar

private val logger = KotlinLogging.logger {}

@Singleton
class VentasController(
    private val ventasRepository: VentasRepository,
    private val productosRepository: ProductosRepository,
    private val exportStorage: VentasStorageService
) {
    fun save(userId: Long, items: Map<Long, Int>): Result<Venta> {
        logger.info { "save: $userId, $items" }
        val venta = Venta(id = 0, userId = userId)
        // Validar
        return venta.validar(items).onSuccess {
            comprobarExistenciaProductos(items)
            // Creamos las lineas de carrito
            crearLineasCarrito(venta, items)
            // añadir lineas al carrito
            // actualizar stock
            actualizarStock(items)
            // guardar venta
            val res = ventasRepository.save(venta)
            Result.Success(res)
        }.onError {
            Result.Failure(Error.GenericError("Error al guardar venta: ${it.message}"))
        }
    }

    private fun crearLineasCarrito(venta: Venta, items: Map<Long, Int>) {
        logger.debug { "crearLineasCarrito: $venta, $items" }
        items.forEach { item ->
            val producto = productosRepository.findById(item.key)
                ?: throw ProductoNoValidoException("Producto con id ${item.key} no es válido")
            logger.debug { "Producto encontrado: $producto" }
            val linea = LineaVenta(
                ventaId = venta.id,
                lineaId = venta.nextLineaId,
                productoId = producto.id,
                cantidad = item.value,
                productoPrecio = producto.precio
            )
            venta.addLinea(linea)
        }
    }

    private fun actualizarStock(items: Map<Long, Int>) {
        items.forEach { item ->
            val producto = productosRepository.findById(item.key)
                ?: throw ProductoNoValidoException("Producto con id ${item.key} no es válido")
            logger.debug { "Producto encontrado: $producto" }
            val updated = producto.copy(cantidad = producto.cantidad - item.value)
            productosRepository.update(updated)
        }
    }

    private fun comprobarExistenciaProductos(items: Map<Long, Int>) {
        logger.debug { "comprobarExistenciaProductos: $items" }
        items.forEach { item ->
            val producto = productosRepository.findById(item.key)
                ?: throw ProductoNoEncontradoException("Producto con id ${item.key} no existe")
            logger.debug { "Producto encontrado: $producto" }
            if (producto.cantidad < item.value) {
                // Esto si que es un error de negocio o excepción de dominio
                throw CarritoNoValidoException("No hay suficiente stock para el producto ${producto.id}")
            }

        }
    }

    fun findById(id: Long): Result<Venta> {
        logger.info { "findById: $id" }
        ventasRepository.findById(id)?.let {
            return Result.Success(it)
        } ?: return Result.Failure(Error.GenericError("Venta con $id no existe en almacenamiento"))
    }

    fun exportData() {
        logger.info { "Ventas export to Storage" }
        val ventas = ventasRepository.findAll()
        exportStorage.saveAll(ventas)
        logger.debug { "Number of Ventas exported to Storage: ${ventas.size}" }
    }

    fun exportInvoice(venta: Venta) {
        logger.info { "saveInvoice: $venta" }
        exportStorage.saveVenta(venta)
    }
}
