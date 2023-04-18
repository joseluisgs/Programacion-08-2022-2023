package exceptions


sealed class ProductoException(message: String) : Exception(message)
class ProductoNoEncontradoException(message: String) : ProductoException("Producto no encontrado: $message")
class ProductoNoGuardadoException(message: String) : ProductoException("Producto no guardado: $message")
class ProductoNoValidoException(message: String) : ProductoException("Producto no valido: $message")
