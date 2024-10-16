package cx.kafka101.samples;

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.time.delay
import mu.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import java.time.Duration
import java.util.*


fun loadConfig(configFile:String): Properties {
    val properties = Properties()
    properties.load(ClassLoader.getSystemResourceAsStream(configFile))
    return properties

}
fun loadLensesConfig(): Properties {
    val properties = Properties()
    properties.put("bootstrap.servers","127.0.0.1:9092")
    return properties

}

class GenericKafkaConsumer(val file:String, val topics:List<String>, val service: IHandleRecords, val consumerID:String){
    val consumer = createConsumer(consumerID)
    private val logger = KotlinLogging.logger { }
    init {
        consumer.subscribe(topics)
    }

    fun pollMessages(): List<KafkaMessage> = //listOf("Message A","Message B","Message C")

        consumer.poll(Duration.ofSeconds(4))
            .map { KafkaMessage(it.partition(),
                it.offset(),
                it.value(),
                it.key(),
                it.topic(),
                it.value())
            }


    fun flow(): Flow<List<KafkaMessage>> =
        kotlinx.coroutines.flow.flow<List<KafkaMessage>> {
            while (true) {
                emit(pollMessages())
                delay(Duration.ofSeconds(5))
            }
        }.onEach {
            logger.info { "received  :${it.size} messages for $consumerID" }
            it.forEach { record -> service.handle(record) }
        }.onEach {
            consumer.commitAsync()
        }

    fun createConsumer(consumerID:String): KafkaConsumer<String, String> {
        val props = loadConfig("client.properties")
        props[ConsumerConfig.GROUP_ID_CONFIG] = consumerID
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        return KafkaConsumer(props)
    }
}