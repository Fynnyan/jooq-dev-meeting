package com.fynnian.dev_meeting.jooqdevmeeting.script

import com.fynnian.dev_meeting.jooq.kotlin.tables.records.UsersRecord
import com.fynnian.dev_meeting.jooq.kotlin.tables.references.USERS
import com.fynnian.dev_meeting.jooqdevmeeting.User
import org.jooq.Records
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import java.sql.DriverManager
import java.util.*


//fun <T> jooq(statement: DSLContext.() -> T): T {
//    DriverManager.getConnection(
//        "jdbc:postgresql://localhost:7878/dev_meeting",
//        "dev_meeting",
//        "dev_meeting"
//    ).use { con -> return DSL.using(con, SQLDialect.POSTGRES).statement() }
//}

val jooq = DSL.using(
    DriverManager.getConnection(
        "jdbc:postgresql://localhost:7878/dev_meeting",
        "dev_meeting",
        "dev_meeting"
    ), SQLDialect.POSTGRES
)

// get record of a user, user it to update it,
val users = jooq.selectFrom(USERS).fetchAny()
users?.apply {
    lastName = "Blobbington"
    firstName = "Blobert"
    store()
    refresh() // used when there is some DB generated values like updated_at
}

// use records for partial updates
val userId = UUID.fromString("8b3db797-5857-4a82-8498-0782ff79a5f1")

val updatedUser = UsersRecord().apply {
    id = userId
    email = "changed.user@address.com"
}

jooq.executeUpdate(updatedUser)

// new record, attached to a jooq session
val newUser = jooq.newRecord(USERS).apply {
    id = UUID.randomUUID()
    email = "new.email@address.com"
}

newUser.store()

// or via one of the execute CRUD functions
val newUser2 = UsersRecord().apply {
    id = UUID.randomUUID()
    email = "new.email-user2@address.com"
}
jooq.executeInsert(newUser2)

// changed flags
val userFlags = jooq.selectFrom(USERS).where(USERS.ID.eq(userId)).fetchOne()!!


println("name: ${userFlags.firstName} - changed: ${userFlags.changed()}")
userFlags.firstName = "New name"
userFlags.lastName = "changed"
println("name: ${userFlags.firstName} - changed: ${userFlags.changed()}")

userFlags.changed(USERS.FIRST_NAME, false)
// wa also changed the last name, so to get the status for the field, add the field to the method
// changed() would return true as a whole the record is still changed
println("name: ${userFlags.firstName} - changed: ${userFlags.changed(USERS.FIRST_NAME)}")

jooq.update(USERS)
    .set(userFlags)
    .where(USERS.ID.eq(userFlags.id))
    .returning()
    .fetch()
    .also { println(it) }


// fetch records and transformation via constructor mapping
jooq.selectFrom(USERS)
    .fetch(Records.mapping(::User))
    .also { println(it) }


