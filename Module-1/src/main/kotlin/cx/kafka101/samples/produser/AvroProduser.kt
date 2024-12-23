package org.example.cx.kafka101.samples.produser

import com.example.Customer
import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

import java.util.*

fun createCustomerAvroProducer(): KafkaProducer<String, Customer> {
    // Kafka producer properties
    val props = Properties().apply {
        put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")  // Kafka broker address
        put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
        put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer::class.java.name)  // Avro serializer
        put("schema.registry.url", "http://localhost:8081")  // Schema Registry URL
    }

    // Create and return the Kafka producer
    return KafkaProducer<String, Customer>(props)
}

fun main() {
    // Create a Kafka Avro producer for Customer objects
    val producer = createCustomerAvroProducer()

    // Create a Customer object
    val customer = Customer(1, "John Doe", "john.doe@example.com", 31)
    val customer2 = Customer(2, "Jane Doe", "jane.doe@example.com", 21)

    // Send the Customer object to the "customer-topic" Kafka topic
    val record = ProducerRecord<String, Customer>("customer", customer.id.toString(), customer)
    val record2 = ProducerRecord<String, Customer>("customer", customer2.id.toString(), customer2)

    // Send the record and handle potential errors
    producer.send(record) { metadata, exception ->
        if (exception == null) {
            println("Customer sent successfully with metadata: $metadata")
        } else {
            println("Error sending customer: ${exception.message}")
            exception.printStackTrace()
        }
    }
    producer.send(record2) { metadata, exception ->
        if (exception == null) {
            println("Customer sent successfully with metadata: $metadata")
        } else {
            println("Error sending customer: ${exception.message}")
            exception.printStackTrace()
        }
    }

    // Flush and close the producer
    producer.flush()
    producer.close()
}
