package cx.kafka102.streaming.samples.streamjoins

import com.example.AvgTemperature
import com.example.Customer
import com.example.Sensor
import com.example.SensorAlarm
import com.example.SensorLocation
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Joined
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.kstream.Produced
import org.apache.kafka.streams.kstream.ValueJoiner
import java.util.Properties


fun <T : SpecificRecord> getSpecificAvroSerde(serdeConfig: Map<String, Any>): SpecificAvroSerde<T> {
    val specificAvroSerde = SpecificAvroSerde<T>()
    specificAvroSerde.configure(serdeConfig, false)
    return specificAvroSerde
}

fun main(){
    val props = Properties().apply {
        put(StreamsConfig.APPLICATION_ID_CONFIG, "joining-streams")
        put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
        put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081")
        put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().javaClass)
        put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde::class.java)
    }
    val builder =  StreamsBuilder();
    //var sensorSerde:SpecificAvroSerde<Sensor>  = getSpecificAvroSerde(mapOf<String, Sensor>());

    //var sensorAlarmSerde:SpecificAvroSerde<SensorAlarm> = getSpecificAvroSerde(mapOf<String, SensorAlarm>());
    val locationSerde = SpecificAvroSerde<SensorLocation>().apply {
        configure(mapOf(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG to "http://localhost:8081"), false)
    }
    val SensorAlarmSerde = SpecificAvroSerde<SensorAlarm>().apply {
        configure(mapOf(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG to "http://localhost:8081"), false)
    }
    val sensorSerde = SpecificAvroSerde<Sensor>().apply {
        configure(mapOf(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG to "http://localhost:8081"), false)
    }

    val sensorJoin = AlarmSensorJoiner()


    val locationTable:KTable<String, SensorLocation>  = builder.table("sensor-lokasjon", Materialized.with(Serdes.String(), locationSerde));
    val sensorDataStream: KStream<String, Sensor> = builder.stream("sensor-topic", Consumed.with(Serdes.String(), sensorSerde))

    sensorDataStream.leftJoin(
        locationTable,
        sensorJoin)
        .peek{ key, value -> println("Stream-Table Join record key " + key + " value " + value)}
        .to("sensor-alarm-raw",Produced.with(Serdes.String(), SensorAlarmSerde))


    val locationStream = locationTable.toStream()
    Thread {
        while (true) {
            locationStream.foreach { key, location ->
                println("Sensor ID: $key, Location: $location")
            }
            Thread.sleep(60000) // Vent i 60 sekunder (1 minutt)
        }
    }.start()

    // Start Kafka Streams-applikasjonen
    val streams = KafkaStreams(builder.build(), props)
    streams.start()

    // Shutdown hook for å stoppe applikasjonen riktig
    Runtime.getRuntime().addShutdownHook(Thread(streams::close))



}

class AlarmSensorJoiner():ValueJoiner<Sensor, SensorLocation, SensorAlarm> {
    override fun apply(sensor: Sensor?, lokasjon: SensorLocation?): SensorAlarm? {
        if (sensor == null) {
            return null
        }
        if (lokasjon == null) {
            return null
        }
        return SensorAlarm(sensor.serialNumber,lokasjon.location,lokasjon.owner,lokasjon.city,lokasjon.armed,sensor.temperature,sensor.moisture,sensor.alarm)
    }
}