package com.akirkpatrick.vnt.btree

import java.io.File
import com.akirkpatrick.vnt.store.Serializer
import java.io.RandomAccessFile

class DeleteTestsFromDisk : DeleteTestsInMem() {
    var raf : RandomAccessFile? = null

    override fun setup(): BTree<Char> {
        val filename = "btree_delete_tests.bin"
        val f=File(filename)
        if ( f.exists() && !f.delete() ) {
            throw IllegalStateException("Cannot delete file!!")
        }
        raf = RandomAccessFile(filename, "rw")
        val store=Serializer<Char>(raf!!)
        val root=store.createRoot()

        val btree=BTree<Char>( {store.createEntry(it)}, root, order )
        populate(btree)

        val loc=store.persist(btree.getRoot())

        return BTree<Char>( {store.createEntry(it)}, store.readRoot(loc), order )
    }

    override fun case1() {
        super<DeleteTestsInMem>.case1()
    }

//    override fun case2c() {
//        super<DeleteTestsInMem>.case2c()
//    }

    override fun cleanup() {
        if ( raf != null ) {
            raf!!.close()
        }
    }
}