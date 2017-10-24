package org.readium.r2.streamer.Parser

import android.content.SharedPreferences
import android.util.Log
import org.readium.r2.shared.Publication
import org.readium.r2.streamer.XmlParser.XmlParser
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

    override fun parse(fileAtPath: String) : PubBox? {
        val aexml = XmlParser()
        val container = try {
            generateContainerFrom(fileAtPath)
        } catch (e: Exception) {
            Log.e("Error", "Could not generate container", e)
            return null
        }
        var data = try {
            container.data(containerDotXmlPath)
        } catch (e: Exception) {
            Log.e("Error", "Missing File : META-INF/container.xml", e)
            return null
        }

        aexml.parseXml(ByteArrayInputStream(data))
        container.rootFile.mimetype = mimetype
        container.rootFile.rootFilePath = aexml.getFirst("container")
                ?.getFirst("rootfiles")
                ?.getFirst("rootfile")
                ?.properties?.get("full-path")
                ?: "content.opf"

        data = try {
            container.data(container.rootFile.rootFilePath)
        } catch (e: Exception) {
            Log.e("Error", "Missing File : ${container.rootFile.rootFilePath}", e)
            return null
        }
        aexml.parseXml(ByteArrayInputStream(data))
        val epubVersion = aexml.root().properties["version"]!!.toDouble()
        val publication: Publication
        try {
            publication = opfParser.parseOpf(aexml, container, epubVersion)
        } catch(e: Exception){
            Log.e("Error", e.message, e)
            return null
        }

        // commenting out for now
//        parseEncryption(container, publication)
        parseNavigationDocument(container, publication)
        parseNcxDocument(container, publication)
        return PubBox(publication, container)
    }

    private fun parseEncryption(container: EpubContainer, publication: Publication) {
        val document: XmlParser
        try {
            document = container.xmlDocumentforFile(encryptionDotXmlPath)
        } catch (e: Exception){
            Log.d("Error", "Could not decrypt", e)
            return
        }
        return
    }

    private fun parseNavigationDocument(container: EpubContainer, publication: Publication) {
        val navLink = publication.linkWithRel("contents") ?: return
        val navDocument = try {
            container.xmlDocumentforResource(navLink)
        } catch(e: Exception){
            Log.e("Error", "Navigation parsing", e)
            return
        }
        ndp.navigationDocumentPath = navLink.href ?: return
        publication.tableOfContents.plusAssign(ndp.tableOfContent(navDocument))
        publication.landmarks.plusAssign(ndp.landmarks(navDocument))
        publication.listOfAudioFiles.plusAssign(ndp.listOfAudiofiles(navDocument))
        publication.listOfIllustrations.plusAssign(ndp.listOfIllustrations(navDocument))
        publication.listOfTables.plusAssign(ndp.listOfTables(navDocument))
        publication.listOfVideos.plusAssign(ndp.listOfVideos(navDocument))
        publication.pageList.plusAssign(ndp.pageList(navDocument))
    }

    private fun parseNcxDocument(container: EpubContainer, publication: Publication){
        val ncxLink = publication.resources.firstOrNull { it.typeLink == "application/x-dtbncx+xml" } ?: return
        val ncxDocument = try {
            container.xmlDocumentforResource(ncxLink)
        } catch (e: Exception) {
            Log.e("Error", "Ncx parsing", e)
            return
        }
        ncxp.ncxDocumentPath = ncxLink.href ?: return
        if (publication.tableOfContents.isEmpty())
            publication.tableOfContents.plusAssign(ncxp.tableOfContents(ncxDocument))
        if (publication.pageList.isEmpty())
            publication.pageList.plusAssign(ncxp.pageList(ncxDocument))
        return
    }

}