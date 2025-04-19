package com.example.healthcareproject.data.source.local

import androidx.room.TypeConverter
import com.example.healthcareproject.domain.model.BloodType
import com.example.healthcareproject.domain.model.Gender
import com.example.healthcareproject.domain.model.MealRelation
import com.example.healthcareproject.domain.model.MeasurementType
import com.example.healthcareproject.domain.model.NotificationType
import com.example.healthcareproject.domain.model.RelatedTable
import com.example.healthcareproject.domain.model.Relationship
import com.example.healthcareproject.domain.model.RepeatPattern
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate): String {
        return date.toString()
    }

    @TypeConverter
    fun toLocalDate(date: String): LocalDate {
        return LocalDate.parse(date)
    }

    @TypeConverter
    fun fromLocalTime(time: LocalTime): String {
        return time.toString()
    }

    @TypeConverter
    fun toLocalTime(time: String): LocalTime {
        return LocalTime.parse(time)
    }

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime): String {
        return dateTime.toString()
    }

    @TypeConverter
    fun toLocalDateTime(dateTime: String): LocalDateTime {
        return LocalDateTime.parse(dateTime)
    }

    @TypeConverter
    fun fromGender(gender: Gender): String {
        return gender.name
    }

    @TypeConverter
    fun toGender(gender: String): Gender {
        return Gender.valueOf(gender)
    }

    @TypeConverter
    fun fromBloodType(bloodType: BloodType): String {
        return bloodType.name
    }

    @TypeConverter
    fun toBloodType(bloodType: String): BloodType {
        return BloodType.valueOf(bloodType)
    }

    @TypeConverter
    fun fromNotificationType(type: NotificationType): String {
        return type.name
    }

    @TypeConverter
    fun toNotificationType(type: String): NotificationType {
        return NotificationType.valueOf(type)
    }

    @TypeConverter
    fun fromRelatedTable(table: RelatedTable): String {
        return table.name
    }

    @TypeConverter
    fun toRelatedTable(table: String): RelatedTable {
        return RelatedTable.valueOf(table)
    }

    @TypeConverter
    fun fromRelationship(relationship: Relationship): String {
        return relationship.name
    }

    @TypeConverter
    fun toRelationship(relationship: String): Relationship {
        return Relationship.valueOf(relationship)
    }

    @TypeConverter
    fun fromMeasurementType(type: MeasurementType): String {
        return type.name
    }

    @TypeConverter
    fun toMeasurementType(type: String): MeasurementType {
        return MeasurementType.valueOf(type)
    }

    @TypeConverter
    fun fromMealRelation(relation: MealRelation): String {
        return relation.name
    }

    @TypeConverter
    fun toMealRelation(relation: String): MealRelation {
        return MealRelation.valueOf(relation)
    }

    @TypeConverter
    fun fromRepeatPattern(pattern: RepeatPattern): String {
        return pattern.name
    }

    @TypeConverter
    fun toRepeatPattern(pattern: String): RepeatPattern {
        return RepeatPattern.valueOf(pattern)
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return value.split(",").map { it.trim() }
    }

    @TypeConverter
    fun fromIntList(value: List<Int>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toIntList(value: String): List<Int> {
        return value.split(",").map { it.trim().toInt() }
    }

    @TypeConverter
    fun fromFloatList(value: List<Float>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toFloatList(value: String): List<Float> {
        return value.split(",").map { it.trim().toFloat() }
    }
}