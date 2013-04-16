package com.akirkpatrick.vnt.btree

import com.akirkpatrick.vnt.store.Serializer

public class StoredEntry<T : Comparable<T>>(val serializer: Serializer<T>, val offset: Long, parent : Entry<T>?, order : Int)
            : EntryImpl<T>(parent, order=order) {

    val keyRefs : Array<Long> = loadKeyRefs();
    val childRefs: Array<Long> = loadChildRefs();

    fun loadKeyRefs() : Array<Long> {
        serializer.seek(offset)
        val keyCount= serializer.readByte()
        return Array<Long>(keyCount.toInt(), { serializer.readLong()})
    }

    fun loadChildRefs() : Array<Long> {
        val childCount= serializer.readByte()
        return Array<Long>(childCount.toInt(), { serializer.readLong()})
    }

    public override var keyCount: Int = keyRefs.size

    public override fun getKey(index: Int): T {
        if ( keys[index] == null ) {
            val value= serializer.readKey(keyRefs[index])
            keys[index]=value;
        }
        return super.getKey(index)
    }

    public override var childCount: Int = childRefs.size

    public override fun getChild(index: Int): Entry<T> {
        return getChild(index, this)
    }

    public fun getChild(index: Int, parent: Entry<T>) : Entry<T> {
        if ( children[index] == null ) {
            val value= serializer.readEntry(childRefs[index], parent)
            children[index]=value;
        }
        return super.getChild(index)
    }
}