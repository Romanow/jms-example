package ru.romanow.jms.service

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserServiceImpl(private val jdbcTemplate: JdbcTemplate) : UserService {

    @Transactional
    override fun dropTable(tableName: String) {
        jdbcTemplate.update("""DROP TABLE "$tableName" """)
        Thread.sleep(1000)
    }

    @Transactional(readOnly = true)
    override fun findAll(tableName: String): Set<String> {
        val names = jdbcTemplate.query("""SELECT name FROM "$tableName" """) { rs, _ -> rs.getString("name") }
        Thread.sleep(1000)
        return names.toSet()
    }
}
