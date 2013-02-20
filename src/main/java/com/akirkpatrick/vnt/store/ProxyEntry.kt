package com.akirkpatrick.vnt.store

import com.akirkpatrick.vnt.btree.Entry
import com.akirkpatrick.vnt.btree.EntryImpl
import com.akirkpatrick.vnt.btree.collectKeys
import com.akirkpatrick.vnt.btree.collectChildren
import com.akirkpatrick.vnt.btree.eachKey
import com.akirkpatrick.vnt.btree.eachChild
import com.akirkpatrick.vnt.btree.StoredEntry
import java.util.HashMap

public class ProxyEntry<T: Comparable<T>> private (var entry: Entry<T>): Entry<T> by entry {
    var mutable = true
    var _offset: Long = 0

    class object {
        fun <T: Comparable<T>> from(parent: Entry<T>?, store: Serializer<T>, offset: Long): Entry<T> {
            val entry = StoredEntry(store, offset, parent, 3)
            val proxy = ProxyEntry(entry)
            proxy.offset=offset
            return proxy
        }

        fun <T: Comparable<T>> from(entry: Entry<T>): Entry<T> {
            return ProxyEntry(entry)
        }
    }

    public override fun compareTo(other: Entry<T>): Int = entry.compareTo(other)

    public override fun getChild(index: Int) : Entry<T> {
        return entry.getChild(index)
    }

    private fun checkModifiable() {
        if ( !mutable ) {
            // entry is already stored, make a modifiable copy
            entry = EntryImpl.from(entry)
            mutable = true
            if ( parent != null ) {
                val p : ProxyEntry<T>? = parent as? ProxyEntry<T>?
                if (p != null)
                    p.checkModifiable()
            }
        }
    }

    // MUTATION METHODS
    override fun addKey(value: T) {
        checkModifiable()
        return entry.addKey(value)
    }
    override fun addChild(child: Entry<T>): Boolean {
        checkModifiable()
        return entry.addChild(child)
    }
    override fun removeChild(child: Entry<T>): Int {
        checkModifiable()
        return entry.removeChild(child)
    }
    override fun removeChild(index: Int): Entry<T> {
        checkModifiable()
        return entry.removeChild(index)
    }
    override fun removeKey(value: T): Int {
        checkModifiable()
        return entry.removeKey(value)
    }
    override fun removeKey(index: Int): T {
        checkModifiable()
        return entry.removeKey(index)
    }
    override fun replaceKey(index: Int, value: T) {
        checkModifiable()
        return entry.replaceKey(index, value)
    }
    override fun replaceChild(index: Int, value: Entry<T>) {
        checkModifiable()
        return entry.replaceChild(index, value)
    }

    var offset: Long
        get() {
            return _offset
        }
        set(offset: Long) {
            _offset = offset
            mutable = false
        }

    fun equals(other: Any?) : Boolean {
        return this === other || entry == other
    }
}