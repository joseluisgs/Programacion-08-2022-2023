package services.storage.ventas

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import config.AppConfig
import models.Venta
import mu.KotlinLogging
import utils.LocalDateAdapter
import utils.LocalDateTimeAdapter
import utils.UuidAdapter
import utils.toPrettyJson
import java.io.File

private val logger = KotlinLogging.logger {}

@ExperimentalStdlibApi // Para usar el método DE adapter con enlazado de Kotlin
class VentasFicheroJsonService : VentasStorageService {

    private val localFile = "${AppConfig.APP_DATA}${File.separator}ventas.json"

    private val moshi = Moshi.Builder()
        .add(UuidAdapter())
        .add(LocalDateTimeAdapter())
        .add(LocalDateAdapter())
        .addLast(KotlinJsonAdapterFactory())
        .build()
    private val jsonAdapter = moshi.adapter<List<Venta>>()


    override fun saveAll(items: List<Venta>) {
        logger.debug { "Guardando ventas en fichero json" }
        val file = File(localFile)
        file.writeText(jsonAdapter.toPrettyJson(items))
    }

    override fun loadAll(): List<Venta> {
        logger.debug { "Cargando ventas desde fichero de json" }
        val file = File(localFile)
        return jsonAdapter.fromJson(file.readText()) ?: emptyList()
    }

    override fun saveVenta(venta: Venta) {
        logger.debug { "Guardando venta ${venta.id} en fichero json" }
        val localFile = "${AppConfig.APP_DATA}${File.separator}venta-${venta.id}.json"
        val file = File(localFile)
        val jsonAdapter = moshi.adapter<Venta>()
        file.writeText(jsonAdapter.toPrettyJson(venta))
    }


}