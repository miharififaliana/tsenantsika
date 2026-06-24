package com.example.tsenantsika.data.entities

import androidx.room.TypeConverter
import java.time.Instant

/**
 * TypeConverter pour convertir java.time.Instant vers/from Long (epoch millis)
 * afin de le stocker correctement dans SQLite via Room.
 *
 * Ceci est nécessaire car Room ne supporte pas nativement java.time.Instant
 * sans converter (surtout pour minSdk 21).
 */

/**
 * TypeConverter pour java.time.Instant
 * @ProvidedTypeConverter est obligatoire avec les versions récentes de Room.
 */
class InstantConverter {

    @TypeConverter
    fun fromInstant(instant: Instant?): Long? {
        return instant?.toEpochMilli()
    }

    @TypeConverter
    fun toInstant(value: Long?): Instant? {
        return value?.let { Instant.ofEpochMilli(it) }
    }
}