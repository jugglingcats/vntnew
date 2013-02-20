package com.akirkpatrick.vnt

import com.akirkpatrick.vnt.repository.RepositoryImpl
import java.io.File
import org.junit.Before
import org.junit.Test
import java.util.UUID
import kotlin.test.assertTrue
import com.akirkpatrick.vnt.btree.ROOT_ID
import com.akirkpatrick.vnt.repository.Node

public class RepositoryTests {
    class object {
        Before fun setup() {
            val f=File("test.bin")
            if ( f.exists() && !f.delete() ) {
                throw IllegalStateException("Cannot delete file!!")
            }
            RepositoryImpl.create("test.bin")
        }
    }

    Test fun open() {
        RepositoryTests.setup()
        val r=RepositoryImpl("test.bin")
        try {
            val root=r.createNode(ROOT_ID)
            for (l in 1..10) {
                val n=root.addNode(UUID.randomUUID())
                n.addProperty("prop", "value${l}")
            }
            for (l in 1..100) {
                root.addProperty("prop${l}", "dummyval")
            }
            r.commit()
        } finally {
            r.close()
        }
    }

    Test fun dump() {
        val r=RepositoryImpl("test.bin")
        r.dump()
        val root=r.get(ROOT_ID)
        dump(root)
    }

    fun dump(node : Node, level : Int = 0) {
        for ( i in 0..level ) {
            print(" ")
        }
        println(node.toString())
        for ( p in node.properties ) {
            println(p.toString())
        }
        for ( c in node.children ) {
            dump(c, level+1)
        }
    }
}

