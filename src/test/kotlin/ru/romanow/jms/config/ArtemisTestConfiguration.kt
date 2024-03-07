package ru.romanow.jms.config

import org.apache.qpid.jms.JmsConnectionFactory
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.jms.connection.CachingConnectionFactory
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait

typealias ArtemisContainer = GenericContainer<*>

@TestConfiguration
class ArtemisTestConfiguration {
    private val logger = LoggerFactory.getLogger(ArtemisTestConfiguration::class.java)

    @Bean
    fun artemis(): GenericContainer<*> {
        val artemis = ArtemisContainer(ARTEMIS_IMAGE)
            .withEnv("ARTEMIS_USER", ARTEMIS_USER)
            .withEnv("ARTEMIS_PASSWORD", ARTEMIS_PASSWORD)
            .withExposedPorts(ARTEMIS_PORT)
            .withLogConsumer(Slf4jLogConsumer(logger))
            .waitingFor(Wait.forListeningPort())
        artemis.start()
        return artemis
    }

    @Bean
    @Primary
    fun jmsConnectionFactory(): CachingConnectionFactory {
        val connectionFactory = JmsConnectionFactory(
            ARTEMIS_USER,
            ARTEMIS_PASSWORD,
            "amqp://localhost:${artemis().getMappedPort(ARTEMIS_PORT)}"
        )
        return CachingConnectionFactory(connectionFactory)
    }

    companion object {
        private const val ARTEMIS_IMAGE = "romanowalex/artemis:2.19.0"
        private const val ARTEMIS_USER = "artemis"
        private const val ARTEMIS_PASSWORD = "artemis"
        private const val ARTEMIS_PORT = 61616
    }
}
