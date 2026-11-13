package com.example.nasaexplorer.data.repository

import com.example.nasaexplorer.data.remote.neows.NeoWsDto
import com.example.nasaexplorer.data.remote.neows.NeoWsRetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class NeoWsRepository {

    suspend fun getAsteroids(
        startDate: String,
        endDate: String,
        apiKey: String
    ): Result<NeoWsDto> = withContext(Dispatchers.IO) {

        try {
            val response = NeoWsRetrofitInstance.neoWsApi.getAsteroids(
                startDate = startDate,
                endDate = endDate,
                apiKey = apiKey
            )

            Result.success(response)

        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión. Verifica tu internet."))

        } catch (e: HttpException) {
            Result.failure(Exception("Error del servidor NASA: ${e.code()}"))

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
