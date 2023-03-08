package com.fynnian.dev_meeting.jooqdevmeeting.script

import com.fynnian.dev_meeting.jooq.kotlin.tables.references.AUTHOR
import com.fynnian.dev_meeting.jooq.kotlin.tables.references.BOOK
import com.fynnian.dev_meeting.jooq.kotlin.tables.references.LANGUAGE
import org.jooq.DSLContext
import org.jooq.Record5
import org.jooq.Result
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.jooq.impl.DSL.asterisk
import java.sql.DriverManager


fun <T> jooq(statement: DSLContext.() -> T): T {
    DriverManager.getConnection(
        "jdbc:postgresql://localhost:7878/dev_meeting",
        "dev_meeting",
        "dev_meeting"
    ).use { con ->
        return DSL.using(con, SQLDialect.POSTGRES).statement()
            .also { println(it.toString()) }
    }

}

jooq {
    select(asterisk())
        .from(BOOK)
        .join(AUTHOR).on(AUTHOR.ID.eq(BOOK.AUTHOR_ID))
        .join(LANGUAGE).on(LANGUAGE.ID.eq(BOOK.LANGUAGE_ID))
        .limit(1)
        .fetch {
            Triple(
                it.into(BOOK),
                it.into(LANGUAGE),
                it.into(AUTHOR)
            )
        }
}

val lookup: Result<Record5<Int?, String?, String?, String?, String?>> = jooq {
    select(
        BOOK.ID,
        BOOK.TITLE,
        BOOK.author().FIRST_NAME,
        BOOK.author().LAST_NAME,
        BOOK.language().CD
    )
        .from(BOOK)
        .fetch()
}
