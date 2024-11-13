import com.fasterxml.jackson.databind.JsonNode
import cx.kafka101.samples.Gadget
import cx.kafka101.samples.objectMapper
import org.example.cx.kafka101.samples.serializers.JsonNodeSerializer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

import java.util.*

fun createLensesJsonProducer(): KafkaProducer<String, JsonNode> {
    // Kafka producer properties
    val props = Properties().apply {
        put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")  // Kafka broker address
        put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
        put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonNodeSerializer::class.java.name)  // Custom JsonNode serializer
        put(ProducerConfig.ACKS_CONFIG, "all")  // Ensures all brokers acknowledge the message
        put(ProducerConfig.RETRIES_CONFIG, 0)   // No retries
        put(ProducerConfig.LINGER_MS_CONFIG, 1)  // Small delay to batch messages
        put(ProducerConfig.BATCH_SIZE_CONFIG, 16384)  // Batch size
    }

    println("Kafka producer is being created with the following properties: $props")

    // Create and return the KafkaProducer
    return KafkaProducer<String, JsonNode>(props)
}

fun main(args: Array<String>) {
    val producer = createLensesJsonProducer()

    Runtime.getRuntime().addShutdownHook(Thread(Runnable {
        println("Shutting down basic producer")
        producer.close()
    }))
    for (i in 1..10000) {
        val gadget = Gadget(color=picColor(),id=(1..10).random().toString(), temp =(1..10).random())
        val futureResult = producer.send(
            ProducerRecord(
                "JsonGadgetTopic",
                gadget.id, objectMapper.valueToTree(gadget),
            )
        )
        val v = futureResult.get()
        println("one down")
    }
}
fun picColor():String {
    val rnds = (0..10).random()
    when (rnds) {
        1 -> return "red"
        2 -> return "blue"
        3 -> return "green"
        4 -> return "black"
        5 -> return "yellow"
        6 -> return "pink"
        7 -> return "brown"
        8 -> return "purple"
        9 -> return "white"
        10 -> return "orange"
        else -> return "unknown"
    }
}