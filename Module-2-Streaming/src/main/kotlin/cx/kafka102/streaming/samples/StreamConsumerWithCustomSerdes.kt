package org.example.cx.kafka102.streaming.samples

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Produced
import java.util.*


val gadgetsinputTopic = "gadgets_input"
val redGadgetTopic = "gadgets_red"
fun main(){
    val kvstore = simpleSampleStreamWithSerdesLENSES()

}

fun simpleSampleStreamWithSerdesLENSES() {
    val props = loadLensesConfig()
    val gadgetSerde = Serdes.serdeFrom(GadgetSerializer(), GadgetDeserializer())
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "sample-serdes-stream")

    // 2. Bygg topologien for Kafka Streams
    val builder = StreamsBuilder()

    // 3. Definer strømmen fra gadget-topic
    builder.stream(gadgetsinputTopic, Consumed.with(Serdes.String(), gadgetSerde))
        // 4. filtrer ut de røde gadgetene
        .filter { key, value -> key != null && value.color == "red" }
        // 5. skriv ut det du finner
        .peek { key, value -> println("key:$key, value:$value") }
        // 5. Skriv det behandlede bjektene til en ny topic
        .to(redGadgetTopic, Produced.with(Serdes.String(), gadgetSerde))
    // 6. Bygg topologien
    val topology = builder.build()
    // 7. Initialiser KafkaStreams med topologien og konfigurasjonen
    val streams = KafkaStreams(topology, props)
    // 8. Start Kafka Streams-applikasjonen
    streams.start()
    println("topology started. It looks like : "+topology.describe())
    // 10. Legg til en shutdown hook for å stoppe streamen riktig ved avslutning
    Runtime.getRuntime().addShutdownHook(Thread(streams::close))
}

fun loadLensesConfig(): Properties {
    val properties = Properties()
    properties.put("bootstrap.servers","127.0.0.1:9092")
    return properties

}