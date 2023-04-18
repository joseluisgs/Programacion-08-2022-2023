package repositories

import models.Producto
import java.sql.DriverManager
import java.sql.Statement
import java.time.LocalDateTime
import java.util.*

private fun db() = DriverManager.getConnection("jdbc:sqlite:Productos.db")

class ProductosRepositoryImpl : ProductosRepository {

    init {
        println("Creando la tabla de productos si no existe...")
        // Creamos las tablas si no existen
        db().use { connection ->
            connection.createStatement().use { statement ->
                val sql = """CREATE TABLE IF NOT EXISTS PRODUCTOS (
                    ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    UUID TEXT UNIQUE, 
                    NOMBRE TEXT,
                    PRECIO REAL, CANTIDAD INTEGER,
                    CREATED_AT TEXT,
                    UPDATED_AT TEXT,
                    DISPONIBLE INTEGER
                )""".trimIndent()
                statement.executeUpdate(sql)
            }
        }

    }

    override fun findAll(): List<Producto> {
        println("Buscando todos los productos...")

        val productos = mutableListOf<Producto>()

        db().use { connection ->
            connection.createStatement().use { statement ->
                val sql = "SELECT * FROM PRODUCTOS"
                val rs = statement.executeQuery(sql)
                while (rs.next()) {
                    val producto = Producto(
                        rs.getLong("ID"),
                        UUID.fromString(rs.getString("UUID")),
                        rs.getString("NOMBRE"),
                        rs.getDouble("PRECIO"),
                        rs.getInt("CANTIDAD"),
                        LocalDateTime.parse(rs.getString("CREATED_AT")),
                        LocalDateTime.parse(rs.getString("UPDATED_AT")),
                        rs.getBoolean("DISPONIBLE")
                    )
                    productos.add(producto)
                }
            }
        }
        return productos
    }

    override fun findById(id: Long): Producto? {
        println("Buscando el producto con id $id...")

        var producto: Producto? = null

        db().use { connection ->
            val sql = "SELECT * FROM PRODUCTOS WHERE ID = ?"
            connection.prepareStatement(sql).use { statement ->
                statement.setLong(1, id)
                val rs = statement.executeQuery()
                if (rs.next()) {
                    producto = Producto(
                        rs.getLong("ID"),
                        UUID.fromString(rs.getString("UUID")),
                        rs.getString("NOMBRE"),
                        rs.getDouble("PRECIO"),
                        rs.getInt("CANTIDAD"),
                        LocalDateTime.parse(rs.getString("CREATED_AT")),
                        LocalDateTime.parse(rs.getString("UPDATED_AT")),
                        rs.getBoolean("DISPONIBLE")
                    )
                }
            }
        }

        return producto
    }

    override fun findByUuid(uuid: String): Producto? {
        println("Buscando el producto con uuid $uuid...")
        var producto: Producto? = null
        db().use { connection ->
            val sql = "SELECT * FROM PRODUCTOS WHERE UUID = ?"
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, uuid)
                val rs = statement.executeQuery()
                if (rs.next()) {
                    producto = Producto(
                        rs.getLong("ID"),
                        UUID.fromString(rs.getString("UUID")),
                        rs.getString("NOMBRE"),
                        rs.getDouble("PRECIO"),
                        rs.getInt("CANTIDAD"),
                        LocalDateTime.parse(rs.getString("CREATED_AT")),
                        LocalDateTime.parse(rs.getString("UPDATED_AT")),
                        rs.getBoolean("DISPONIBLE")
                    )
                }
            }
        }
        return producto
    }

    override fun findByNombre(nombre: String): List<Producto> {
        println("Buscando el producto con nombre $nombre...")

        return this.findAll().filter { producto ->
            producto.nombre.lowercase().contains(nombre.lowercase())
        }
    }

    override fun findByDisponible(disponible: Boolean): List<Producto> {
        println("Buscando el producto con disponible $disponible...")

        val productos = mutableListOf<Producto>()

        db().use { connection ->
            val sql = "SELECT * FROM PRODUCTOS WHERE DISPONIBLE = ?"
            connection.prepareStatement(sql).use { statement ->
                statement.setBoolean(1, disponible)
                val rs = statement.executeQuery()
                while (rs.next()) {
                    val producto = Producto(
                        rs.getLong("ID"),
                        UUID.fromString(rs.getString("UUID")),
                        rs.getString("NOMBRE"),
                        rs.getDouble("PRECIO"),
                        rs.getInt("CANTIDAD"),
                        LocalDateTime.parse(rs.getString("CREATED_AT")),
                        LocalDateTime.parse(rs.getString("UPDATED_AT")),
                        rs.getBoolean("DISPONIBLE")
                    )
                    productos.add(producto)
                }
            }
        }

        return productos
    }

    override fun save(entity: Producto): Producto {
        println("Guardando el producto $entity...")

        val createdTime = LocalDateTime.now()
        var myId: Long = 0

        db().use { connection ->
            val sql =
                "INSERT INTO PRODUCTOS (UUID, NOMBRE, PRECIO, CANTIDAD, CREATED_AT, UPDATED_AT, DISPONIBLE) VALUES (?, ?, ?, ?, ?, ?, ?)"
            val stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            stmt.setString(1, entity.uuid.toString())
            stmt.setString(2, entity.nombre)
            stmt.setDouble(3, entity.precio)
            stmt.setInt(4, entity.cantidad)
            stmt.setString(5, createdTime.toString())
            stmt.setString(6, createdTime.toString())
            stmt.setBoolean(7, entity.disponible)

            stmt.executeUpdate()

            val claves = stmt.generatedKeys
            if (claves.next()) {
                myId = claves.getLong(1)
                println("Clave generada: $myId")
            }

            stmt.close()
        }

        return entity.copy(id = myId, createdAt = createdTime, updatedAt = createdTime)
    }


    override fun update(producto: Producto): Producto? {
        val updatedTime = LocalDateTime.now()

        db().use { connection ->
            val sql =
                "UPDATE PRODUCTOS SET NOMBRE = ?, PRECIO = ?, CANTIDAD = ?, UPDATED_AT = ?, DISPONIBLE = ? WHERE ID = ?"
            val stmt = connection.prepareStatement(sql)
            stmt.setString(1, producto.nombre)
            stmt.setDouble(2, producto.precio)
            stmt.setInt(3, producto.cantidad)
            stmt.setString(4, updatedTime.toString())
            stmt.setBoolean(5, producto.disponible)
            stmt.setLong(6, producto.id)

            val rows = stmt.executeUpdate()

            stmt.close()

            return if (rows == 1) producto.copy(updatedAt = updatedTime) else null
        }
    }

    override fun deleteById(id: Long): Boolean {
        db().use { connection ->
            val sql = "DELETE FROM PRODUCTOS WHERE ID = ?"
            val stmt = connection.prepareStatement(sql)
            stmt.setLong(1, id)

            val rows = stmt.executeUpdate()

            stmt.close()

            return rows == 1
        }
    }

    override fun delete(producto: Producto): Boolean {
        db().use { connection ->
            val sql = "DELETE FROM PRODUCTOS WHERE ID = ?"
            val stmt = connection.prepareStatement(sql)
            stmt.setLong(1, producto.id)

            val rows = stmt.executeUpdate()

            stmt.close()

            return rows == 1
        }
    }

    override fun deleteAll(): Boolean {
        db().use { connection ->
            val sql = "DELETE FROM PRODUCTOS"
            val stmt = connection.prepareStatement(sql)

            val rows = stmt.executeUpdate()

            stmt.close()

            return rows == 1
        }
    }


}