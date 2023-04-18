package exceptions

// Ya no es necesario, solo para casos de excepciones de dominio
sealed class ProductoException(message: String) : Exception(message)
class ProductoNoEncontradoException(message: String) : ProductoException("Producto no encontrado: $message")
class ProductoNoDisponibleException(message: String) : ProductoException("Producto no disponible: $message")
class ProductoNoValidoException(message: String) : ProductoException("Producto no valido: $message")
