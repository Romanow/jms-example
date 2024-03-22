package ru.romanow.jms.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.romanow.jms.models.UserResponse
import ru.romanow.jms.repository.UserRepository
import javax.persistence.EntityNotFoundException

@Service
class UserServiceImpl(
    private val userRepository: UserRepository
) : UserService {

    @Transactional
    override fun updateName(id: Int, name: String): UserResponse {
        val user = userRepository.findById(id)
            .orElseThrow { EntityNotFoundException("User not found by '$id'") }
        user.name = name
        return UserResponse(id = id, name = user.name!!, login = user.login!!)
    }

    @Transactional
    override fun updateLogin(id: Int, login: String): UserResponse {
        val user = userRepository.findById(id)
            .orElseThrow { EntityNotFoundException("User not found by '$id'") }
        user.login = login
        return UserResponse(id = id, name = user.name!!, login = user.login!!)
    }
}
