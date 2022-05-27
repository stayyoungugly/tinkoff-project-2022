package com.itis.springpractice.domain.entity

data class Place(
    val uri: String,
    val name: String,
    val workingHours: String?,
    val closed: Boolean,
    val address: String,
    val photoUrl: String?,
    val description: String?,
)
