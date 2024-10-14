# MODULE 1 - Introduction to Kafka

## Prerequisites
You need the following to get the most out of this module
- Docker installed
- IDE of  your choise
- Lenses license (https://lenses.io/start/)
- gradle installed
- Java 20


## What we will cover in this module

* Short introduction to event driven architecture
* Kafka theory (how and why do kafka scale)
* Introduction to kafka produser
* Introduction to kafka consumer

# Coding session
1. start the broker. remember to expose the ports neeed (e.g  registry if you use this)
2. create a topic on the broker
3. create a simple kafka produser and add some messages on the topic of your choise
   * choose what type of message. pure text, AVRO, JSON ..
4. create a simple kafka consumer and retreive the messages you added to the topic of your choise
   * choose what type of message. pure text, AVRO, JSON ..
5. Discussion : Based on your topic configuration; can you scale your consumer  application ? what is your limit
6. play with different configurations for your produser. Try to produse 100.000 random messages
   * ack = 1 
   * ack = 0
7. discussion : was there any difference in time used ?. What`s the implications of using ack=0. 
8. what happens if you try to scale the consumer? how are the messages consumed? 
9. Try to put messages and read messages from a topic using serdes. Why is this a good ide/Not god idea












