package ru.plumsoftware.service.auth

import ru.plumsoftware.model.dto.UserDto
import ru.plumsoftware.service.auth.database.NamesTable
import ru.plumsoftware.service.auth.database.PasswordsTable
import ru.plumsoftware.service.auth.database.PhonesTable
import ru.plumsoftware.service.auth.database.SecretKeysTable

class AuthService {

    suspend fun createNewUser(userDto: UserDto) {
        NamesTable.insert(name = userDto.name, id = userDto.id)
        PasswordsTable.insert(password = userDto.password, id = userDto.id)
        PhonesTable.insert(phone = userDto.phone, id = userDto.id)
        SecretKeysTable.insert(secretKey = userDto.secretKey, id = userDto.id)
    }

    suspend fun getSecretKey(id: Long): String? {
        return SecretKeysTable.selectById(id = id)
    }

    suspend fun getAllPhones(): Map<Long, String> = PhonesTable.getAllPhones()

    suspend fun getPhone(id: Long): String? = PhonesTable.selectById(id = id)

    suspend fun getPassword(id: Long) : String? = PasswordsTable.selectById(id = id)

    suspend fun getName(id: Long) : String? = NamesTable.selectById(id = id)
}