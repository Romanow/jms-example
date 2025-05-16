package ru.romanow.jms

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.context.annotation.Import
import org.springframework.jms.annotation.JmsListener
import org.springframework.jms.core.JmsTemplate
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.shaded.org.awaitility.Awaitility.await
import ru.romanow.jms.config.ArtemisTestConfiguration
import ru.romanow.jms.config.DatabaseTestConfiguration
import ru.romanow.jms.domain.User
import ru.romanow.jms.models.UserChangeRequest
import ru.romanow.jms.repository.UserRepository
import ru.romanow.jms.utils.CHANGE_LOGIN_REQUEST_QUEUE
import ru.romanow.jms.utils.CHANGE_NAME_REQUEST_QUEUE
import ru.romanow.jms.utils.RESPONSE_QUEUE
import java.time.Duration.ofMillis
import java.time.Duration.ofSeconds
import java.util.concurrent.atomic.AtomicInteger

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(value = [DatabaseTestConfiguration::class, ArtemisTestConfiguration::class])
class JmsTransactionalApplicationTest {
    private final val logger = LoggerFactory.getLogger(JmsTransactionalApplicationTest::class.java)

    @Autowired
    private lateinit var jmsTemplate: JmsTemplate

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val counter = AtomicInteger()

    @BeforeEach
    fun init() {
        userRepository.save(User(id = USER_ID, name = "Alex", login = "romanow"))
    }

    @Test
    fun test() {
        var request = UserChangeRequest(id = USER_ID, value = "Alexey")
        logger.info("Send request to change name to '${request.value}' to '$CHANGE_NAME_REQUEST_QUEUE'")
        jmsTemplate.convertAndSend(CHANGE_NAME_REQUEST_QUEUE, objectMapper.writeValueAsString(request))

        await().atMost(ofSeconds(2)).pollDelay(ofMillis(100)).until { counter.get() == 1 }

        request = UserChangeRequest(id = USER_ID, value = "ronin")
        logger.info("Send request to change login to '${request.value}' to '$CHANGE_LOGIN_REQUEST_QUEUE'")
        jmsTemplate.convertAndSend(CHANGE_LOGIN_REQUEST_QUEUE, objectMapper.writeValueAsString(request))

        logger.info("Waiting for response...")
        await().atMost(ofSeconds(2)).pollDelay(ofMillis(100)).until { counter.get() == 2 }

        val user = userRepository.findById(USER_ID)
        assertThat(user)
            .isPresent
            .get()
            .extracting("name", "login")
            .contains("Alexey", "ronin")
    }

    @JmsListener(destination = RESPONSE_QUEUE)
    fun outListener(@Payload message: String) {
        logger.info("Received message '$message' from '$RESPONSE_QUEUE'")
        counter.incrementAndGet()
    }

    companion object {
        private const val USER_ID = 1
    }
}
