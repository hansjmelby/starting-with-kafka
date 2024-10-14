package cx.kafka101.samples;



import mu.KotlinLogging

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord

import java.util.*


fun loadProduceConfig(configFile:String): Properties {
    val properties = Properties()
    properties.load(ClassLoader.getSystemResourceAsStream(configFile))
    return properties

}

class KafkaMessageProducer(val file:String,val clientID:String){
    val producer = createProducer(file,clientID)
    private val logger = KotlinLogging.logger { }


    fun createProducer(file:String = "client.properties",clientID: String): KafkaProducer<String, String> {
        val props = loadProduceConfig(file)
        props[ProducerConfig.CLIENT_ID_CONFIG] = clientID

        return KafkaProducer(props)
    }
    fun sendMessageOfTypeTekst(topic:String,key:String,value:String){
        val futureResult = producer.send(
            ProducerRecord(
                topic,
                key, value
            )
        )
        val v = futureResult.get()
        logger.info { "message sent to topic $topic" }

    }
}