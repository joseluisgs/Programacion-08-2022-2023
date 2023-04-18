package validators

import exceptions.ProductoNoValidoException
import models.Producto
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}
fun Producto.validar() {
    logger.debug { "validar: $this" }
    require(nombre.isNotBlank()) { throw ProductoNoValidoException("Nombre no puede estar vacio") }
    require(precio > 0) { throw ProductoNoValidoException("Precio no puede ser menor a 0") }
    require(cantidad >= 0) { throw ProductoNoValidoException("Cantidad debe ser mayor o igual a 0") }
}