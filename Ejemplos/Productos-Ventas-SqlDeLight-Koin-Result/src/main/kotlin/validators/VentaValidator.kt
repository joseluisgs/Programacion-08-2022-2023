package validators

import io.getstream.result.Error
import io.getstream.result.Result
import models.Venta
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun Venta.validar(items: Map<Long, Int>): Result<Venta> {
    logger.debug { "validar: $this" }

    if (items.isEmpty()) return Result.Failure(Error.GenericError("El carrito no tiene productos"))
    if (userId <= 0) return Result.Failure(Error.GenericError("El id del usuario no es válido"))

    return Result.Success(this)
}