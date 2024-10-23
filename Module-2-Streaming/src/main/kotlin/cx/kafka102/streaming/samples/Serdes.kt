package org.example.cx.kafka102.streaming.samples
import org.example.cx.kafka102.streaming.samples.Gadget
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serializer

import java.nio.ByteBuffer

class GadgetSerializer : Serializer<Gadget> {
    override fun serialize(topic: String?, data: Gadget?): ByteArray? {
        if (data == null) return null
        return jsonMapper.writeValueAsBytes(data)
    }

}

class GadgetDeserializer : Deserializer<Gadget> {
    override fun deserialize(topic: String?, data: ByteArray?): Gadget? {
        if (data == null) return null
        return jsonMapper.readValue(data, Gadget::class.java)
    }

}


// Custom Serializer and Deserializer for PurchaseEvent (for simplicity)
class PurchaseEventSerializer : Serializer<PurchaseEvent> {
    override fun serialize(topic: String?, data: PurchaseEvent?): ByteArray? {
        return data?.let { "${it.category},${it.amount}".toByteArray() }
    }
}

class PurchaseEventDeserializer : Deserializer<PurchaseEvent> {
    override fun deserialize(topic: String?, data: ByteArray?): PurchaseEvent? {
        return data?.let {
            val fields = String(it).split(",")
            PurchaseEvent(fields[0], fields[1].toDouble())
        }
    }
}

class CountAndSumSerializer : Serializer<CountAndSum> {
    override fun serialize(topic: String?, data: CountAndSum?): ByteArray? {
        if (data == null) {
            return null
        }
        val buffer = ByteBuffer.allocate(16) // sum and count are Long (8 bytes each)
        buffer.putLong(data.sum)
        buffer.putLong(data.count)
        return buffer.array()
    }
}

class CountAndSumSerde : Serde<CountAndSum> {
    private val serializer = CountAndSumSerializer()
    private val deserializer = CountAndSumDeserializer()

    override fun serializer(): Serializer<CountAndSum> = serializer

    override fun deserializer(): Deserializer<CountAndSum> = deserializer
}
class CountAndSumDeserializer : Deserializer<CountAndSum> {
    override fun deserialize(topic: String?, data: ByteArray?): CountAndSum? {
        if (data == null || data.isEmpty()) {
            return null
        }
        val buffer = ByteBuffer.wrap(data)
        val sum = buffer.long
        val count = buffer.long
        return CountAndSum(sum, count)
    }
}

class BankTransactionSerializer : Serializer<BankTransaction> {
    override fun serialize(topic: String?, data: BankTransaction?): ByteArray? {
        if (data == null) return null
        return jsonMapper.writeValueAsBytes(data)
    }

}
class BankTransactionDeserializer : Deserializer<BankTransaction> {
    override fun deserialize(topic: String?, data: ByteArray?): BankTransaction? {
        if (data == null) return null
        return jsonMapper.readValue(data, BankTransaction::class.java)
    }

}

class BankTransactionSumSerde : Serde<BankTransaction> {
    private val serializer = BankTransactionSerializer()
    private val deserializer = BankTransactionDeserializer()

    override fun serializer(): Serializer<BankTransaction> = serializer

    override fun deserializer(): Deserializer<BankTransaction> = deserializer
}
