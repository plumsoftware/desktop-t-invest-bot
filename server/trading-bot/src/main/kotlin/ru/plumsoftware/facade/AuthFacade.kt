package ru.plumsoftware.facade

import ru.plumsoftware.mappers.trading.toDto1
import ru.plumsoftware.mappers.trading.toDto2
import ru.plumsoftware.mappers.trading.toReceive
import ru.plumsoftware.mappers.trading.toReceive2
import ru.plumsoftware.net.core.model.dto.UserDto
import ru.plumsoftware.net.core.model.dto.trading.TradingModelsDto
import ru.plumsoftware.net.core.model.receive.PasswordMatchReceive
import ru.plumsoftware.net.core.model.receive.TTokensReceive
import ru.plumsoftware.net.core.model.receive.UserReceive
import ru.plumsoftware.net.core.model.receive.trading.TradingModelsReceive
import ru.plumsoftware.net.core.model.response.UserResponseEither
import ru.plumsoftware.service.auth.AuthService
import ru.plumsoftware.service.auth.database.PasswordsTable
import ru.plumsoftware.service.auth.database.SecretKeysTable
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

        val key = SecretKeysTable.selectById(id = tTokensReceive.id)

        if (key != null) {
            val secretKey = cryptographyService.stringToKey(key)

            val marketTokenEncrypted =
                cryptographyService.encrypt(tTokensReceive.marketToken, secretKey)
            val sandboxTokenEncrypted =
                cryptographyService.encrypt(tTokensReceive.sandboxToken, secretKey)

            authService.insertTTokens(
                tTokensReceive = tTokensReceive.copy(
                    marketToken = marketTokenEncrypted,
                    sandboxToken = sandboxTokenEncrypted
                )
            )
        } else throw Exception("Invalid id")
    }

    suspend fun insertTradingModels(tradingModelsReceive: TradingModelsReceive) {
        authService.insertTradingModel(tradingModelsDto = encryptTradingModels(tradingModelsReceive = tradingModelsReceive))
    }

    suspend fun insertSandboxTradingModels(tradingModelsReceive: TradingModelsReceive) {
        authService.insertSandboxTradingModel(
            tradingModelsDto = encryptTradingModels(
                tradingModelsReceive = tradingModelsReceive
            )
        )
    }

    suspend fun getTTokens(id: Long): TTokensReceive? {

        val key = SecretKeysTable.selectById(id = id)
        if (key != null) {

            val secretKey = cryptographyService.stringToKey(key)

            val tTokensDto = authService.getTTokens(id = id)

            return if (tTokensDto != null) {
                val marketTokenDecrypted =
                    cryptographyService.decrypt(tTokensDto.marketToken, secretKey)
                val sandboxTokenDecrypted =
                    cryptographyService.decrypt(tTokensDto.sandboxToken, secretKey)

                TTokensReceive(
                    id = id,
                    marketToken = marketTokenDecrypted,
                    sandboxToken = sandboxTokenDecrypted
                )
            } else null
        } else throw Exception("Invalid id")
    }

    suspend fun getUser(phone: String): UserResponseEither {

        var id: Long? = null
        var exitLoop = false

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
            UserResponseEither.UserResponse(
                id = id
            )
        } else UserResponseEither.Error(msg = "Invalid id")
    }

    suspend fun getTradingModels(id: Long): TradingModelsReceive {
        return decryptTradingModels(tradingModelsDto = authService.getTTradingModels(id = id))
    }

    suspend fun getSandboxTradingModels(id: Long): TradingModelsReceive {
        return authService.getSandboxTTradingModels(id = id).toReceive2()
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

    suspend fun match(passwordMatchReceive: PasswordMatchReceive): Boolean {
        val passwordFromDb = PasswordsTable.selectById(id = passwordMatchReceive.id)
        if (passwordFromDb != null) {
            val matches: Boolean = hashService.matches(passwordMatchReceive.password, passwordFromDb)
            return matches
        } else throw Exception("Invalid id")
    }

    private suspend fun encryptTradingModels(tradingModelsReceive: TradingModelsReceive): TradingModelsDto {
        val id = tradingModelsReceive.id
        val key = SecretKeysTable.selectById(id = id)

        if (key != null) {

            val secretKey = cryptographyService.stringToKey(key)


            val tradingModelsDto = tradingModelsReceive
                .toDto1()
                .copy(
                    tradingModelsDto = tradingModelsReceive.tradingModelsReceive.map {
                        val encryptedFigi = cryptographyService.encrypt(it.figi, secretKey)
                        it
                            .toDto2()
                            .copy(
                                figi = encryptedFigi
                            )
                    }
                )


            return tradingModelsDto
        } else throw Exception("Invalid id")
    }

    private suspend fun decryptTradingModels(tradingModelsDto: TradingModelsDto): TradingModelsReceive {
        val id = tradingModelsDto.id
        val key = SecretKeysTable.selectById(id = id)

        if (key != null) {

            val secretKey = cryptographyService.stringToKey(key)


            val tradingModelsReceive = tradingModelsDto
                .toReceive2()
                .copy(
                    tradingModelsReceive = tradingModelsDto.tradingModelsDto.map {
                        val decryptedFigi = cryptographyService.decrypt(it.figi, secretKey)
                        it
                            .toReceive()
                            .copy(
                                figi = decryptedFigi
                            )
                    }
                )


            return tradingModelsReceive
        } else throw Exception("Invalid id")
    }
}