package ru.plumsoftware.facade

import ru.plumsoftware.net.core.model.dto.UserDto
import ru.plumsoftware.net.core.model.receive.TTokensReceive
import ru.plumsoftware.net.core.model.receive.UserReceive
import ru.plumsoftware.net.core.model.response.UserResponseEither
import ru.plumsoftware.service.auth.AuthService
import service.cryptography.CryptographyService
import service.hash.HashService
import java.util.UUID

class AuthFacade(
    private val authService: AuthService,
    private val hashService: HashService,
    private val cryptographyService: CryptographyService
) {

    suspend fun authNewUser(userReceive: UserReceive): UserResponseEither {

        val allPhones = authService.getAllPhones()
        val phone = userReceive.phone

        allPhones.forEach {
            val secretKeyString = authService.getSecretKey(it.key)
            if (secretKeyString != null) {
                val secretKey = cryptographyService.stringToKey(secretKeyString)
                val encryptedPhone = authService.getPhone(id = it.key)

                if (encryptedPhone != null) {
                    val decryptedPhone = cryptographyService.decrypt(encryptedPhone, secretKey)
                    if (decryptedPhone == phone) return UserResponseEither.Error("Phone is not unique")
                }
            }
        }

        val id = UUID.randomUUID().mostSignificantBits
        val password = userReceive.password
        val name = userReceive.name

        val secretKey = cryptographyService.generateSecretKey()
        val userDto = UserDto(
            id = id,
            password = hashService.hash(password),
            name = cryptographyService.encrypt(name, secretKey),
            phone = cryptographyService.encrypt(phone, secretKey),
            secretKey = cryptographyService.keyToString(secretKey)
        )
        authService.createNewUser(userDto = userDto)
        return UserResponseEither.UserResponse(
            id = id
        )
    }

    suspend fun insertTTokens(tTokensReceive: TTokensReceive) {
        authService.insertTTokens(tTokensReceive = tTokensReceive)
    }

    suspend fun getTTokens(id: Long): TTokensReceive? {
        val tTokensDto = authService.getTTokens(id = id)
        return if (tTokensDto != null) {
            TTokensReceive(
                id = id,
                marketToken = tTokensDto.marketToken,
                sandboxToken = tTokensDto.sandboxToken
            )
        } else null
    }

    suspend fun getUser(phone: String): UserReceive? {

        var id: Long? = null
        var exitLoop: Boolean = false

        val allPhones = authService.getAllPhones()

        for (it in allPhones.entries) {
            val secretKeyString = authService.getSecretKey(it.key)
            if (secretKeyString != null) {
                val secretKey = cryptographyService.stringToKey(secretKeyString)
                val encryptedPhone = authService.getPhone(id = it.key)

                if (encryptedPhone != null) {
                    val decryptedPhone = cryptographyService.decrypt(encryptedPhone, secretKey)
                    if (decryptedPhone == phone) {
                        id = it.key
                        exitLoop = true
                        break
                    }
                }
            }
        }

        if (!exitLoop) {
            throw Exception("User is not registered")
        }

        return if (id != null) {
            val secretKey = cryptographyService.stringToKey(
                authService.getSecretKey(id = id) ?: throw Exception("Invalid secret key")
            )
            val name = cryptographyService.decrypt(
                authService.getName(id = id) ?: throw Exception("Invalid name"), secretKey
            )
            val password = authService.getPassword(id = id) ?: throw Exception("Invalid password")
            val phoneFromRemote = cryptographyService.decrypt(
                authService.getPhone(id = id) ?: throw Exception("Invalid phone"), secretKey
            )

            UserReceive(
                password = password,
                name = name,
                phone = phoneFromRemote
            )
        } else null
    }

    suspend fun getAll(): List<UserDto> {
        val allPhones = authService.getAllPhones()
        val list = mutableListOf<UserDto>()
        allPhones.forEach {
            list.add(
                UserDto(
                    id = it.key,
                    password = authService.getPassword(it.key) ?: "",
                    name = authService.getName(it.key) ?: "",
                    phone = authService.getPhone(it.key) ?: "",
                    secretKey = authService.getSecretKey(it.key) ?: ""
                )
            )
        }
        return list
    }
}