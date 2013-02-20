package com.akirkpatrick.vnt.repository

import com.akirkpatrick.vnt.btree.BTree
import com.akirkpatrick.vnt.store.Serializer
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.RandomAccessFile
import java.util.UUID
import com.akirkpatrick.vnt.write
import com.akirkpatrick.vnt.UUIDread

class NodeSerializer(raf: RandomAccessFile, val repository : Repository, val mapper : DefaultNodeTypeMapper): Serializer<Node>(raf) {
    override fun writeKey(n: Node): Long {
        return mapper.write(n, raf)
    }

    override fun readKey(offset: Long): Node {
        raf.seek(offset)
        return mapper.read(raf)
    }
}