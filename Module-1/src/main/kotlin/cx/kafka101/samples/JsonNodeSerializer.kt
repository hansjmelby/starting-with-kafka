package cx.workshop.messageoriented.cx.kafka101.samples

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.serialization.Serializer

class JsonNodeSerializer : Serializer<JsonNode> {

    private val objectMapper = ObjectMapper()

    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {
        // No configuration needed
    }

    override fun serialize(topic: String?, data: JsonNode?): ByteArray? {
        return try {
            objectMapper.writeValueAsBytes(data)
        } catch (e: Exception) {
            throw RuntimeException("Error serializing JsonNode", e)
        }
    }

    override fun close() {
        // Nothing to close
    }
}
