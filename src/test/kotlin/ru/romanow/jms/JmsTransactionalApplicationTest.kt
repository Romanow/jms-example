package ru.romanow.jms

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.jms.annotation.JmsListener
import org.springframework.jms.core.JmsTemplate
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.shaded.org.awaitility.Awaitility.await
import ru.romanow.jms.config.ArtemisTestConfiguration
import java.time.Duration.ofSeconds
import java.util.UUID
import java.util.concurrent.atomic.AtomicReference

@ActiveProfiles("test")
@SpringBootTest
@Import(ArtemisTestConfiguration::class)
class JmsTransactionalApplicationTest {
    private final val logger = LoggerFactory.getLogger(JmsTransactionalApplicationTest::class.java)

    @Autowired
    private lateinit var jmsTemplate: JmsTemplate

    private val holder = AtomicReference<String>()

    @Test
    fun test() {
        val payload = UUID.randomUUID().toString()

        logger.info("Send request '$payload' to 'queues.in'")
        jmsTemplate.convertAndSend("queues.in", payload)

        logger.info("Waiting for response")
        await().atMost(ofSeconds(2)).pollDelay(ofSeconds(1)).until { holder.get() != null }

        assertThat(holder.get()).isEqualTo(payload)
    }

    @JmsListener(destination = "queues.out")
    fun outListener(@Payload message: String) {
        logger.info("Received message '$message' from 'queues.out'")
        holder.set(message)
    }
}
