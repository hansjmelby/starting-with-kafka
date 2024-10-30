package org.example.cx.kafka102.streaming.samples.sensor

import com.example.Customer
import com.example.Sensor
import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import java.util.Properties
import java.util.UUID

fun main(){
    run()
}

fun createSensorAvroProducer(): KafkaProducer<String, Sensor> {
    // Kafka producer properties
    val props = Properties().apply {
        put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")  // Kafka broker address
        put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
        put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer::class.java.name)  // Avro serializer
        put("schema.registry.url", "http://localhost:8081")  // Schema Registry URL
    }

    // Create and return the Kafka producer
    return KafkaProducer<String, Sensor>(props)
}

fun run(){
    val producer = createSensorAvroProducer()

    val sensorIdList = listOf<String>("Sensor1", "Sensor2", "Sensor3", "Sensor4", "Sensor5", "Sensor6", "Sensor7", "Sensor8")

    // Create a Customer object
    while (true) {
        val sensor = Sensor(
            sensorIdList.random(),
            (-10..30).random(),
            (20..90).random(),
            (0..5000).random()
        )
        val record = ProducerRecord<String, Sensor>("sensor-topic",sensor.serialNumber, sensor)
        val result = producer.send(record).get()
        println("publisert sensor record: til partisjon ${result.partition()}, offset ${result.offset()} ")
        Thread.sleep(1000)
    }
    producer.flush()
    producer.close()

}