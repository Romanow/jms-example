package ru.romanow.jms.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.romanow.jms.domain.User

interface UserRepository : JpaRepository<User, Int>
