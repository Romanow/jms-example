package ru.romanow.jms.service

interface UserService {
    fun dropTable(tableName: String)
    fun findAll(tableName: String): Set<String>
}
