package org.example.cx.kafka101.samples.produser


import mu.KotlinLogging
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

private val logger = KotlinLogging.logger { }
private val TOPIC = "test"
fun main(args: Array<String>) {
    println("Starter basic producer")
    val props = Properties()

    props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "127.0.0.1:9092"
    props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
    props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
    props[ProducerConfig.CLIENT_ID_CONFIG] = "BasicProducer -v1.0"
    /*
    * konfigurasjon man kan skru på
    * */
    props[ProducerConfig.ACKS_CONFIG] = "all" // legal values are : "0","1" and"all"
    // Optional: Retry and timeout settings for improved reliability
    props[ProducerConfig.RETRIES_CONFIG] = 3  // Number of retries before giving up
    props[ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG] = 15000  // Timeout for each request
    // Optional: Set idempotence to true for preventing duplicate messages
    props[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = true  // Ensure exactly-once semantics


    val producer:KafkaProducer<String, String>  = KafkaProducer(props)

    Runtime.getRuntime().addShutdownHook(Thread(Runnable {
        println("Shutting down basic producer")
        producer.close()
    }))

    var key=0
    var value=0
    for (i in 1..10) {
        val timestampTwoDaysAgo = Instant.now().minus(2, ChronoUnit.DAYS).toEpochMilli()
        val record = ProducerRecord(TOPIC, null, timestampTwoDaysAgo, key.toString(), "value-$value")
        val futureResult = producer.send(record)
        val v = futureResult.get()
        key++
        value++
        logger.info { "message sent to topic $TOPIC" }
        Thread.sleep(10)
    }

}