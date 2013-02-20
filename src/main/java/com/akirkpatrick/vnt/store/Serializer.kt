package com.akirkpatrick.vnt.store

import java.io.FileInputStream
import java.io.RandomAccessFile
import java.io.InputStream
import java.io.DataInputStream
import java.nio.channels.FileChannel
import java.nio.channels.ReadableByteChannel
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel.MapMode
import com.akirkpatrick.vnt.btree.Entry
import java.io.FileOutputStream
import java.io.DataOutputStream
import java.util.HashMap
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import com.akirkpatrick.vnt.btree.StoredEntry
import com.akirkpatrick.vnt.btree.eachKey
import com.akirkpatrick.vnt.btree.eachChild
import com.akirkpatrick.vnt.btree.EntryImpl

public open class Serializer<T: Comparable<T>>(val raf: RandomAccessFile) {

    class object {
        fun <T: Comparable<T>> fromFilename(filename : String) : Serializer<T> {
            return Serializer(RandomAccessFile(filename, "rw"))
        }
    }

    fun readByte() : Byte {
        return raf.readByte()
    }

    fun readLong() : Long {
        return raf.readLong()
    }

    fun createRoot() : Entry<T> {
        return ProxyEntry.from(EntryImpl<T>(null))
    }

    fun createEntry(parent: Entry<T>?) : Entry<T> {
        return ProxyEntry.from(EntryImpl<T>(parent))
    }

    fun persist(e : Entry<T>) : Long {
        val entry = e as ProxyEntry<T>
        if ( !entry.mutable ) {
            return entry.offset
        }
        val lookup=HashMap<Any, Long>()
        entry.eachKey { lookup.put(it, writeKey(it)) }
        entry.eachChild { lookup.put(it, persist(it)) }

        val loc=raf.getFilePointer()

        raf.writeByte(entry.keyCount)
        entry.eachKey { raf.writeLong(lookup[it]!!) }
        raf.writeByte(entry.childCount)
        entry.eachChild { raf.writeLong(lookup[it]!!) }

        entry.offset=loc
        return loc
    }

    open fun writeKey(o : T) : Long {
        val loc=raf.getFilePointer()
        val fos=FileOutputStream(raf.getFD()!!)
        ObjectOutputStream(fos).writeObject(o)
        return loc
    }

    open fun readKey(offset: Long) : T {
        raf.seek(offset)
        val fis=FileInputStream(raf.getFD()!!)
        return ObjectInputStream(fis).readObject() as T
    }

    fun readEntry(loc : Long, parent : Entry<T>) : Entry<T> {
        return ProxyEntry.from(parent, this, loc)
    }

    fun readRoot(loc : Long) : Entry<T> {
        return ProxyEntry.from(null : Entry<T>?, this, loc)
    }

    fun seek(offset: Long) {
        raf.seek(offset)
    }

    fun close() {
        raf.close()
    }
}

