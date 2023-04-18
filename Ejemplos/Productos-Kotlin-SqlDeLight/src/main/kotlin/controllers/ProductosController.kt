package controllers

import exceptions.ProductoNoEncontradoException
import models.Producto
import repositories.productos.ProductosRepository
import validators.validar

// Usamos un controlador o servicio para manejar la lógica de negocio, filtrar excepciones, filtrar datos, control nulos, validar, etc.

private val logger = mu.KotlinLogging.logger {}


class ProductosController(
    private val productosRepository: ProductosRepository,
) {

    fun findAll(): List<Producto> {
        logger.info { "findAll" }
        return productosRepository.findAll()
    }

    fun findAllDisponible(disponible: Boolean = true): List<Producto> {
        logger.info { "findAll: $disponible" }
        return productosRepository.findAll().filter { it.disponible == disponible }
    }

    fun findById(id: Long): Producto {
        logger.info { "findById: $id" }
        return productosRepository.findById(id)
            ?: throw ProductoNoEncontradoException("Producto con $id no existe en almacenamiento")
    }

    fun findByNombre(nombre: String): List<Producto> {
        logger.info { "findByNombre: $nombre" }
        return productosRepository.findByNombre(nombre)
    }

    fun save(producto: Producto): Producto {
        logger.info { "save: $producto" }
        producto.validar() // Validar el producto
        return productosRepository.save(producto)
    }

    fun update(producto: Producto): Producto {
        logger.info { "update: $producto" }
        productosRepository.findById(producto.id)
            ?: throw ProductoNoEncontradoException("Producto con ${producto.id} no existe en almacenamiento")
        producto.validar() // Validar el producto
        return productosRepository.update(producto)
    }

    fun deleteById(id: Long): Producto {
        logger.info { "deleteById: $id" }
        // Lo buscamos
        val producto = productosRepository.findById(id)
            ?: throw ProductoNoEncontradoException("Producto con $id no existe en almacenamiento")
        productosRepository.deleteById(id)
        return producto
    }
}