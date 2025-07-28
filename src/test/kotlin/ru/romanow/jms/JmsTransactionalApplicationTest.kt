package ru.romanow.jms

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.qpid.jms.message.JmsTextMessage
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jms.annotation.JmsListener
import org.springframework.jms.core.JmsTemplate
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.shaded.org.awaitility.Awaitility.await
import ru.romanow.jms.config.ArtemisTestConfiguration
import ru.romanow.jms.config.DatabaseTestConfiguration
import ru.romanow.jms.models.TableInfoRequest
import ru.romanow.jms.utils.DROP_TABLE_REQUEST_QUEUE
import ru.romanow.jms.utils.FIND_ALL_REQUEST_QUEUE
import ru.romanow.jms.utils.RESPONSE_QUEUE
import java.time.Duration.ofMillis
import java.time.Duration.ofSeconds
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(value = [DatabaseTestConfiguration::class, ArtemisTestConfiguration::class])
class JmsTransactionalApplicationTest {
    private final val logger = LoggerFactory.getLogger(JmsTransactionalApplicationTest::class.java)

    @Autowired
    private lateinit var jmsTemplate: JmsTemplate

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val counter = AtomicInteger()

    @BeforeEach
    fun init() {
        val createTableSql = """
            CREATE TABLE "users-$REQUEST_UID" (
                id SERIAL PRIMARY KEY,
                name VARCHAR(255) NOT NULL
            );
        """.trimIndent()
        jdbcTemplate.update(createTableSql)
        jdbcTemplate.update("""INSERT INTO "users-$REQUEST_UID" (name) VALUES (?), (?)""", "Alex", "Kate")
    }

    @Test
    fun test() {
        val request = TableInfoRequest("users-$REQUEST_UID")
        logger.info("Send request to drop table '${request.tableName}' to '$DROP_TABLE_REQUEST_QUEUE'")
        jmsTemplate.convertAndSend(DROP_TABLE_REQUEST_QUEUE, objectMapper.writeValueAsString(request))

        Thread.sleep(100)

        logger.info("Send request to find all names from table '${request.tableName}' to '$FIND_ALL_REQUEST_QUEUE'")
        jmsTemplate.convertAndSend(FIND_ALL_REQUEST_QUEUE, objectMapper.writeValueAsString(request))

        logger.info("Waiting for response...")
        await().atMost(ofSeconds(2)).pollDelay(ofMillis(100)).until { counter.get() == 2 }
    }

    @JmsListener(destination = RESPONSE_QUEUE)
    fun outListener(@Payload message: JmsTextMessage) {
        logger.info("Received message '${message.text}' from '$RESPONSE_QUEUE'")
        counter.incrementAndGet()
    }

    companion object {
        private val REQUEST_UID = UUID.randomUUID()
    }
}
