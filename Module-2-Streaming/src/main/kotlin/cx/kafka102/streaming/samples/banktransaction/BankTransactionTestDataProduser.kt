package org.example.cx.kafka102.streaming.samples.banktransaction



import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.LongSerializer
import org.example.cx.kafka102.streaming.samples.BankTransaction
import org.example.cx.kafka102.streaming.samples.BankTransactionSerializer
import java.time.LocalDate
import java.util.Properties
import kotlin.collections.set

fun main(){
    val bankTransactionProduser = createLensesExactlyOnceProducerBankTransaction()

    while (true) {

        val transaction = BankTransaction(
            accountNumber = (1..10).random().toLong(),
            sum = (-100..100).random().toLong(),
            count = 1,
            time = LocalDate.now().toString()
        )
        sendMessageOfTypeBankTransaction(bankTransactionProduser,"bank-transactions",transaction.accountNumber,transaction)
        println("bankTransaction created")
        Thread.sleep(10000)
    }
}
fun createLensesExactlyOnceProducerBankTransaction(): KafkaProducer<Long, BankTransaction> {
    val props = Properties()
    props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "127.0.0.1:9092"
    props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = LongSerializer::class.java
    props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = BankTransactionSerializer::class.java

    props[ProducerConfig.ACKS_CONFIG] = "all" //the strongest producing guaranties
    props[ProducerConfig.RETRIES_CONFIG] = "3"
    props[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = "true" //ensure we dont push duplikates


    props[ProducerConfig.CLIENT_ID_CONFIG] = "bankTransactionProduser"
    return KafkaProducer(props)
}
fun sendMessageOfTypeBankTransaction(producer:KafkaProducer<Long,BankTransaction>, topic:String, key:Long, value:BankTransaction){
    val futureResult = producer.send(
        ProducerRecord(
            topic,
            key, value
        )
    )
    val v = futureResult.get()


}