package org.readium.r2.streamer.Parser

import org.readium.r2.shared.Publication
import org.readium.r2.streamer.Containers.Container

data class PubBox(val publication: Publication, val container: Container)

interface PublicationParser {
    fun parse(fileAtPath: String) : PubBox
}