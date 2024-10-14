package cx.kafka101.samples


data class KafkaMessage(val partition:Int,val offset:Long,val value : String, val key:String,val topic:String,val json  :String)
data class Gadget(val color:String,val id:String,val temp:Int)