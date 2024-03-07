package ru.romanow.jms.service

import org.slf4j.LoggerFactory
import org.springframework.jms.annotation.JmsListener
import org.springframework.jms.core.JmsTemplate
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service

@Service
class MessageService(
    private val jmsTemplate: JmsTemplate
) {
    private final val logger = LoggerFactory.getLogger(MessageService::class.java)

    @JmsListener(destination = "queues.in")
    fun inListener(@Payload message: String) {
        logger.info("Receive message '$message' from 'queues.in'")
        jmsTemplate.convertAndSend("queues.out", message)
        logger.info("Send echo message '$message' to 'queues.out'")

        logger.info("Sleep for 3 seconds")
        Thread.sleep(3000)
        logger.info("Finished")
    }
}
