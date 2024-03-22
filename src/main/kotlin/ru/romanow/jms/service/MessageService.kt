package ru.romanow.jms.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.qpid.jms.message.JmsTextMessage
import org.slf4j.LoggerFactory
import org.springframework.jms.annotation.JmsListener
import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Service
import ru.romanow.jms.models.UserChangeRequest
import ru.romanow.jms.utils.CHANGE_LOGIN_REQUEST_QUEUE
import ru.romanow.jms.utils.CHANGE_NAME_REQUEST_QUEUE
import ru.romanow.jms.utils.RESPONSE_QUEUE
import javax.jms.Session

@Service
class MessageService(
    private val userService: UserService,
    private val jmsTemplate: JmsTemplate,
    private val objectMapper: ObjectMapper
) {
    private final val logger = LoggerFactory.getLogger(MessageService::class.java)

    @JmsListener(destination = CHANGE_NAME_REQUEST_QUEUE)
    fun changeNameListener(message: JmsTextMessage, session: Session) {
        logger.info("Receive message '$message' from '$CHANGE_NAME_REQUEST_QUEUE'")
        val request = objectMapper.readValue(message.text, UserChangeRequest::class.java)

        userService.updateName(request.id, request.value)

        jmsTemplate.convertAndSend(RESPONSE_QUEUE, "success")
        logger.info("Send echo message '$message' to '$RESPONSE_QUEUE'")
    }

    @JmsListener(destination = CHANGE_LOGIN_REQUEST_QUEUE)
    fun changeLoginListener(message: JmsTextMessage, session: Session) {
        logger.info("Receive message '$message' from '$CHANGE_LOGIN_REQUEST_QUEUE'")
        val request = objectMapper.readValue(message.text, UserChangeRequest::class.java)

        userService.updateLogin(request.id, request.value)

        jmsTemplate.convertAndSend(RESPONSE_QUEUE, "success")
        logger.info("Send echo message '$message' to '$RESPONSE_QUEUE'")
    }
}
