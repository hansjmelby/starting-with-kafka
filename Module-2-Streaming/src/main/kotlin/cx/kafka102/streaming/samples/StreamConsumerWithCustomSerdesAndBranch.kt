package org.example.cx.kafka102.streaming.samples

import cx.kafka102.streaming.samples.Gadget
import cx.kafka102.streaming.samples.GadgetDeserializer
import cx.kafka102.streaming.samples.GadgetSerializer
import cx.kafka102.streaming.samples.StringDeserializer
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.Branched
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.Named
import org.apache.kafka.streams.kstream.Predicate
import org.apache.kafka.streams.kstream.Produced
import java.util.*


val gadgetsinputTopic = "JsonGadgetTopic"
val redGadgetTopic = "gadgets_red"
fun main(){
    val kvstore = simpleSampleStreamWithSerdesLENSES()

}

fun simpleSampleStreamWithSerdesLENSES() {
    val props = loadLensesConfig()
    val gadgetSerde = Serdes.serdeFrom(GadgetSerializer(), GadgetDeserializer())
    val keySerde = Serdes.serdeFrom(StringSerializer(), StringDeserializer())
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "sample-serdes-streamWithBranching7")

    // 2. Bygg topologien for Kafka Streams
    val builder = StreamsBuilder()

    // 3. Definer strømmen fra gadget-topic
    val gadgets = builder.stream(gadgetsinputTopic, Consumed.with(Serdes.String(), gadgetSerde))

    val branches = gadgets.split(Named.`as`("Splitt"))
        .branch({ key, value -> value.color == "RED" }, Branched.withConsumer { ks -> ks.to("red-topic") })
        .branch({ key, value -> value.color == "BLUE" }, Branched.withConsumer { ks -> ks.to("blue-topic") })
        .defaultBranch(Branched.withConsumer { ks -> ks.to("other-topic") })

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
class test: Predicate<String, Gadget> {
    override fun test(p0: String?, p1: Gadget?): Boolean {
        val match = p1!!.color == "RED"
        if (match)
            println("red match for $p1")
        return match
    }
}



