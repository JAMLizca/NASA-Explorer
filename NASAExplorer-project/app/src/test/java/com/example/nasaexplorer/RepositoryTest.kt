/** Prueba el comportamiento de la funcionalidad correspondiente y verifica sus resultados esperados. */
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

/** Agrupa las pruebas relacionadas con esta funcionalidad. */
class RepositoryTest {
    /** Verifica el resultado esperado de este escenario. */
    @Test
    fun apodRepositoryReturnsValidatedNASAData() = runBlocking {
        val repository = ApodRepository(object : ApodApi {
            /** Verifica el resultado esperado de este escenario. */
            override suspend fun getTodayApod() = listOf(ApodDto(date = "2026-11-13", title = "Sky"))
        })

        assertEquals("Sky", repository.getTodayApod().getOrThrow().title)
    }

    /** Verifica el resultado esperado de este escenario. */
    @Test
    fun apodRepositoryRejectsAnEmptyResponse() = runBlocking {
        val repository = ApodRepository(object : ApodApi {
            /** Verifica el resultado esperado de este escenario. */
            override suspend fun getTodayApod() = emptyList<ApodDto>()
        })

        assertTrue(repository.getTodayApod().isFailure)
    }

    /** Verifica el resultado esperado de este escenario. */
    @Test
    fun neoRepositoryDoesNotSendAnUnsupportedDateRange() = runBlocking {
        var wasCalled = false
        val api = object : NeoWsApi {
            /** Verifica el resultado esperado de este escenario. */
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

    /** Verifica el resultado esperado de este escenario. */
    @Test
    fun neoRepositoryExplainsRateLimitResponses() = runBlocking {
        val api = object : NeoWsApi {
            /** Verifica el resultado esperado de este escenario. */
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
