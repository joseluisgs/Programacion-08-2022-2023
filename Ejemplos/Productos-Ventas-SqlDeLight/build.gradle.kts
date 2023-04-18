plugins {
    kotlin("jvm") version "1.8.0"
    application
    // Plugin de SqlDeLight
    id("app.cash.sqldelight") version "2.0.0-alpha05"
}

group = "es.joseluisgs"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Logger
    implementation("ch.qos.logback:logback-classic:1.4.5")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.4")

    // Moshi
    implementation("com.squareup.moshi:moshi:1.14.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.14.0")
    implementation("com.squareup.moshi:moshi-adapters:1.14.0")

    // SqlDeLight para SQLite
    implementation("app.cash.sqldelight:sqlite-driver:2.0.0-alpha05")

    // SQLite
    // implementation("org.xerial:sqlite-jdbc:3.41.2.1")

    // H2
    // implementation("com.h2database:h2:2.1.214")

    // MyBatis para scripts SQL y otras utilidades
    implementation("org.mybatis:mybatis:3.5.11")


    // Test con JUnit 5 básico
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}

// Configuración del plugin de SqlDeLight
sqldelight {
    databases {
        // Nombre de la base de datos
        create("AppDatabase") {
            // Paquete donde se generan las clases
            packageName.set("dev.joseluisgs.database")
        }
    }
}