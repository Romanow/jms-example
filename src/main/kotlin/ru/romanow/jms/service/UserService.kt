package ru.romanow.jms.service

import ru.romanow.jms.models.UserResponse

interface UserService {
    fun updateName(id: Int, name: String): UserResponse
    fun updateLogin(id: Int, login: String): UserResponse
}
