package org.readium.r2streamer.Containers

import org.readium.r2shared.RootFile
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.util.zip.ZipFile


interface Container{

    var rootFile: RootFile

    var successCreated: Boolean

    fun data(relativePath: String) : ByteArray

    //fun dataLength(relativePath: String) : Int

    //fun dataInputStream(relativePath: String) : InputStream
}

interface DirectoryContainer : Container {

    override fun data(relativePath: String) : ByteArray {
        val filePath = rootFile.toString() + "/" + relativePath
        val epubFile = File(filePath)

        if (!epubFile.exists())
            throw Exception("Missing File")

        val buffer = ByteArrayOutputStream()

        var nRead: Int
        val data = ByteArray(16384)
        val fis = FileInputStream(epubFile)

        do {
            nRead = fis.read(data, 0, data.size)
            buffer.write(data, 0, nRead)
        } while (nRead != -1)

        buffer.flush()

        return buffer.toByteArray()
    }

    fun generateFullPath(relativePath: String) : String{
        return rootFile.toString()
    }
}

interface ZipArchiveContainer: Container {
    var zipFile: ZipFile

    override fun data(relativePath: String) : ByteArray{
        val zipEntry = zipFile.getEntry(relativePath)
        val buffer = ByteArrayOutputStream()
        var nRead: Int
        val data = ByteArray(16384)
        val fis = zipFile.getInputStream(zipEntry)

        nRead = fis.read(data, 0, data.size)
        while (nRead != -1) {
            buffer.write(data, 0, nRead)
            nRead = fis.read(data, 0, data.size)
        }
        buffer.flush()
        return buffer.toByteArray()
    }
}

interface EpubContainer : Container {
}

interface CbzContainer : Container {
    fun getFilesList() : List<String>
}
