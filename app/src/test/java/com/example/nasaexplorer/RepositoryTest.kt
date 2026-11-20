/** Prueba el comportamiento relacionado con repository. */
package com.example.nasaexplorer

import com.example.nasaexplorer.data.remote.apod.ApodApi
import com.example.nasaexplorer.data.remote.apod.ApodDto
import com.example.nasaexplorer.data.remote.neows.NeoWsApi
import com.example.nasaexplorer.data.remote.neows.NeoWsDto
import com.example.nasaexplorer.data.repository.ApodRepository
import com.example.nasaexplorer.data.repository.NeoWsApiException
import com.example.nasaexplorer.data.repository.NeoWsRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

/** Grupo de pruebas relacionadas con repository. */
class RepositoryTest {
    @Test
    fun apodRepositoryReturnsValidatedNASAData() = runBlocking {
        val repository = ApodRepository(object : ApodApi {
            override suspend fun getTodayApod() = listOf(ApodDto(date = "2026-11-13", title = "Sky"))
        })

        assertEquals("Sky", repository.getTodayApod().getOrThrow().title)
    }

    @Test
    /** Verifica el comportamiento esperado de apod repository rejects an empty response. */
    fun apodRepositoryRejectsAnEmptyResponse() = runBlocking {
        val repository = ApodRepository(object : ApodApi {
            override suspend fun getTodayApod() = emptyList<ApodDto>()
        })

        assertTrue(repository.getTodayApod().isFailure)
    }

    @Test
    /** Verifica el comportamiento esperado de neo repository does not send an unsupported date range. */
    fun neoRepositoryDoesNotSendAnUnsupportedDateRange() = runBlocking {
        var wasCalled = false
        val api = object : NeoWsApi {
            override suspend fun getAsteroids(startDate: String, endDate: String, apiKey: String): NeoWsDto {
                wasCalled = true
                return NeoWsDto()
            }
        }
        val result = NeoWsRepository(api).getAsteroids(
            startDate = "2026-11-01",
            endDate = "2026-11-09",
            apiKey = "test"
        )

        assertTrue(result.isFailure)
        assertEquals(false, wasCalled)
    }

    @Test
    /** Verifica el comportamiento esperado de neo repository explains rate limit responses. */
    fun neoRepositoryExplainsRateLimitResponses() = runBlocking {
        val api = object : NeoWsApi {
            override suspend fun getAsteroids(
                startDate: String,
                endDate: String,
                apiKey: String
            ): NeoWsDto {
                throw HttpException(
                    Response.error<NeoWsDto>(
                        429,
                        """{"error":{"code":"OVER_RATE_LIMIT"}}"""
                            .toResponseBody("application/json".toMediaType())
                    )
                )
            }
        }

        val result = NeoWsRepository(api).getAsteroids(
            startDate = "2026-11-13",
            endDate = "2026-11-13",
            apiKey = "test"
        )

        val error = result.exceptionOrNull() as NeoWsApiException
        assertEquals(429, error.statusCode)
        assertEquals("Límite de solicitudes alcanzado", error.title)
        assertTrue(error.message.contains("DEMO_KEY"))
    }
}
