plugins {
    kotlin("jvm") version "2.0.21"
    application
}

group = "com.example"
version = "1.0"

repositories {
    mavenCentral()
}

application {
    mainClass.set("MainKt")
}
