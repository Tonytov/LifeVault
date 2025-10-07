package com.quitsmoking.data.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Converters {
    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? {
        return dateTime?.format(dateTimeFormatter)
    }

    @TypeConverter
    fun toLocalDateTime(dateTimeString: String?): LocalDateTime? {
        return dateTimeString?.let { LocalDateTime.parse(it, dateTimeFormatter) }
    }
    
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.format(dateFormatter)
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it, dateFormatter) }
    }
    
    @TypeConverter
    fun fromGender(gender: Gender?): String? {
        return gender?.name
    }

    @TypeConverter
    fun toGender(genderString: String?): Gender? {
        return genderString?.let { Gender.valueOf(it) }
    }
    
    @TypeConverter
    fun fromLifeExpectancyRegion(region: LifeExpectancyRegion?): String? {
        return region?.name
    }

    @TypeConverter
    fun toLifeExpectancyRegion(regionString: String?): LifeExpectancyRegion? {
        return regionString?.let { LifeExpectancyRegion.valueOf(it) }
    }
    
    @TypeConverter
    fun fromHabitType(habitType: HabitType?): String? {
        return habitType?.name
    }

    @TypeConverter
    fun toHabitType(habitTypeString: String?): HabitType? {
        return habitTypeString?.let { HabitType.valueOf(it) }
    }
    
    // Конвертеры для новых типов
    @TypeConverter
    fun fromHabitIntensity(intensity: HabitIntensity?): String? {
        return intensity?.name
    }

    @TypeConverter
    fun toHabitIntensity(intensityString: String?): HabitIntensity? {
        return intensityString?.let { HabitIntensity.valueOf(it) }
    }
    
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return if (value == null) null else Gson().toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return if (value == null) null else {
            val listType = object : TypeToken<List<String>>() {}.type
            Gson().fromJson(value, listType)
        }
    }
    
    @TypeConverter
    fun fromHabitTypeList(value: List<HabitType>?): String? {
        return if (value == null) null else Gson().toJson(value.map { it.name })
    }

    @TypeConverter
    fun toHabitTypeList(value: String?): List<HabitType>? {
        return if (value == null) null else {
            val listType = object : TypeToken<List<String>>() {}.type
            val stringList: List<String> = Gson().fromJson(value, listType)
            stringList.map { HabitType.valueOf(it) }
        }
    }
    
    @TypeConverter
    fun fromResearchCitationList(value: List<ResearchCitation>?): String? {
        return if (value == null) null else Gson().toJson(value)
    }

    @TypeConverter
    fun toResearchCitationList(value: String?): List<ResearchCitation>? {
        return if (value == null) null else {
            val listType = object : TypeToken<List<ResearchCitation>>() {}.type
            Gson().fromJson(value, listType)
        }
    }
    
    @TypeConverter
    fun fromStudyType(studyType: StudyType?): String? {
        return studyType?.name
    }

    @TypeConverter
    fun toStudyType(studyTypeString: String?): StudyType? {
        return studyTypeString?.let { StudyType.valueOf(it) }
    }
    
    @TypeConverter
    fun fromEvidenceLevel(evidenceLevel: EvidenceLevel?): String? {
        return evidenceLevel?.name
    }

    @TypeConverter
    fun toEvidenceLevel(evidenceLevelString: String?): EvidenceLevel? {
        return evidenceLevelString?.let { EvidenceLevel.valueOf(it) }
    }
    
    @TypeConverter
    fun fromArticleDifficulty(difficulty: ArticleDifficulty?): String? {
        return difficulty?.name
    }

    @TypeConverter
    fun toArticleDifficulty(difficultyString: String?): ArticleDifficulty? {
        return difficultyString?.let { ArticleDifficulty.valueOf(it) }
    }
    
    @TypeConverter
    fun fromArticleCategory(category: ArticleCategory?): String? {
        return category?.name
    }

    @TypeConverter
    fun toArticleCategory(categoryString: String?): ArticleCategory? {
        return categoryString?.let { ArticleCategory.valueOf(it) }
    }
    
    // Конвертеры для Time Bank
    @TypeConverter
    fun fromTransactionType(type: TransactionType?): String? {
        return type?.name
    }

    @TypeConverter
    fun toTransactionType(typeString: String?): TransactionType? {
        return typeString?.let { TransactionType.valueOf(it) }
    }
    
    @TypeConverter
    fun fromRewardCategory(category: RewardCategory?): String? {
        return category?.name
    }

    @TypeConverter
    fun toRewardCategory(categoryString: String?): RewardCategory? {
        return categoryString?.let { RewardCategory.valueOf(it) }
    }
    
    // Конвертеры для Daily Challenges
    @TypeConverter
    fun fromChallengeCategory(category: ChallengeCategory?): String? {
        return category?.name
    }

    @TypeConverter
    fun toChallengeCategory(categoryString: String?): ChallengeCategory? {
        return categoryString?.let { ChallengeCategory.valueOf(it) }
    }
    
    @TypeConverter
    fun fromChallengeDifficulty(difficulty: ChallengeDifficulty?): String? {
        return difficulty?.name
    }

    @TypeConverter
    fun toChallengeDifficulty(difficultyString: String?): ChallengeDifficulty? {
        return difficultyString?.let { ChallengeDifficulty.valueOf(it) }
    }
    
    @TypeConverter
    fun fromChallengeDuration(duration: ChallengeDuration?): String? {
        return duration?.name
    }

    @TypeConverter
    fun toChallengeDuration(durationString: String?): ChallengeDuration? {
        return durationString?.let { ChallengeDuration.valueOf(it) }
    }
    
    @TypeConverter
    fun fromChallengeStatus(status: ChallengeStatus?): String? {
        return status?.name
    }

    @TypeConverter
    fun toChallengeStatus(statusString: String?): ChallengeStatus? {
        return statusString?.let { ChallengeStatus.valueOf(it) }
    }
}