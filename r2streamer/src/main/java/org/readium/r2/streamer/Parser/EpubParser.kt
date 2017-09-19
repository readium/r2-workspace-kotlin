package org.readium.r2.streamer.Parser

import android.util.Log
import org.readium.r2.shared.Publication
import org.readium.r2.streamer.AEXML.AEXML
import org.readium.r2.streamer.Containers.ContainerEpub
import org.readium.r2.streamer.Containers.ContainerEpubDirectory
import org.readium.r2.streamer.Containers.EpubContainer
import org.readium.r2.streamer.Parser.EpubParserSubClasses.EncryptionParser
import org.readium.r2.streamer.Parser.EpubParserSubClasses.NCXParser
import org.readium.r2.streamer.Parser.EpubParserSubClasses.NavigationDocumentParser
import org.readium.r2.streamer.Parser.EpubParserSubClasses.OPFParser
import java.io.ByteArrayInputStream
import java.io.File

// Some constants useful to parse an Epub document
const val defaultEpubVersion = 1.2
const val containerDotXmlPath = "META-INF/container.xml"
const val encryptionDotXmlPath = "META-INF/encryption.xml"
const val mimetype = "application/epub+zip"
const val mimetypeOEBPS = "application/oebps-package+xml"
const val mediaOverlayURL = "media-overlay?resource="

class EpubParser : PublicationParser {

    private val opfParser = OPFParser()
    private val ndp = NavigationDocumentParser()
    private val ncxp = NCXParser()
    private val encp = EncryptionParser()

    private fun generateContainerFrom(path: String) : EpubContainer {
        val isDirectory = File(path).isDirectory
        val container: EpubContainer?

        if (!File(path).exists())
            throw Exception("Missing File")
        container = when (isDirectory) {
            true -> ContainerEpubDirectory(path)
            false -> ContainerEpub(path)
        }
        if (!container.successCreated)
            throw Exception("Missing File")
        return container
    }

    override fun parse(fileAtPath: String) : PubBox {
        val aexml = AEXML()
        val container = try {
            generateContainerFrom(fileAtPath)
        } catch (e: Exception) { throw e }
        var data = try {
            container.data(containerDotXmlPath)
        } catch (e: Exception) { throw Exception("Missing File") }

        aexml.parseXml(ByteArrayInputStream(data))
        container.rootFile.mimetype = mimetype
        container.rootFile.rootFilePath = aexml.get("container").first()
                .get("rootfiles").first()
                .get("rootfile").first()
                .properties["full-path"]
                ?: "content.opf"

        //val document = container.data(container.rootFile.rootFilePath)
        data = try {
            container.data(container.rootFile.rootFilePath)
        } catch (e: Exception) { throw Exception("Missing File") }
        aexml.parseXml(ByteArrayInputStream(data))
        val epubVersion = aexml.getFirst("package").properties["version"]!!.toDouble()
        var publication = opfParser.parseOpf(aexml, container, epubVersion)

        parseEncryption(container, publication)
        publication = parseNavigationDocument(container, publication)
        publication = parseNcxDocument(container, publication)

        return PubBox(publication, container)
    }

    private fun parseEncryption(container: EpubContainer, publication: Publication) {
        val document: AEXML
        try {
            document = container.xmlDocumentforFile(encryptionDotXmlPath)
        } catch (e: Exception){
            Log.d("Error", e.toString())
            return
        }
        return
    }

    private fun parseNavigationDocument(container: EpubContainer, publication: Publication) : Publication {
        return publication
    }

    private fun parseNcxDocument(container: EpubContainer, publication: Publication) : Publication {
        return publication
    }

}