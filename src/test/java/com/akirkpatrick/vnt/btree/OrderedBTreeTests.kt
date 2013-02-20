package com.akirkpatrick.vnt.btree

import org.junit.Test
import kotlin.test.assertEquals
import com.akirkpatrick.vnt.btree.spec.entry

public class OrderedBTreeTests() {
    Test fun testBasic() : Unit {
        var btree : BTree<String> = BTree({ EntryImpl(it, sort=false)})

        btree.add("TEST")
        btree.add("AAA")

        entry("TEST", "AAA").validate(btree)
    }

    Test fun testWithSplit() : Unit {
        var btree : BTree<String> = BTree({ EntryImpl(it, sort=false)})

        btree.add("TEST")
        btree.add("AAA")
        btree.add("BBB")
        btree.add("CCC")
        btree.add("DDD")
        btree.add("EEE")
        btree.add("FFF")

        entry("BBB") {
            child("TEST","AAA")
            child("CCC", "DDD", "EEE", "FFF")
        }.validate(btree)
    }

    Test fun testRemove() : Unit {
        var btree : BTree<String> = BTree({ EntryImpl(it, sort=false)})

        btree.add("TEST")
        btree.add("AAA")
        btree.add("BBB")
        btree.add("CCC")
        btree.add("FFF")
        btree.add("DDD")
        btree.add("EEE")

        btree.remove("DDD")

        entry("BBB") {
            child("TEST","AAA")
            child("CCC", "FFF", "EEE")
        }.validate(btree)
    }
}