package com.akirkpatrick.vnt.repository

import java.io.RandomAccessFile
import java.util.UUID

class RepoDump() : Repository {
    override fun get(id: UUID): Node {
        throw UnsupportedOperationException()
    }
    fun main(args : Array<String>) {
        dump("test.bin")
    }

    override val storage: RepositoryImpl.Refs get() {
        throw UnsupportedOperationException()
    }

    override fun createNode(id: UUID): Node {
        throw UnsupportedOperationException()
    }

    override fun newNode(node: Node) {
        throw UnsupportedOperationException()
    }

    fun dump(filename: String) {
        val raf=RandomAccessFile(filename, "rw")
        try {
            println("== DUMPING REPOSITORY: '${filename}'")

            val position= RepositoryHeader().find(raf)
            //        raf.seek(position)
            //
            //        val nodesOffset=raf.readLong()
            //        if ( nodesOffset < 0 ) {
            //            println("** REPOSITORY HAS NO DATA - END **")
            //            return
            //        }
            //
            //        println("=== NODE BTREE OFFSET: ${nodesOffset}")
            //
            //        val store=NodeStore(raf, this)
        } finally {
            raf.close()
        }
    }
}