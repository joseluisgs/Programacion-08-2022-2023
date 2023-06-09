package exceptions

// Ya no es necesario, solo para casos de excepciones de dominio
sealed class VentaException(message: String) : Exception(message)
class CarritoNoExisteException(id: Int) : VentaException("Carrito con $id no existe en almacenamiento")
class CarritoNoGuardadoException(message: String) : VentaException("Carrito no guardado: $message")
class CarritoNoValidoException(message: String) : VentaException("Carrito no valido: $message")
