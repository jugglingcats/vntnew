package com.akirkpatrick.vnt.btree.spec

import kotlin.test.assertEquals
import com.akirkpatrick.vnt.btree.Entry
import com.akirkpatrick.vnt.btree.BTree
import java.util.ArrayList

class ENTRYSPEC<T : Comparable<T>>(val keys: Array<T>) {
    val children = ArrayList<ENTRYSPEC<T>>()

    fun child(vararg s: T, init : ENTRYSPEC<T>.() -> Unit = {}) : ENTRYSPEC<T> {
        val entry= ENTRYSPEC<T>(s)
        entry.init()
        children.add(entry)
        return entry
    }

    fun validate(btree : BTree<T>) {
        val e=btree.root
        try {
            validate(e)
        } catch (e1 : Throwable) {
            println(btree.toString())
            throw e1
        }
    }

    fun validate(e: Entry<T>) {
        assertEquals(keys.size, e.keyCount)
        for (i in keys.indices) {
            assertEquals(keys[i], e.getKey(i))
        }
        assertEquals(children.size, e.childCount)
        for (j in children.indices) {
            children[j].validate(e.getChild(j))
        }
    }
}

fun entry<T : Comparable<T>>(vararg s: T, init : ENTRYSPEC<T>.() -> Unit = {}) : ENTRYSPEC<T> {
    val entry= ENTRYSPEC(s)
    entry.init()
    return entry
}

