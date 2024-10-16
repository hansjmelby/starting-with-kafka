package org.example.cx.kafka101.samples.consumer


import com.example.Customer
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import java.util.Properties




fun createAvroConsumer(): KafkaConsumer<String, Customer> {
    val props = Properties().apply {
        put("schema.registry.url", "http://localhost:8081")
        put("specific.avro.reader", "true")  // Enable specific Avro reader
        put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
        put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer::class.java.name)
        put(ConsumerConfig.GROUP_ID_CONFIG, "my-avro-consumer-groupV2") // Consumer group ID
        put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest") // Start from the earliest message
        // Enable auto commit (true by default)
        put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,"true")
        // Set the auto commit interval to 1 second,(default is 5000 ms)
        put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,"1000")
    }

    // Configure the Avro deserializer to use the specific Java class

    /*
    val deserializer = KafkaAvroDeserializer()
    deserializer.configure(
        mapOf(
            "schema.registry.url" to "http://localhost:8081",
            "specific.avro.reader" to "true"
        ),
        false
    )

     */

    return KafkaConsumer<String, Customer>(props)
}

fun main() {
    val consumer = createAvroConsumer()
    consumer.subscribe(listOf("customer-topic"))

    while (true) {
        val records = consumer.poll(java.time.Duration.ofSeconds(1))
        println("record found : ${records.count()}")
        for (record in records) {

            val customer = record.value() as Customer
            println("Received customer: $customer")
        }
    }
}