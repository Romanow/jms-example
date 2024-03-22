package ru.romanow.jms.domain

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

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
