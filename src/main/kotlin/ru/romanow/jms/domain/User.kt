package ru.romanow.jms.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
data class User(

    @Id
    var id: Int? = null,

    @Column(name = "name", nullable = false)
    var name: String? = null,

    @Column(name = "login", nullable = false)
    var login: String? = null,
)
