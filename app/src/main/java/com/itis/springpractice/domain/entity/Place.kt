package com.itis.springpractice.domain.entity

data class Place(
    val uri: String,
    val name: String,
    val workingHours: String?,
    val longitude: Double? = null,
    val latitude: Double? = null,
    val phones: String,
    val closed: Boolean,
    val category: String,
    val address: String,
    val photoUrl: String?,
    val description: String?,
)
