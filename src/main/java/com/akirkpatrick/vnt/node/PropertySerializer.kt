package com.akirkpatrick.vnt.repository

import com.akirkpatrick.vnt.store.Serializer
import java.util.UUID
import java.io.RandomAccessFile
import com.akirkpatrick.vnt.write
import com.akirkpatrick.vnt.UUIDread
import com.akirkpatrick.vnt.node.Property

class PropertySerializer(raf: RandomAccessFile) : Serializer<Property>(raf) {
    override fun writeKey(o: Property): Long {
        val loc=raf.getFilePointer()
        raf.writeUTF(o.name)
        raf.writeUTF(o.value)
        return loc
    }

    override fun readKey(offset: Long): Property {
        raf.seek(offset)
        val name=raf.readUTF()
        val value=raf.readUTF()
        return Property(name, value)
    }
}

