/** Prueba el comportamiento relacionado con space activity dto. */
package com.example.nasaexplorer

import com.example.nasaexplorer.data.remote.donki.DonkiNotificationDto
import com.example.nasaexplorer.data.remote.donki.parseDonkiMessageBody
import com.example.nasaexplorer.data.remote.donki.summarizeDonkiMessage
import com.example.nasaexplorer.data.remote.eonet.satelliteImageUrl
import com.example.nasaexplorer.data.remote.eonet.EonetEventsDto
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Test

/** Grupo de pruebas relacionadas con space activity dto. */
class SpaceActivityDtoTest {
    private val gson = Gson()

    @Test
    fun parsesDonkiNotificationFieldsShownOnHome() {
        val json = """
            {
              "messageType": "CME",
              "messageIssueTime": "2026-11-14T12:00Z",
              "messageBody": "NASA notification details.",
              "messageURL": "https://example.nasa.gov/notification"
            }
        """.trimIndent()

        val notification = gson.fromJson(json, DonkiNotificationDto::class.java)

        assertEquals("CME", notification.messageType)
        assertEquals("2026-11-14T12:00Z", notification.messageIssueTime)
        assertEquals("NASA notification details.", notification.messageBody)
        assertEquals("https://example.nasa.gov/notification", notification.messageUrl)
    }

    @Test
    /** Verifica el comportamiento esperado de parses open eonet event and latest geometry fields. */
    fun parsesOpenEonetEventAndLatestGeometryFields() {
        val json = """
            {
              "events": [{
                "id": "EONET_123",
                "title": "Sample active event",
                "description": "Near the southern coast.",
                "link": "https://eonet.gsfc.nasa.gov/api/v3/events/EONET_123",
                "categories": [{"id": "wildfires", "title": "Wildfires"}],
                "geometry": [{
                  "date": "2026-11-14T00:00:00Z",
                  "magnitudeValue": 2.5,
                  "magnitudeUnit": "km",
                  "type": "Point",
                  "coordinates": [-89.06, 31.13]
                }]
              }]
            }
        """.trimIndent()

        val event = gson.fromJson(json, EonetEventsDto::class.java).events.single()

        assertEquals("EONET_123", event.id)
        assertEquals("Sample active event", event.title)
        assertEquals("Near the southern coast.", event.description)
        assertEquals("wildfires", event.categories.single().id)
        assertEquals("2026-11-14T00:00:00Z", event.geometry.single().date)
        assertEquals(2.5, event.geometry.single().magnitudeValue!!, 0.0)
        val imageUrl = event.satelliteImageUrl()
        assertEquals(true, imageUrl?.contains("LAYERS=MODIS_Terra_CorrectedReflectance_TrueColor"))
        assertEquals(true, imageUrl?.contains("TIME=2026-11-14"))
        assertEquals(true, imageUrl?.contains("BBOX=-90.2600,29.9300,-87.8600,32.3300"))
    }

    @Test
    /** Verifica el comportamiento esperado de formats donki markdown headings links and emphasis for reading. */
    fun formatsDonkiMarkdownHeadingsLinksAndEmphasisForReading() {
        val blocks = parseDonkiMessageBody(
            """
                ### WHAT: **Solar activity**

                - A *strong* flare was observed.
                Read [NASA details](https://example.nasa.gov/details).
            """.trimIndent()
        )

        assertEquals(3, blocks.size)
        assertEquals("WHAT: Solar activity", blocks[0].text)
        assertEquals(true, blocks[0].isHeading)
        assertEquals("• A strong flare was observed.", blocks[1].text)
        assertEquals("Read NASA details.", blocks[2].text)
    }

    @Test
    /** Verifica el comportamiento esperado de summarizes donki report using relevant sections only. */
    fun summarizesDonkiReportUsingRelevantSectionsOnly() {
        val blocks = parseDonkiMessageBody(
            """
                ### WHAT:
                A coronal mass ejection was observed.
                ### WHEN:
                It was observed today.
                ### WHERE:
                The event originated on the Sun.
                ### IMPACT:
                Possible geomagnetic effects.
                ### ADDITIONAL INFORMATION:
                Long technical reference that is not part of the summary.
            """.trimIndent()
        )

        val summary = summarizeDonkiMessage(blocks)

        assertEquals(8, summary.size)
        assertEquals(
            false,
            summary.any { it.text.contains("Long technical reference") }
        )
    }
}
