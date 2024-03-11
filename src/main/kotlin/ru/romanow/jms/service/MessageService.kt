package ru.romanow.jms.service

import org.apache.activemq.artemis.jms.client.ActiveMQQueue
import org.apache.qpid.jms.message.JmsTextMessage
import org.slf4j.LoggerFactory
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Service
import javax.jms.Session

@Service
class MessageService {
    private final val logger = LoggerFactory.getLogger(MessageService::class.java)

    @JmsListener(destination = "queues.in")
    fun inListener(message: JmsTextMessage, session: Session) {
        logger.info("Receive message '$message' from 'queues.in'")
        session
            .createProducer(ActiveMQQueue("queues.out"))
            .send(session.createTextMessage(message.text))
        logger.info("Send echo message '$message' to 'queues.out'")
    }
}
