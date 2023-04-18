import models.Producto
import repositories.ProductosRepositoryImpl
import java.sql.DriverManager
import java.time.LocalDateTime
import java.util.*

fun main(args: Array<String>) {
    println("Hola Bases de Datos!!!")

    // Ejemplo de JDBC

    // 1. Cargar el driver
    // Class.forName("org.sqlite.JDBC")

    // 2. Crear la conexión
    val connection = DriverManager.getConnection("jdbc:sqlite:Productos.db")


    // Podemos incluso crear las tablas que necesitemos
    var sql = """CREATE TABLE IF NOT EXISTS PRODUCTOS (
        ID INTEGER PRIMARY KEY AUTOINCREMENT,
        UUID TEXT UNIQUE, 
        NOMBRE TEXT,
        PRECIO REAL, CANTIDAD INTEGER,
        CREATED_AT TEXT,
        UPDATED_AT TEXT,
        DISPONIBLE INTEGER
    )""".trimIndent()

    // Crear el statment y con executeUpdate ejecutarlo
    val statement0 = connection.prepareStatement(sql)
    statement0.executeUpdate()
    // Cerrar el statement
    statement0.close()


    // 3. Crear el statement, siempre con prepareStatement para evitar SQL Injection y poder usar parámetros y reutilizar
    sql = "SELECT * FROM PRODUCTOS"
    val statement = connection.prepareStatement(sql)

    // 4. Ejecutar la consulta, siempre con executeQuery (porque no vamos a modificar nada)
    val resultSet = statement.executeQuery()

    // 5. Recorrer el resultado y mostrarlo seleccionando las columnas por su nombre
    while (resultSet.next()) {
        println(
            "ID: ${resultSet.getInt("ID")}, Nombre: ${resultSet.getString("NOMBRE")}, Precio: ${
                resultSet.getDouble(
                    "PRECIO"
                )
            }"
        )
    }

    // 6. Cerrar el resultSet
    resultSet.close()

    // 7. Cerrar el statement
    statement.close()

    // 8. puedes  modificar la base de datos con executeUpdate (insert, update, delete)
    sql =
        "INSERT INTO PRODUCTOS (UUID, NOMBRE, PRECIO, CANTIDAD, CREATED_AT, UPDATED_AT, DISPONIBLE) VALUES (?, ?, ?, ?, ?, ?, ?)"
    val statement2 = connection.prepareStatement(sql)
    statement2.setString(1, UUID.randomUUID().toString())
    statement2.setString(2, "Producto Insert")
    statement2.setDouble(3, 10.0)
    statement2.setInt(4, 10)
    statement2.setString(5, LocalDateTime.now().toString())
    statement2.setString(6, LocalDateTime.now().toString())
    statement2.setBoolean(7, true)
    var res = statement2.executeUpdate()
    println("Filas afectadas tras insertar: $res")

    // 9. Cerrar el statement
    statement2.close()

    // 10. Esta vez solo vamos a buscar un producto y además vamos a mapearlo a un objeto
    sql = "SELECT * FROM PRODUCTOS WHERE ID = ?"
    val statement3 = connection.prepareStatement(sql)
    statement3.setInt(1, 2)
    val resultSet2 = statement3.executeQuery()
    while (resultSet2.next()) {
        val producto = Producto(
            resultSet2.getLong("ID"),
            UUID.fromString(resultSet2.getString("UUID")),
            resultSet2.getString("NOMBRE"),
            resultSet2.getDouble("PRECIO"),
            resultSet2.getInt("CANTIDAD"),
            LocalDateTime.parse(resultSet2.getString("CREATED_AT")),
            LocalDateTime.parse(resultSet2.getString("UPDATED_AT")),
            resultSet2.getBoolean("DISPONIBLE")
        )
        println(producto.toLocalString())
    }

    // 11. Cerrar el resultSet
    resultSet2.close()

    // 12. Cerrar el statement
    statement3.close()

    // 13. Ahora del tiron borramos el producto insertado
    sql = "DELETE FROM PRODUCTOS WHERE NOMBRE = ?"
    val statement4 = connection.prepareStatement(sql)
    statement4.setString(1, "Producto Insert")
    res = statement4.executeUpdate()
    println("Filas afectadas tras eliminar: $res")

    // 14. Cerrar el statement
    statement4.close()

    // 15. No olvides cerrar la conexión
    //connection.close()

    // Podemos usar la interfaz AutoCloseable para cerrar la conexión automáticamente
    connection.use {
        // Hacer lo que queramos con la conexión
        val sql = "SELECT * FROM PRODUCTOS"
        connection.prepareStatement(sql).use {
            // Hacer lo que queramos con el statement
            it.executeQuery().use {
                // Hacer lo que queramos con el resultSet
                while (it.next()) {
                    println(
                        "ID: ${it.getInt("ID")}, Nombre: ${it.getString("NOMBRE")}, Precio: ${
                            it.getDouble(
                                "PRECIO"
                            )
                        }"
                    )
                }
            }

        }
    }

    // Ahora con el repositorio

    // 1. Crear el repositorio
    val productosRepository = ProductosRepositoryImpl()

    productosRepository.findAll().forEach {
        println(it.toLocalString())
    }

    // vamos a insertar un producto
    val producto = Producto(
        nombre = "Producto Insert",
        precio = 10.0,
        cantidad = 10,
    )

    val productoInsertado = productosRepository.save(producto)
    println(productoInsertado.toLocalString())

    // vamos a buscar el producto insertado
    val productoBuscado = productosRepository.findById(productoInsertado.id)
    println(productoBuscado?.toLocalString())

    // vamos a actualizar el producto insertado
    val productoActualizado = productosRepository.update(
        productoInsertado.copy(nombre = "Producto Actualizado")
    )
    println(productoActualizado?.toLocalString())

    // vamos a borrar el producto insertado
    val productoBorrado = productosRepository.deleteById(productoInsertado.id)
    if (productoBorrado) {
        println("Producto borrado")
    } else {
        println("No se ha podido borrar el producto")
    }


}

