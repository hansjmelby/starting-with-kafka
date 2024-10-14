package cx.kafka101.samples

import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import org.apache.avro.Schema
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*

fun createAvroProducer(): KafkaProducer<String, GenericRecord> {
    // Kafka Producer properties
    val props = Properties().apply {
        put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")  // Kafka broker address
        put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
        put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer::class.java.name)  // Confluent Avro serializer
        put("schema.registry.url", "http://127.0.0.1:8081")  // Schema Registry URL
    }

    // Create and return the Kafka producer
    return KafkaProducer<String, GenericRecord>(props)
}

fun main() {
    // Define Avro schema
    val schemaString = """
        {
          "type": "record",
          "name": "User",
          "namespace": "com.example",
          "fields": [
            { "name": "id", "type": "int" },
            { "name": "name", "type": "string" },
            { "name": "email", "type": "string" }
          ]
        }
    """
    val schema = Schema.Parser().parse(schemaString)

    // Create Avro Producer
    val producer = createAvroProducer()

    // Create a record using Avro schema
    val userRecord: GenericRecord = GenericData.Record(schema).apply {
        put("id", 1)
        put("name", "John Doe")
        put("email", "john.doe@example.com")
    }

    // Send Avro record to Kafka
    val record = ProducerRecord<String, GenericRecord>("test-topic", "user-1", userRecord)

    producer.send(record) { metadata, exception ->
        if (exception == null) {
            println("Message sent successfully with metadata: $metadata")
        } else {
            println("Error while sending message: ${exception.message}")
            exception.printStackTrace()
        }
    }

    // Flush and close producer
    producer.flush()
    producer.close()
}
