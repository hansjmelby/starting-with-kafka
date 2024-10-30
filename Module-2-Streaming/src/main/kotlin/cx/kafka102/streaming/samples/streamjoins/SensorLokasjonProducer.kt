package org.example.cx.kafka102.streaming.samples.streamjoins

import com.example.Sensor
import com.example.SensorLocation
import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.example.cx.kafka102.streaming.samples.sensor.createSensorAvroProducer
import java.util.Properties


fun main() {
    run()
}




fun createSensorLokasjonAvroProducer(): KafkaProducer<String, SensorLocation> {
    // Kafka producer properties
    val props = Properties().apply {
        put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")  // Kafka broker address
        put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
        put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer::class.java.name)  // Avro serializer
        put("schema.registry.url", "http://localhost:8081")  // Schema Registry URL
    }

    // Create and return the Kafka producer
    return KafkaProducer<String, SensorLocation>(props)
}

fun run(){
    val producer = createSensorLokasjonAvroProducer()

    val sensorIdList = listOf<String>("Sensor1", "Sensor2", "Sensor3", "Sensor4", "Sensor5", "Sensor6", "Sensor7", "Sensor8")
    val sensor1 = SensorLocation(sensorIdList.get(0),"STUE","hjm@computas.com","OSLO",false)
    val sensor2 = SensorLocation(sensorIdList.get(1),"SOV","hjm@computas.com","OSLO",false)
    val sensor3 = SensorLocation(sensorIdList.get(2),"KJØKKEN","hjm@computas.com","OSLO",false)
    val sensor4 = SensorLocation(sensorIdList.get(3),"GANG","hjm@computas.com","OSLO",false)
    val sensor5 = SensorLocation(sensorIdList.get(4),"STUE","jlet@computas.com","OSLO",false)
    val sensor6 = SensorLocation(sensorIdList.get(5),"SOV","jlet@computas.com","OSLO",false)
    val sensor7 = SensorLocation(sensorIdList.get(6),"KJØKKEN","jlet@computas.com","OSLO",false)
    val sensor8 = SensorLocation(sensorIdList.get(7),"GANG","jlet@computas.com","OSLO",false)
    val list = listOf<SensorLocation>(sensor1,sensor2,sensor3,sensor4,sensor5,sensor6,sensor7,sensor8)
    list.forEach {
        val record = ProducerRecord<String, SensorLocation>("sensor-lokasjon",it.serialNumber, it)
        val result = producer.send(record).get()
        println("publisert sensor record: til partisjon ${result.partition()}, offset ${result.offset()} ")
    }
    producer.flush()
    producer.close()

}