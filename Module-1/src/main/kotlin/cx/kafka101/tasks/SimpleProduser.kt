package org.example.cx.kafka101.tasks



import java.util.*

fun main(args: Array<String>) {
    println("Starter basic producer")
    // 1. Kafka lag producer properties
    val props = Properties()

    // 2. opprett en instans av kafka produser


    // 3.definer shutdown hook
    Runtime.getRuntime().addShutdownHook(Thread(Runnable {
        println("Shutting down basic producer")
    }))


    for (i in 1..10) {
        //send meldingenene dine her

    }
    println("Program avsluttet")

}