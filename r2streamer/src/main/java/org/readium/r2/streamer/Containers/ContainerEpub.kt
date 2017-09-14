package org.readium.r2.streamer.Containers

import org.readium.r2shared.RootFile
import org.readium.r2.streamer.Parser.mimetype
import java.io.File
import java.util.zip.ZipFile

class ContainerEpub(path: String) : EpubContainer, ZipArchiveContainer {

    lateinit override var rootFile: RootFile
    lateinit override var zipFile: ZipFile
    override var successCreated: Boolean = false

    init {
        if (File(path).exists())
            successCreated = true
        zipFile = ZipFile(path)
        rootFile = RootFile(path, mimetype = mimetype)
    }

    override fun data(relativePath: String): ByteArray {
        return super.data(relativePath)
    }

}