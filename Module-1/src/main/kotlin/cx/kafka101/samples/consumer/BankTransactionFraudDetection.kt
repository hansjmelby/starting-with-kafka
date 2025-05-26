package org.example.cx.kafka101.samples.consumer

import com.example.avro.BankTransaction
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import java.time.Duration
import java.util.*

fun main() {
    val props = Properties().apply {
        put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
        put(ConsumerConfig.GROUP_ID_CONFIG, "fraud-detector")
        put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroDeserializer")
        put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroDeserializer")
        put("schema.registry.url", "http://localhost:8081")
        put("specific.avro.reader", "true") // Viktig for å få BankTransaction-objekter
        put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    }

    val consumer = KafkaConsumer<String, BankTransaction>(props)
    val topic = "bank-transactions-avro"
    consumer.subscribe(listOf(topic))

    val recentWithdrawals = mutableMapOf<String, MutableList<Long>>() // konto → tidsstempler

    while (true) {
        val records = consumer.poll(Duration.ofMillis(500))
        for (record in records) {
            val tx = record.value()
            val account = tx.accountId
            val amount = tx.amount
            val type = tx.type
            val timestamp = tx.time

            // Svindelregel 1: Transaksjon > 10 000
            if (amount > 10_000) {
                println("🚨 HIGH VALUE FRAUD: $account withdrew/deposited $amount")
            }

            // Svindelregel 2: Flere raske uttak (<10 sekunder)
            if (type == "withdrawal") {
                val history = recentWithdrawals.getOrPut(account) { mutableListOf() }
                history.add(timestamp)
                history.retainAll { it > timestamp - 10_000 } // bare siste 10 sekunder

                if (history.size >= 3) {
                    println("🚨 RAPID WITHDRAWALS: $account has ${history.size} in 10 seconds")
                }
            }
        }
    }
}
