package com.fynnian.dev_meeting.jooqdevmeeting

import com.beust.klaxon.Klaxon
import org.jooq.Converter
import org.jooq.JSONB

enum class OrgStatus {
    NEW,
    ACTIVE,
    CLOSED,
    UNKNOWN
}

data class Translation(
    val de: String?,
    val fr: String?,
    val it: String?,
    val eb: String?
)

class TranslationConverter : Converter<JSONB, Translation> {
    override fun from(databaseObject: JSONB?): Translation? {
        return Klaxon().parse<Translation>(databaseObject?.data() ?: "{}")
    }

    override fun to(userObject: Translation?): JSONB? {
        return JSONB.jsonbOrNull(Klaxon().toJsonString(userObject))
    }

    override fun fromType(): Class<JSONB> {
        return JSONB::class.java
    }

    override fun toType(): Class<Translation> {
        return Translation::class.java
    }

}
