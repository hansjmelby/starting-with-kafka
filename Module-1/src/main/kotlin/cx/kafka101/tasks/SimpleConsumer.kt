package org.example.cx.kafka101.tasks

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.errors.WakeupException
import org.apache.kafka.common.serialization.StringDeserializer
import java.time.Duration
import java.util.*

fun main() {
    val bootstrapServers = "localhost:9092" // Kafka broker URL
    val topic = "test"                // Replace with your topic

    // Kafka Consumer configuration settings
    val props = Properties().apply {

    }

    // Create Kafka consumer


    Runtime.getRuntime().addShutdownHook(Thread(Runnable {
        // Close the consumer gracefully (this part won't be reached in an infinite loop)
        println("shutting down...")

    }))

    // Subscribe to the topic


    // Poll the broker for new data
    try {
        while (true) {
            try {
                // Poll with a timeout of 1 second
                val records = emptyList<ConsumerRecord<String,String>>()
                println("Records received : ${records.count()}")
                for (record in records) {
                    println("Received message: key=${record.key()}, value=${record.value()}, partition=${record.partition()}, offset=${record.offset()}")
                }

            } catch (e: WakeupException) {
                println("Received shutdown signal.")
                break
            }
        }
    }
    finally {
        // Ensure that the consumer is properly closed in case of errors

        println("Consumer closed.")
    }


}