package ru.plumsoftware.service.auth

import ru.plumsoftware.model.dto.UserDto
import ru.plumsoftware.model.receive.TTokensReceive
import ru.plumsoftware.service.auth.database.NamesTable
import ru.plumsoftware.service.auth.database.PasswordsTable
import ru.plumsoftware.service.auth.database.PhonesTable
import ru.plumsoftware.service.auth.database.SecretKeysTable
import ru.plumsoftware.service.auth.database.TTokensTable

class AuthService {

    suspend fun createNewUser(userDto: UserDto) {
        with (userDto) {
            NamesTable.insert(name = name, id = id)
            PasswordsTable.insert(password = password, id = id)
            PhonesTable.insert(phone = phone, id = id)
            SecretKeysTable.insert(secretKey = secretKey, id = id)
        }
    }

    suspend fun insertTTokens(tTokensReceive: TTokensReceive) {
        with(tTokensReceive) {
            TTokensTable.insert(marketToken = marketToken, sandboxToken = sandboxToken, id = id)
        }
    }

    suspend fun getSecretKey(id: Long): String? {
        return SecretKeysTable.selectById(id = id)
    }

    suspend fun getAllPhones(): Map<Long, String> = PhonesTable.getAllPhones()

    suspend fun getPhone(id: Long): String? = PhonesTable.selectById(id = id)

    suspend fun getPassword(id: Long): String? = PasswordsTable.selectById(id = id)

    suspend fun getName(id: Long): String? = NamesTable.selectById(id = id)
}