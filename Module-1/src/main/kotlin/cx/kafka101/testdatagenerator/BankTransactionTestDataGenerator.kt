package org.example.cx.kafka101.testdatagenerator



import com.example.avro.BankTransaction
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import java.util.*

fun main() {
    val topic = "bank-transactions-avro"

    val props = Properties().apply {
        put("bootstrap.servers", "localhost:9092")
        put("key.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer")
        put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer")
        put("schema.registry.url", "http://localhost:8081")
    }

    val producer = KafkaProducer<Any, Any>(props)


    while (true) {
        val transaction = generateTransaction()
        val record = ProducerRecord<Any, Any>(topic, transaction.accountId, transaction)
        producer.send(record)
        println("Sent Avro transaction: ${transaction.accountId}, ${transaction.amount}")
        Thread.sleep(1000)
    }
}



fun generateTransaction(): BankTransaction {
    val user = "User_${(1..10).random()}"
    val amount = (100..15000).random().toDouble()
    val type = if ((0..1).random() == 0) "deposit" else "withdrawal"
    val now = System.currentTimeMillis()
    return BankTransaction(user, amount, now, type)
}
