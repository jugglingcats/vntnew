package com.akirkpatrick.vnt

import org.junit.Test
import com.akirkpatrick.vnt.btree.*
import com.akirkpatrick.vnt.store.Serializer
import kotlin.test.assertTrue
import kotlin.test.assertEquals
import kotlin.test.assertFalse

public class StoreTests {
    Test fun basics() {
        val store : Serializer<String> = Serializer.fromFilename("test.bin")
        val e1 = store.createRoot()
        e1.addKey("blah")
        try {
            val loc = store.persist(e1)

            val e2: Entry<String> = store.readRoot(loc)
            assertTrue(e2.hasKeys)
            assertEquals(1, e2.keyCount)
            assertEquals("blah", e2.firstKey)
        } finally {
            store.close()
        }
    }

    Test fun children() {
        val store : Serializer<Int> = Serializer.fromFilename("test.bin")
        try {
            val root = store.createRoot()
            root.addKey(1)
            root.addKey(2)

            val c1 = store.createEntry(root)
            c1.addKey(3)
            val c2 = store.createEntry(root)
            c2.addKey(4)

            root.addChild(c1)
            root.addChild(c2)

            val loc = store.persist(root)
            println("root offset: " + loc)
//            store.writeHeader()

            val root2 = store.readRoot(loc)
            assertEquals(2, root2.keyCount)
            assertEquals(1, root2.firstKey)
            assertEquals(2, root2.childCount)

            val c1_2 = root2.firstChild
            assertEquals(3, c1_2.firstKey)

            val c2_2 = root2.lastChild
            assertEquals(4, c2_2.firstKey)
        } finally {
            store.close()
        }
    }

    Test fun modify() {
        val store : Serializer<Int> = Serializer.fromFilename("test.bin")
        try {
            val root = store.createRoot()
            root.addKey(1)
            root.addKey(2)

            val loc = store.persist(root)
            println("root offset: " + loc)
//            store.writeHeader()

            root.addKey(3)
        } finally {
            store.close()
        }
    }

    Test fun newTree() {
        val store : Serializer<Int> = Serializer.fromFilename("test.bin")
        try {
            val btree = BTree<Int>({ store.createEntry(it) })
            for ( i in 1..100 ) {
                btree.add(i)
            }
            assertTrue(btree.contains(56))
            val loc=store.persist(btree.getRoot())

            val root = store.readRoot(loc)
            val btree2=BTree<Int>({ store.createEntry(it) }, root)

            assertTrue(btree2.contains(56))
        } finally {
            store.close()
        }
    }

    Test fun modify2() {
        val store : Serializer<Int> = Serializer.fromFilename("test.bin")
        try {
            val btree = BTree<Int>({ store.createEntry(it) })
            for ( i in 1..100 ) {
                btree.add(i*2)
            }

            val loc = store.persist(btree.getRoot())
            println("root offset: " + loc)

            btree.add(98)

            val loc2 = store.persist(btree.getRoot())

            assertFalse(loc == loc2)
        } finally {
            store.close()
        }
    }

    Test fun simpleCreateStoreAdd() {
        val store : Serializer<Int> = Serializer.fromFilename("test.bin")
        try {
            val btree = BTree<Int>({ store.createEntry(it) })
            for ( i in 1..5 )
                btree.add(i)

            println(btree.toString())

            val loc = store.persist(btree.getRoot())

            btree.add(98)

            val loc2 = store.persist(btree.getRoot())

            assertFalse(loc == loc2)
        } finally {
            store.close()
        }
    }

}