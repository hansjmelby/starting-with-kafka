package cx.kafka101.samples


import com.fasterxml.jackson.databind.JsonNode

import mu.KotlinLogging
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*

private val logger = KotlinLogging.logger { }

fun main(){
    println("staring application")
    //sendStringMeldingMedNativeProducer("nativProduserTopic")
    // sendJsonMeldingMedNativeProducer("gadgetTopic")
    sendJsonMeldingMedNativeJsonProducer("JsonGadgetTopic")
    //sendAvroMeldingNativeProducer(

}


fun sendStringMeldingMedNativeProducer(topic:String = "test"){
    val producer = createLensesStringProducer()
    val futureResult = producer.send(
        ProducerRecord(
            topic,
            "key","value"
        )
    )
    val v = futureResult.get()
    logger.info { "message sent to topic $topic" }

}
fun sendJsonMeldingMedNativeProducer(topic:String = "test"){
    val producer = createLensesJsonWithStringProducer() //merk denne!!

    val gadget = Gadget("RED","1",30)
    val futureResult = producer.send(
        ProducerRecord(
            topic,
            gadget.id, objectMapper.writeValueAsString(gadget)
        )
    )
    val v = futureResult.get()
    logger.info { "message sent to topic $topic" }

}


fun sendJsonMeldingMedNativeJsonProducer(topic:String = "test"){
    val producer = createLensesJsonProducer() //merk denne!!

    val gadget = Gadget("RED","1",30)
    val futureResult = producer.send(
        ProducerRecord(
            topic,
            gadget.id, objectMapper.valueToTree(gadget)
        )
    )
    val v = futureResult.get()
    logger.info { "message sent to topic $topic" }

}
fun sendAvroMeldingMedNativeJsonProducer(topic:String = "test"){
    val producer = createLensesAvroProducer() //merk denne!!

    val gadget = Gadget("RED","1",30)
    val futureResult = producer.send(
        ProducerRecord(
            topic,
            gadget.id, objectMapper.valueToTree(gadget)
        )
    )
    val v = futureResult.get()
    logger.info { "message sent to topic $topic" }

}

fun sendMedlingMedGenericProduser(){
    val producer = KafkaMessageProducer("lenses_client.properties","myClientID")
    producer.sendMessageOfTypeTekst("test3","key","value")

}
fun createLensesStringProducer(): KafkaProducer<String, String> {
    val props = Properties()
    props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "127.0.0.1:9092"
    props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
    props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
    props[ProducerConfig.CLIENT_ID_CONFIG] = "SerdesProducer"
    return KafkaProducer(props)
}
fun createLensesJsonWithStringProducer(): KafkaProducer<String, String> {
    val props = Properties()
    props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "127.0.0.1:9092"
    props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
    props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
    props[ProducerConfig.CLIENT_ID_CONFIG] = "SerdesProducer"
    return KafkaProducer(props)
}
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

fun createLensesAvroProducer(): KafkaProducer<String, JsonNode> {
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