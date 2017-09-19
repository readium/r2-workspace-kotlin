package org.readium.r2.streamer.Containers

import org.readium.r2.shared.Link
import org.readium.r2.shared.RootFile
import org.readium.r2.streamer.AEXML.AEXML
import java.io.File

class ContainerEpubDirectory(path: String) : EpubContainer, DirectoryContainer {

    override var successCreated: Boolean = false
    lateinit override var rootFile: RootFile

    override fun xmlDocumentforFile(relativePath: String): AEXML {
        val containerData = data(relativePath)
        val document = AEXML()
        document.parseXml(containerData.inputStream())
        return document
    }

    override fun xmlDocumentforResource(link: Link?): AEXML {
        var pathFile = link?.href ?: throw Exception("missing Link : ${link?.title}")
        if (pathFile.first() == '/')
            pathFile = pathFile.substring(1)
        return xmlDocumentforFile(pathFile)
    }

    init {
        if (File(path).exists())
            successCreated = true
        rootFile = RootFile(rootPath = path, version = null)
    }

    override fun data(relativePath: String): ByteArray {
        return super.data(relativePath)
    }
}