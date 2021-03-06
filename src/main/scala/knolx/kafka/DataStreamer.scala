package knolx.kafka

import java.util.Properties

import akka.actor.ActorSystem
import knolx.Config.{bootstrapServer, topic}
import knolx.KnolXLogger
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.util.Random

/**
  * Copyright Knoldus Software LLP. All rights reserved.
  */
object DataStreamer extends App with KnolXLogger {
  val system = ActorSystem("DataStreamer")
  val props = new Properties()
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer)
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)

  val producer = new KafkaProducer[String, String](props)

  val someWords = List("about", "above", "after", "again", "against")

  info("Streaming data into Kafka...")
  system.scheduler.schedule(0 seconds, 200 milliseconds) {
    Random.shuffle(someWords).headOption.foreach { word =>
      producer.send(new ProducerRecord[String, String](topic, word))
    }
  }
}
