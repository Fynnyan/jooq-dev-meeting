package com.fynnian.dev_meeting.jooqdevmeeting

import com.fynnian.dev_meeting.jooq.kotlin.tables.records.UsersRecord
import com.fynnian.dev_meeting.jooq.kotlin.tables.references.USERS
import org.jooq.DSLContext
import org.jooq.impl.DSL.trueCondition
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*


@RestController
class UserController(private val jooq: DSLContext) {

    @GetMapping("/users")
    fun getUserBy(@RequestParam email: String, @RequestParam name: String): List<UsersRecord> {
        return jooq
            .selectFrom(USERS) // jooq shorthand for select all(*) from users
            .where(USERS.EMAIL.eq(name))
            .and(USERS.FIRST_NAME.eq(name))
            .fetch()
            // or with mapping to domain
            //.fetch { User(it[USERS.ID], it[USERS.FIRST_NAME], it[USERS.LAST_NAME], it[USERS.EMAIL]) }
    }

    @GetMapping("/users/{id}")
    fun getUsers(@PathVariable id: UUID? = null): List<UsersRecord> {
        return jooq
            .select(USERS.asterisk())
            .from(USERS)
            .where(trueCondition())
            .let { if (id != null) it.and(USERS.ID.eq(id)) else it }
            .fetchInto(USERS)
    }

}

data class User(
    val id: UUID?,
    val firstName: String?,
    val lastName: String?,
    val email: String?
)
