val ktorVersion = "2.1.3"
val kafkaVersion = "7.5.2-ccs"
val jacksonVersion = "2.14.0"
val konfigVersion = "1.6.10.0"
val kotlinLoggerVersion = "1.8.3"
val resilience4jVersion = "1.5.0"
val logstashVersion = "7.2"
val logbackVersion = "1.2.9"
val httpClientVersion = "4.5.13"
val mainClass = "no.nav.medlemskap.saga.ApplicationKt"

plugins {
    kotlin("jvm") version "1.7.10"
    application
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("com.github.davidmc24.gradle.plugin.avro") version "1.5.0"  // Correct Avro Gradle Plugin
}

group = "no.cx.workshop"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://packages.confluent.io/maven/")
    maven("https://jitpack.io")
}


dependencies {
    implementation(kotlin("stdlib"))
    implementation("io.github.resilience4j:resilience4j-retry:$resilience4jVersion")
    implementation("io.github.resilience4j:resilience4j-kotlin:$resilience4jVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion") {
        exclude(group = "io.netty", module = "netty-codec")
        exclude(group = "io.netty", module = "netty-codec-http")
    }
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
    implementation("org.apache.httpcomponents:httpclient:$httpClientVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-call-id-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-apache:$ktorVersion")
    implementation("io.ktor:ktor-client-json:$ktorVersion")
    implementation("org.apache.httpcomponents:httpclient:$httpClientVersion")
    // AVRO SPESIFIC
    implementation("io.confluent:kafka-avro-serializer:7.7.0")
    implementation("io.confluent:kafka-streams-avro-serde:7.0.0")

    // --END AVRO---


    implementation("io.micrometer:micrometer-registry-prometheus:1.7.0")
    implementation("io.ktor:ktor-server-metrics-micrometer-jvm:2.1.2")
    implementation("com.natpryce:konfig:$konfigVersion")
    implementation("io.github.microutils:kotlin-logging:$kotlinLoggerVersion")
    implementation("net.logstash.logback:logstash-logback-encoder:$logstashVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    // 2.8.0 er tilgjengelig, burde kanskje oppdatere
    implementation("org.apache.kafka:kafka-clients:$kafkaVersion")
    testImplementation(platform("org.junit:junit-bom:5.7.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.mockk:mockk:1.11.0")
    testImplementation("io.ktor:ktor-client-mock:$ktorVersion")
    testImplementation("org.testcontainers:kafka:1.17.6")
    testImplementation ("org.testcontainers:postgresql:1.16.0")
    testImplementation ("org.testcontainers:junit-jupiter:1.16.0")
    //Database


    //Streams dependencies
    implementation("org.apache.kafka:kafka-streams:3.5.1")

}

avro {
    //outputDir.set(file("src/main/kotlin"))  // Directory for generated Java classes
    isCreateSetters.set(true)  // Optional: Generate setters
    stringType.set("String")  // Set Avro strings to use Java String type
    kotlin
}

//tasks.register<Exec>("fetchAvroSchema") {
    //val schemaUrl = "http://localhost:8081/subjects/nyc_yellow_taxi_trip_data-value/versions/latest"
    //val schemaFile = file("src/main/avro/nyc_yellow_taxi_trip.avsc")

    //commandLine("curl", "-X", "GET", schemaUrl, "-o", schemaFile.absolutePath)
//}

tasks.named("build") {
    //dependsOn("fetchAvroSchema")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "18"
        kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }

    compileTestKotlin {
        kotlinOptions.jvmTarget = "18"
    }


    test {
        useJUnitPlatform()
        //Trengs inntil videre for bytebuddy med java 16, som brukes av mockk.
        jvmArgs = listOf("-Dnet.bytebuddy.experimental=true")
    }
}

application {
    mainClass.set("no.nav.medlemskap.saga.ApplicationKt")
}
