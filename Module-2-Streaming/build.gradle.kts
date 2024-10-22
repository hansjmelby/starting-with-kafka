val kafkaVersion = "7.5.2-ccs"
val jacksonVersion = "2.14.0"
plugins {
    kotlin("jvm") version "2.0.20"
}

group = "no.cx.workshop.streaming"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://packages.confluent.io/maven/")
}

dependencies {
    testImplementation(kotlin("test"))
    //Streams dependencies
    implementation("org.apache.kafka:kafka-streams:3.5.1")
    implementation("io.confluent:kafka-streams-avro-serde:7.0.0")
    implementation("org.apache.kafka:kafka-clients:$kafkaVersion")
    //jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(20)
}