package com.akirkpatrick.vnt

import java.util.UUID
import java.io.RandomAccessFile

public fun UUID.write(raf: RandomAccessFile) : Long {
    val loc=raf.getFilePointer()
    raf.writeLong(this.getMostSignificantBits())
    raf.writeLong(this.getLeastSignificantBits())
    return loc
}

public fun UUIDread(raf: RandomAccessFile, offset : Long = -1.toLong()) : UUID {
    if ( offset >= 0 ) {
        raf.seek(offset)
    }
    val msb=raf.readLong()
    val lsb=raf.readLong()
    return UUID(msb, lsb)
}