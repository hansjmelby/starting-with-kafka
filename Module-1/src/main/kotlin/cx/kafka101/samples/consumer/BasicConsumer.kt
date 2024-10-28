package cx.kafka101.samples.consumer

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.errors.WakeupException
import org.apache.kafka.common.serialization.StringDeserializer
import java.time.Duration
import java.util.Properties

fun main() {
    val bootstrapServers = "localhost:9092" // Kafka broker URL
    val topic = "test"                // Replace with your topic

    // Kafka Consumer configuration settings
    val props = Properties().apply {
        put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
        put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        put(ConsumerConfig.GROUP_ID_CONFIG, "my-consumer-group") // Consumer group ID
        put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest") // Start from the earliest message
        // Enable auto commit (true by default)
        put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,"true")
        // Set the auto commit interval to 1 second,(default is 5000 ms)
        put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,"1000")
    }

    // Create Kafka consumer
    val consumer = KafkaConsumer<String, String>(props)

    Runtime.getRuntime().addShutdownHook(Thread(Runnable {
        // Close the consumer gracefully (this part won't be reached in an infinite loop)
        println("shutting down...")
        consumer.wakeup()
    }))

    // Subscribe to the topic
    consumer.subscribe(listOf(topic))

    // Poll the broker for new data
    try {
        while (true) {
            try {
                val records = consumer.poll(Duration.ofMillis(1000)) // Poll with a timeout of 1 second
                println("Records received : ${records.count()}")
                for (record in records) {
                    println("Received message: key=${record.key()}, value=${record.value()}, partition=${record.partition()}, offset=${record.offset()}")
                }
            } catch (e: WakeupException) {
                println("Received shutdown signal.")
                break
            }
        }
    }
    finally {
        // Ensure that the consumer is properly closed in case of errors
        consumer.close()
        println("Consumer closed.")
    }


}
