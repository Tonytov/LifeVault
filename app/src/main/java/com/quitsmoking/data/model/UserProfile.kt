package com.quitsmoking.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey
    val id: Long = 1,
    val gender: Gender,
    val birthDate: LocalDate,
    val height: Int, // in cm
    val weight: Int, // in kg  
    val region: LifeExpectancyRegion,
    val baseLifeExpectancy: Int, // calculated base life expectancy in years
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class Gender {
    MALE, FEMALE
}

enum class LifeExpectancyRegion(
    val displayName: String,
    val baseMaleLifeExpectancy: Int,
    val baseFemaleLifeExpectancy: Int
) {
    WESTERN_EUROPE("Western Europe", 78, 83),
    EASTERN_EUROPE("Eastern Europe", 71, 80),
    NORTH_AMERICA("North America", 76, 81),
    SOUTH_AMERICA("South America", 72, 78),
    DEVELOPED_ASIA("Developed Asia", 80, 86),
    DEVELOPING_ASIA("Developing Asia", 68, 73),
    AFRICA("Africa", 58, 62),
    OCEANIA("Oceania", 79, 83)
}

fun LifeExpectancyRegion.getLifeExpectancyForGender(gender: Gender): Int {
    return when (gender) {
        Gender.MALE -> baseMaleLifeExpectancy
        Gender.FEMALE -> baseFemaleLifeExpectancy
    }
}