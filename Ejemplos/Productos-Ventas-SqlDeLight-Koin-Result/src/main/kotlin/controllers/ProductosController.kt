package controllers

import io.getstream.result.Error
import io.getstream.result.Result
import models.Producto
import mu.KotlinLogging
import org.koin.core.annotation.Named
import org.koin.core.annotation.Singleton
import repositories.productos.ProductosRepository
import services.storage.productos.ProductosStorageService
import validators.validar

private val logger = KotlinLogging.logger {}

@Singleton
class ProductosController(
    private val productosRepository: ProductosRepository,
    @Named("ProductosCsvService") // Usamos el nombre para diferenciar los servicios, al pertenecer a la misma interfaz
    private val importStorage: ProductosStorageService,
    @Named("ProductosJsonService") // Usamos el nombre para diferenciar los servicios, al pertenecer a la misma interfaz
    private val exportStorage: ProductosStorageService
) {

    fun findAll(): List<Producto> {
        logger.info { "findAll" }
        return productosRepository.findAll()
    }

    fun findAllByDisponible(disponible: Boolean = true): List<Producto> {
        logger.info { "findAll: $disponible" }
        return productosRepository.findAllByDisponible(disponible)
    }

    fun findById(id: Long): Result<Producto> {
        logger.info { "findById: $id" }
        productosRepository.findById(id)?.let {
            return Result.Success(it)
        } ?: return Result.Failure(Error.GenericError("Producto con $id no existe en almacenamiento"))

    }

    fun findByNombre(nombre: String): List<Producto> {
        logger.info { "findByNombre: $nombre" }
        return productosRepository.findByNombre(nombre)
    }

    fun save(producto: Producto): Result<Producto> {
        logger.info { "save: $producto" }
        return producto.validar()
            .onSuccess {
                val res = productosRepository.save(it)
                Result.Success(res)
            }
            .onError {
                Result.Failure(Error.GenericError("Producto no guardado: ${it.message}"))
            }
    }

    fun update(producto: Producto): Result<Producto> {
        logger.info { "update: $producto" }
        return producto.validar()
            .onSuccess {
                val res = productosRepository.update(it)
                Result.Success(res)
            }
            .onError {
                Result.Failure(Error.GenericError("Producto no actualizado: ${it.message}"))
            }
    }

    fun deleteById(id: Long): Result<Producto> {
        logger.info { "deleteById: $id" }
        return findById(id)
            .onSuccess {
                if (productosRepository.deleteById(id))
                    Result.Success(it)
                else
                    Result.Failure(Error.GenericError("No se ha podido borrar el producto con id $id"))

            }
            .onError {
                Result.Failure(Error.GenericError("Producto con $id no existe en almacenamiento"))
            }
    }

    fun exportData() {
        logger.info { "Productos export to Storage" }
        val productos = productosRepository.findAll()
        exportStorage.saveAll(productos)
        logger.debug { "Number of Productos exported to Storage: ${productos.size}" }
    }

    fun importData() {
        logger.info { "Productos import from Storage" }
        val productos = importStorage.loadAll()
        logger.debug { "Number of Productos imported from Storage: ${productos.size}" }
        // borramos antes todos los productos
        // productosRepository.deleteAll() // Me borra los datos que tengo en la base de datos pro script
        // los recorremos e insertamos en el almacén
        productos.forEach { producto ->
            productosRepository.save(producto)
        }
    }
}