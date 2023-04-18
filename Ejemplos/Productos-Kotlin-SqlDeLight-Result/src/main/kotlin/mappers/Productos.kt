package mappers

import database.ProductoEntity
import models.Producto
import java.time.LocalDateTime
import java.util.*

fun ProductoEntity.toProducto(): Producto {
    return Producto(
        id = this.id,
        uuid = UUID.fromString(this.uuid),
        nombre = this.nombre,
        precio = this.precio,
        cantidad = this.cantidad.toInt(),
        createdAt = LocalDateTime.parse(this.created_at),
        updatedAt = LocalDateTime.parse(this.updated_at),
        disponible = this.disponible.toInt() == 1
    )
}