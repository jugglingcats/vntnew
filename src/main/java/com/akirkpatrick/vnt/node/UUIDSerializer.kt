package com.akirkpatrick.vnt.repository

import com.akirkpatrick.vnt.store.Serializer
import java.util.UUID
import java.io.RandomAccessFile
import com.akirkpatrick.vnt.write
import com.akirkpatrick.vnt.UUIDread

class UUIDSerializer(raf: RandomAccessFile) : Serializer<UUID>(raf) {
    override fun writeKey(o: UUID): Long {
        return o.write(raf)
    }

    override fun readKey(offset: Long): UUID {
        return UUIDread(raf, offset)
    }
}

