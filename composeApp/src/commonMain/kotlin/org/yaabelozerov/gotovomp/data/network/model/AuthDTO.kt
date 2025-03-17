package org.yaabelozerov.gotovomp.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val username: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val password: String,
)


@Serializable
data class UserDTO(
    val username: String,
    val firstName: String?,
    val lastName: String?,
    val phoneNumber: String
)

@Serializable
data class SignInDTO(
    val token: String
)

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
