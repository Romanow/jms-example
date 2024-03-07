package ru.romanow.jms.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "application.artemis")
data class ArtemisProperties(
    var username: String? = null,
    var password: String? = null,
    var brokerUrl: String? = null
)
