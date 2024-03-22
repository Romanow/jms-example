package ru.romanow.jms.config

import com.zaxxer.hikari.HikariDataSource
import org.postgresql.Driver
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Primary
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.output.Slf4jLogConsumer

typealias CustomPostgresContainer = PostgreSQLContainer<*>

@TestConfiguration
class DatabaseTestConfiguration {
    private val logger = LoggerFactory.getLogger(DatabaseTestConfiguration::class.java)

    @Bean
    fun postgres(): PostgreSQLContainer<*> {
        val postgres = CustomPostgresContainer(POSTGRES_IMAGE)
            .withUsername(USERNAME)
            .withPassword(PASSWORD)
            .withDatabaseName(DATABASE_NAME)
            .withLogConsumer(Slf4jLogConsumer(logger))
        postgres.start()
        return postgres
    }

    @Primary
    @DependsOn("postgres")
    @Bean(destroyMethod = "close")
    fun dataSource(): HikariDataSource {
        val dataSource = HikariDataSource()
        dataSource.jdbcUrl = postgres().jdbcUrl
        dataSource.username = USERNAME
        dataSource.password = PASSWORD
        dataSource.driverClassName = Driver::class.java.canonicalName
        return dataSource
    }

    companion object {
        private const val POSTGRES_IMAGE = "postgres:15"
        private const val DATABASE_NAME = "jms"
        private const val USERNAME = "program"
        private const val PASSWORD = "test"
    }
}
