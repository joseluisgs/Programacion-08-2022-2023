package validators

import io.getstream.result.Error
import io.getstream.result.Result
import models.Producto
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun Producto.validar(): Result<Producto> {
    logger.debug { "validar: $this" }

    if (nombre.isBlank()) return Result.Failure(Error.GenericError("El nombre no puede estar vacío"))
    if (precio < 0) return Result.Failure(Error.GenericError("Precio debe ser mayor o igual a 0"))
    if (cantidad < 0) return Result.Failure(Error.GenericError("Cantidad debe ser mayor o igual a 0"))

    return Result.Success(this)
}