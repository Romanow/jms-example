package ru.romanow.jms.config

import org.apache.qpid.jms.JmsConnectionFactory
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.jms.annotation.EnableJms
import org.springframework.jms.config.DefaultJmsListenerContainerFactory
import org.springframework.jms.config.JmsListenerContainerFactory
import org.springframework.jms.connection.CachingConnectionFactory
import org.springframework.jms.core.JmsTemplate
import org.springframework.jms.listener.DefaultMessageListenerContainer
import ru.romanow.jms.config.properties.ArtemisProperties

@EnableJms
@Configuration
class JmsConfiguration {
    private final val logger = LoggerFactory.getLogger(JmsConfiguration::class.java)

    @Bean
    @ConditionalOnMissingBean(CachingConnectionFactory::class)
    fun jmsConnectionFactory(artemisProperties: ArtemisProperties): CachingConnectionFactory {
        val connectionFactory = JmsConnectionFactory(
            artemisProperties.username,
            artemisProperties.password,
            artemisProperties.brokerUrl
        )
        return CachingConnectionFactory(connectionFactory)
    }

    @Bean
    @Primary
    fun artemisJmsTemplate(connectionFactory: CachingConnectionFactory) = JmsTemplate(connectionFactory)

    @Bean
    fun jmsListenerContainerFactory(
        connectionFactory: CachingConnectionFactory,
        configurer: DefaultJmsListenerContainerFactoryConfigurer
    ): JmsListenerContainerFactory<DefaultMessageListenerContainer> {
        val listenerContainerFactory = DefaultJmsListenerContainerFactory()
        configurer.configure(listenerContainerFactory, connectionFactory)
        listenerContainerFactory.setErrorHandler { logger.error("", it) }
        return listenerContainerFactory
    }
}
