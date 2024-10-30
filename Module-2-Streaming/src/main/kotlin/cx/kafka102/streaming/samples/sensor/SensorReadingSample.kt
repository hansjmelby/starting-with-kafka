package cx.kafka102.streaming.samples.sensor

import com.example.AvgTemperature // Generert Avro-klasse
import com.example.Sensor
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import org.apache.avro.specific.SpecificRecordBase
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.*
import java.time.Duration
import java.util.*
import org.apache.kafka.streams.KeyValue;

fun main() {

    // Konfigurasjon for Kafka Streams
    val props = Properties().apply {
        put(StreamsConfig.APPLICATION_ID_CONFIG, "sensor-average-temperature-app2")
        put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
        put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081")
        put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().javaClass)
        put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde::class.java)
    }

    // Anta at du har definert et Avro-schema for Windowed<String>
    val windowedSerde2: Serde<Windowed<String>> = WindowedSerdes.timeWindowedSerdeFrom(String::class.java, Duration.ofMinutes(1).toMillis()).apply {
        configure(mapOf(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG to "http://localhost:8081"), true) // isKey = true
    }


    // Set up SerDe for Sensor Avro-serialisering
    val sensorSerde = SpecificAvroSerde<Sensor>().apply {
        configure(mapOf(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG to "http://localhost:8081"), false)
    }

    // Set up SerDe for AvgTemperature Avro-serialisering
    val avgTemperatureSerde = SpecificAvroSerde<AvgTemperature>().apply {
        configure(mapOf(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG to "http://localhost:8081"), false)
    }


    // Bygg Stream-topologien
    val builder = StreamsBuilder()
    val sensorStream: KStream<String, Sensor> = builder.stream("sensor-topic", Consumed.with(Serdes.String(), sensorSerde))

    sensorStream.groupByKey(Grouped.with(Serdes.String(), sensorSerde))
        .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(1)))
        .aggregate(
            { AvgTemperature(0, 0) }, // Initialiser aggregat
            { _, sensor, aggregator -> AvgTemperature(aggregator.count + 1, aggregator.sum + sensor.temperature) }, // Oppdater aggregat
            Materialized.with(Serdes.String(), avgTemperatureSerde) // Bruk Avro SerDe for AvgTemperature
        ).toStream()
        .mapValues { avgTemp -> (if (avgTemp.count == 0L) 0.0 else avgTemp.sum.toDouble() / avgTemp.count).toLong() }
        .to("average-temperature-topic2",Produced.with(windowedSerde2,Serdes.Long()))

/*
    // Gruppér sensorene etter serialNumber og beregn gjennomsnittstemperatur hvert  minutt
    val avgTemperaturePerSensor: KTable<Windowed<String>, AvgTemperature> = sensorStream
        .groupBy({ _, sensor -> sensor.serialNumber.toString() }, Grouped.with(Serdes.String(), sensorSerde))
        .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(1)))
        .aggregate(
            { AvgTemperature(0, 0) }, // Initialiser aggregat
            { _, sensor, aggregator -> AvgTemperature(aggregator.count + 1, aggregator.sum + sensor.temperature) }, // Oppdater aggregat
            Materialized.with(Serdes.String(), avgTemperatureSerde) // Bruk Avro SerDe for AvgTemperature
        )

    // Skriv resultatene til en output-topic for overvåking

    val windowedSerde: Serde<Windowed<String>> = WindowedSerdes.timeWindowedSerdeFrom(String::class.java, Duration.ofMinutes(1).toMillis())


    avgTemperaturePerSensor
        .toStream()
        .mapValues { avgTemp -> (if (avgTemp.count == 0L) 0.0 else avgTemp.sum.toDouble() / avgTemp.count).toLong() }
        .peek { key, value -> println("Key: $key, Value: $value") }
        .to("average-temperature-topic",Produced.with(windowedSerde2,Serdes.Long()))

*/

    // Start Kafka Streams-applikasjonen
    val streams = KafkaStreams(builder.build(), props)
    streams.start()

    // Shutdown hook for å stoppe applikasjonen riktig
    Runtime.getRuntime().addShutdownHook(Thread(streams::close))
}
