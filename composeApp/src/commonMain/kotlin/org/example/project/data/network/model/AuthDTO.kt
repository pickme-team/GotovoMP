package org.example.project.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val username: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val password: String,
)

fun SignUpRequest.toSignInWithPhoneNumberRequest() =
    SignInWithPhoneNumberRequest(phoneNumber, password)

@Serializable
data class SignInWithUsernameRequest(
    val username: String,
    val password: String,
)

@Serializable
data class SignInWithPhoneNumberRequest(
    val phoneNumber: String,
    val password: String,
)
