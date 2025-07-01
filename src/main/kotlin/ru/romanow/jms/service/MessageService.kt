package ru.romanow.jms.service

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.jms.Session
import org.apache.qpid.jms.message.JmsTextMessage
import org.slf4j.LoggerFactory
import org.springframework.jms.annotation.JmsListener
import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Service
import ru.romanow.jms.models.TableInfoRequest
import ru.romanow.jms.utils.DROP_TABLE_REQUEST_QUEUE
import ru.romanow.jms.utils.FIND_ALL_REQUEST_QUEUE
import ru.romanow.jms.utils.RESPONSE_QUEUE

@Service
class MessageService(
    private val userService: UserService,
    private val jmsTemplate: JmsTemplate,
    private val objectMapper: ObjectMapper
) {
    private final val logger = LoggerFactory.getLogger(MessageService::class.java)

    @JmsListener(destination = DROP_TABLE_REQUEST_QUEUE)
    fun dropTable(message: JmsTextMessage, session: Session) {
        logger.info("Receive message '$message' from '$DROP_TABLE_REQUEST_QUEUE'")
        val request = objectMapper.readValue(message.text, TableInfoRequest::class.java)

        userService.dropTable(request.tableName)

        jmsTemplate.convertAndSend(RESPONSE_QUEUE, "success")
        logger.info("Send echo message '$message' to '$RESPONSE_QUEUE'")
    }

    @JmsListener(destination = FIND_ALL_REQUEST_QUEUE)
    fun changeLoginListener(message: JmsTextMessage, session: Session) {
        logger.info("Receive message '$message' from '$FIND_ALL_REQUEST_QUEUE'")
        val request = objectMapper.readValue(message.text, TableInfoRequest::class.java)

        val response = userService.findAll(request.tableName).toString()

        jmsTemplate.convertAndSend(RESPONSE_QUEUE, response)
        logger.info("Send echo message '$message' to '$RESPONSE_QUEUE'")
    }
}
