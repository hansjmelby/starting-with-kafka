package org.example.cx.kafka101.samples.produser


import mu.KotlinLogging
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*

private val logger = KotlinLogging.logger { }
private val TOPIC = "test"

fun main() {
    println("Starter basic producer")
    val props = Properties()

    props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "127.0.0.1:9092"
    props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
    props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
    props[ProducerConfig.CLIENT_ID_CONFIG] = "BasicProducer -v1.0"

    val producer:KafkaProducer<String, String>  = KafkaProducer(props)

    Runtime.getRuntime().addShutdownHook(Thread(Runnable {
        println("Shutting down basic producer")
        producer.close()
    }))

    var key=0
    var value=0
    for (i in 1..10) {
        val futureResult = producer.send(
            ProducerRecord(
                TOPIC,
                key.toString(),"value-$value",
            )
        )
        val v = futureResult.get()
        key++
        value++
        logger.info { "message sent to topic $TOPIC" }
        Thread.sleep(10000)
    }

}