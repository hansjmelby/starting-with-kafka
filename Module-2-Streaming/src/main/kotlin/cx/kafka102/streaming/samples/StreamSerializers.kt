package org.example.cx.kafka102.streaming.samples


import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serializer

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