package repositories

import models.Producto

interface ProductosRepository : CrudRepository<Producto, Long> {
    fun findByUuid(uuid: String): Producto?
    fun findByNombre(nombre: String): List<Producto>
    fun findByDisponible(disponible: Boolean): List<Producto>
    fun update(producto: Producto): Producto?
    fun delete(producto: Producto): Boolean
    fun deleteAll(): Boolean
}