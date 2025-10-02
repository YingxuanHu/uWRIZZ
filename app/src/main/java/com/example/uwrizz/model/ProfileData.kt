package com.example.uwrizz.model

data class BasicUserInfo(
    val userId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val age: Int = 0,
    val ethnicity: String = "",
    val gender: String = "",
    val program: String = "",
    val programEmoji: String = "",
    val hobby: String = "",
    val hobbyEmoji: String = "",
    val oneWord: String = "",
    val oneEmoji: String = "",
    val prompt: String = "",
    val promptAnswer: String = "",
    val profilePictureUri: String = "",
    val pictureUri1: String = "",
    val pictureUri2: String = "",
    val pictureUri3: String = "",
    val likes: List<String>? = null
)

data class UserPreference(
    val userId: String = "",
    val interestedInGender: List<String> = listOf(),
    val interestedInEthnicity: List<String> = listOf(),
    val interestedInProgram: List<String> = listOf(),
    val agePreferenceMin: Int = 0,
    val agePreferenceMax: Int = 0
)

data class SurveyAnswers(
    val userId: String = "",
    val answers: List<Int> = listOf()
)
