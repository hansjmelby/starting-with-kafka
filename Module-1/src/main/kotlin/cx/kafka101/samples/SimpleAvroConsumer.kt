package cx.workshop.messageoriented.cx.kafka101.samples

import io.confluent.kafka.serializers.KafkaAvroDeserializer
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import java.util.Properties

data class Customer(
    val id: Int,
    val name: String,
    val email: String,
    val age: Int
)

fun createAvroConsumer(): KafkaConsumer<String, Customer> {
    val props = Properties().apply {
        put("bootstrap.servers", "localhost:9092")
        put("group.id", "test")
        put("key.deserializer", StringDeserializer::class.java.name)
        put("value.deserializer", KafkaAvroDeserializer::class.java.name)
        put("schema.registry.url", "http://localhost:8081")
        put("specific.avro.reader", "true")  // Enable specific Avro reader
        put("value.deserializer", KafkaAvroDeserializer::class.java.name)
    }

    // Configure the Avro deserializer to use the specific Java class
    val deserializer = KafkaAvroDeserializer()
    deserializer.configure(
        mapOf(
            "schema.registry.url" to "http://localhost:8081",
            "specific.avro.reader" to "true"
        ),
        false
    )

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