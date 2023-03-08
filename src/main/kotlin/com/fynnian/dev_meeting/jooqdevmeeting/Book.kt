package com.fynnian.dev_meeting.jooqdevmeeting

import com.fynnian.dev_meeting.jooq.kotlin.tables.records.BookRecord
import com.fynnian.dev_meeting.jooq.kotlin.tables.references.BOOK
import org.jooq.DSLContext
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class BookController(
    private val jooq: DSLContext
) {

    @GetMapping("/books")
    fun getBooks(): List<BookResponse> {
        return jooq
            .selectFrom(BOOK)
            .fetch { it.toResponse() }
    }

}

fun BookRecord.toResponse() = BookResponse(
    this[BOOK.ID],
    this[BOOK.AUTHOR_ID],
    this[BOOK.TITLE],
    this[BOOK.PUBLISHED_IN],
    this[BOOK.LANGUAGE_ID]
)

data class BookResponse(
    val id: Int?,
    val authorId: Int?,
    val title: String?,
    val publishedIn: Int?,
    val languageId: Int?
)
