package cx.kafka101.samples



interface IHandleRecords {
    fun handle(record: KafkaMessage)

}