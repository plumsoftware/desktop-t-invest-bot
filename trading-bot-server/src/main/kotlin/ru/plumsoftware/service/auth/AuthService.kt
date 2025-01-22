package ru.plumsoftware.service.auth

import ru.plumsoftware.model.UserDto
import ru.plumsoftware.service.auth.database.UsersTable


class AuthService {

    suspend fun createNewUser(userDto: UserDto) {
        val user = UsersTable.selectByPhone(phone = userDto.phone)
        if (user != null) throw Exception("Phone is not unique")
        else
            UsersTable.insert(userDto = userDto)
    }

    suspend fun getUserByPhone(phone: String): UserDto? = UsersTable.selectByPhone(phone = phone)
}