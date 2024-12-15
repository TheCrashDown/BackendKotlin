plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.serialization") version "2.0.21"
    application
}

group = "com.example"
version = "1.0"

repositories {
    mavenCentral()
}
dependencies {
    implementation("io.ktor:ktor-server-core:3.0.2")
    implementation("io.ktor:ktor-server-netty:3.0.2")
    implementation("io.ktor:ktor-server-content-negotiation:3.0.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.2")

    implementation("io.ktor:ktor-server-auth:3.0.2")
    implementation("io.ktor:ktor-server-auth-jwt:3.0.2")
    implementation("org.mindrot:jbcrypt:0.4")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0")
}

application {
    mainClass.set("MainKt")
}
