import config.AppConfig
import controllers.ProductosController
import controllers.VentasController
import factories.productoRandom
import models.User
import mu.KotlinLogging
import repositories.productos.ProductosRepositoryImpl
import repositories.ventas.VentasRepositoryImpl
import services.database.SqlDeLightClient
import services.storage.productos.ProductosFicheroCsvService
import services.storage.productos.ProductosFicheroJsonService
import services.storage.ventas.VentasFicheroJsonService
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

@ExperimentalStdlibApi
fun main(args: Array<String>) {
    println("Hola Carro de Compra con Bases de Datos")

    println(LocalDateTime.now())

    // Leemos la configuración de la aplicación
    println("APP_NAME: ${AppConfig.APP_NAME}")

    // Inyectamos dependencias
    val productosRepository = ProductosRepositoryImpl(
        sqlClient = SqlDeLightClient
    )
    // Productos, importamos, operamos en Base de Datos y exportamos a JSON
    val productosController = ProductosController(
        productosRepository = productosRepository,
        importStorage = ProductosFicheroCsvService(),
        exportStorage = ProductosFicheroJsonService()
    )

    val ventasRepository = VentasRepositoryImpl(
        sqlClient = SqlDeLightClient
    )
    val ventasController = VentasController(
        ventasRepository = ventasRepository,
        productosRepository = productosRepository,
        exportStorage = VentasFicheroJsonService()
    )


    // importamos los productos del CSV
    productosController.importData()

    // Operamos con los productos

    println("Todos los productos")
    productosController.findAll().forEach { println(it.toLocalString()) }

    println()
    println("Info Producto con ID 1")
    productosController.findById(1).also { println(it.toLocalString()) }

    println()
    println("Ponemos nuevo nombre no disponible el producto con ID 1")
    val producto = productosController.findById(1)
    val updatedProducto = producto.copy(nombre = "Producto 1 Updated", disponible = false)
    productosController.update(updatedProducto).also { println(it.toLocalString()) }

    println()
    println("Insertamos un nuevo producto")
    val newProducto = productoRandom()
    productosController.save(newProducto).also { println(it.toLocalString()) }

    println()
    println("Borramos el producto con ID 1")
    productosController.deleteById(1).also { println(it.toLocalString()) }

    println("Todos los productos")
    productosController.findAll().forEach { println(it.toLocalString()) }

    // Exportamos los productos a JSON
    println()
    println("Exportamos los productos a JSON")
    productosController.exportData()


    // Vamos ahora con las ventas
    println()
    println("Venta con ID 1")
    val venta1 = ventasController.findById(1).also { println(it.toLocalString()) }

    // Sacamos una factura de la venta con ID 1
    println()
    println("Factura de la venta con ID 1")
    ventasController.exportInvoice(venta1)

    println()
    println("Insertar nueva Venta")
    val user = User(1, "Juan", "juan@juan.com", "123456")
    val items = mapOf(2L to 2, 3L to 2) // Daría 60€
    var venta2 = ventasController.save(user.id, items).also { println(it.toLocalString()) }

    println()
    println("Factura de la venta con ID 2")
    ventasController.exportInvoice(venta2)

    // Exportamos las ventas a JSON
    println()
    println("Exportamos los productos a JSON")
    ventasController.exportData()


}

