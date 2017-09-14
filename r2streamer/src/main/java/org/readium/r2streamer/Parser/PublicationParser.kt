package org.readium.r2streamer.Parser

import org.readium.r2shared.Publication
import org.readium.r2streamer.Containers.Container

data class PubBox(val publication: Publication, val container: Container)

interface PublicationParser {
    fun parse(fileAtPath: String) : PubBox
}