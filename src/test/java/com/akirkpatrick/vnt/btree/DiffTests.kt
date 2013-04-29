package com.akirkpatrick.vnt.btree

import org.junit.Test
import com.akirkpatrick.vnt.store.Serializer
import java.io.File
import java.io.RandomAccessFile
import java.util.Stack

/**
 * Created with IntelliJ IDEA.
 * User: alfie
 * Date: 03/03/13
 * Time: 12:15
 * To change this template use File | Settings | File Templates.
 */
class DiffTests() {
    private class Frame<T : Comparable<T>>(val entry: Entry<T>) {
        private var keyIndex = 0
        private var childIndex = 0

        var visitChild = entry.hasChildren

        fun nextKey() : T {
            val value= entry.getKey(keyIndex++)
            if ( entry.hasChildren ) {
                visitChild=true
            }
            return value
        }
        val hasChildren : Boolean get() {
            return entry.hasChildren
        }
        val exhausted : Boolean get() {
            return keyIndex == entry.keyCount && childIndex == entry.childCount
        }
        fun nextChild() : Entry<T> {
            visitChild=false
            return entry.getChild(childIndex++)
        }
    }

    Test fun simple() {
        val filename = "btree_delete_tests.bin"
        val f=File(filename)
        if ( f.exists() && !f.delete() ) {
            throw IllegalStateException("Cannot delete file!!")
        }
        val raf = RandomAccessFile(filename, "rw")
        val store=Serializer<Int>(raf)
        val root=store.createRoot()

        val btree1=BTree<Int>( {store.createEntry(it)}, root)
        for ( n in array(1,2,3,4,5,6,7,8,9,10,11,12) ) {
            btree1.add(n)
        }
        val loc=store.persist(btree1.root)
        val btree2=BTree<Int>( {store.createEntry(it)}, store.readRoot(loc) )

        val stack1 = Stack<Frame<Int>>();
        val stack2 = Stack<Frame<Int>>();

        stack1.push(Frame(btree1.root))
        stack2.push(Frame(btree2.root))

        while (true) {
            if ( stack1.empty || stack2.empty ) {
                break
            }
            if ( stack1.last().entry == stack2.last().entry )
        }
    }
}