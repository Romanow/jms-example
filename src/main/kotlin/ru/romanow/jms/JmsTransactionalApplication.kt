package ru.romanow.jms

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import ru.romanow.jms.config.properties.ArtemisProperties

@SpringBootApplication
@EnableConfigurationProperties(ArtemisProperties::class)
class JmsTransactionalApplication

fun main(args: Array<String>) {
    runApplication<JmsTransactionalApplication>(*args)
}
