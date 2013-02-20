package com.akirkpatrick.vnt

import org.junit.Test
import com.akirkpatrick.vnt.btree.BTree
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import com.akirkpatrick.vnt.btree.EntryImpl
import kotlin.test.assertTrue
import kotlin.test.assertFalse

public class BTreeTests() {
    Test fun testMe() : Unit {
        var btree : BTree<String> = BTree({ EntryImpl(it)})

        btree.add("TEST")

        for (n in 1..100) {
            var t : String = getRandomString()
            btree.add(t)
        }
        btree.remove("TEST")

        System.out.println(btree.toString())
        assertEquals(100, btree.size())
    }

    private fun getRandomString() : String {
        var t : String = ""
        for (i in 0..4 - 1) {
            val c = (65 + (Math.round(Math.random() * 25))).toChar()
            var x : CharArray = charArray(c)
            t += String(x)
        }
        return t
    }

    Test fun testFind() {
        var btree : BTree<String> = BTree({ EntryImpl(it)})
        for (i in 0..25) {
            val c = (65 + i).toChar().toString()
            btree.add(c)
        }
        System.out.println(btree.toString())
        assertNotNull(btree.contains("J"))
    }

    Test fun testFind2() {
        // this was causing npe
        val btree = BTree<Int>({ EntryImpl(it)})
        for ( i in 1..100 ) {
            btree.add(i)
        }
        assertTrue(btree.contains(56))
    }

    Test fun testFindEmpty() {
        // this was causing npe
        val btree = BTree<Int>({ EntryImpl(it)})
        assertFalse(btree.contains(56))
    }

}
