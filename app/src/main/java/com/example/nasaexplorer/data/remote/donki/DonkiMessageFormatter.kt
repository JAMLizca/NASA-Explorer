/** Convierte los datos DONKI en mensajes legibles para la interfaz. */
package com.example.nasaexplorer.data.remote.donki

data class DonkiMessageBlock(
    val text: String,
    val isHeading: Boolean
)

internal fun parseDonkiMessageBody(markdown: String): List<DonkiMessageBlock> {
    // DONKI mezcla Markdown y etiquetas HTML; normaliza primero los saltos y elimina el marcado
    // para que el resumen no dependa de contenido visual o enlaces incrustados.
    val normalized = markdown
        .replace("\r\n", "\n")
        .replace('\r', '\n')
        .replace(Regex("(?i)<br\\s*/?>"), "\n")
        .replace(Regex("<[^>]+>"), "")
        .replace("&nbsp;", " ")
        .replace("&amp;", "&")

    return normalized.lineSequence()
        .mapNotNull { line ->
            val trimmed = line.trim()
            if (trimmed.isEmpty()) return@mapNotNull null

            val isHeading = trimmed.matches(Regex("^#{1,6}\\s+.+"))
            val text = trimmed
                .replace(Regex("^#{1,6}\\s*"), "")
                .replace(Regex("^[-*+]\\s+"), "• ")
                .replace(Regex("\\[([^]]+)]\\([^)]+\\)"), "$1")
                .replace(Regex("\\*\\*(.*?)\\*\\*"), "$1")
                .replace(Regex("__(.*?)__"), "$1")
                .replace(Regex("(?<!\\*)\\*(?!\\s)(.+?)(?<!\\s)\\*(?!\\*)"), "$1")
                .replace(Regex("(?<!_)_(?!\\s)(.+?)(?<!\\s)_(?!_)"), "$1")
                .replace("`", "")
                .replace(Regex("\\s+"), " ")
                .trim()

            text.takeIf(String::isNotBlank)?.let { DonkiMessageBlock(it, isHeading) }
        }
        .toList()
}

internal fun summarizeDonkiMessage(blocks: List<DonkiMessageBlock>): List<DonkiMessageBlock> {
    val relevantHeadings = listOf(
        "WHAT",
        "WHEN",
        "WHERE",
        "SUMMARY",
        "DESCRIPTION",
        "IMPACT",
        "MAGNITUDE",
        "POSITION"
    )
    val summary = mutableListOf<DonkiMessageBlock>()
    var index = 0

    while (index < blocks.size && summary.count(DonkiMessageBlock::isHeading) < 4) {
        val block = blocks[index]
        if (!block.isHeading) {
            index++
            continue
        }

        val heading = block.text.substringBefore(':').trim().uppercase()
        // Prioriza secciones informativas y omite el resto para mantener legible la tarjeta.
        if (relevantHeadings.any(heading::startsWith)) {
            summary += block
            val nextBlock = blocks.getOrNull(index + 1)
            if (nextBlock != null && !nextBlock.isHeading) {
                summary += nextBlock.copy(text = nextBlock.text.concise())
            }
        }
        index++
    }

    return if (summary.isEmpty()) {
        blocks.take(SUMMARY_BLOCK_LIMIT).map { block ->
            if (block.isHeading) block else block.copy(text = block.text.concise())
        }
    } else {
        summary
    }
}

private fun String.concise(): String {
    if (length <= SUMMARY_TEXT_LIMIT) return this
    val lastSpace = lastIndexOf(' ', SUMMARY_TEXT_LIMIT)
    return take(if (lastSpace > SUMMARY_TEXT_LIMIT / 2) lastSpace else SUMMARY_TEXT_LIMIT)
        .trimEnd() + "…"
}

private const val SUMMARY_BLOCK_LIMIT = 4
private const val SUMMARY_TEXT_LIMIT = 420
