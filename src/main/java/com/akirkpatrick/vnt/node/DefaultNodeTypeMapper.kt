package com.akirkpatrick.vnt.repository

import java.io.RandomAccessFile
import java.util.HashMap
import java.io.FileOutputStream
import java.io.FileInputStream
import java.io.ObjectOutputStream
import com.akirkpatrick.vnt.btree.BTree
import java.util.UUID
import com.akirkpatrick.vnt.write
import com.akirkpatrick.vnt.UUIDread
import com.akirkpatrick.vnt.node.Property

class DefaultNodeTypeMapper(val repository: Repository) {
    val lookup = HashMap<Class<out Node>, CustomSerializer>();

    {
        lookup.put(javaClass<Node>(),
                CustomSerializer(1,
                        {(raf, node) -> defaultWrite(raf, node) },
                        {(raf) -> defaultRead(raf) }
                ))
    }

    class CustomSerializer(val magic: Byte, val writer: (raf: RandomAccessFile, node: Node) -> Long,
                           val reader: (raf: RandomAccessFile) -> Node)

    fun write(n: Node, raf: RandomAccessFile): Long {
        return lookup[n.javaClass]!!.writer(raf, n)
    }

    fun read(raf: RandomAccessFile): Node {
        val magic = raf.readByte()
        val t = lookup.values().find({ it.magic == magic })
        if ( t == null ) {
            throw IllegalStateException()
        }
        return t.reader(raf)
    }

    fun defaultWrite(raf: RandomAccessFile, node: Node): Long {
        val btreeloc = writeChildren(node.childBTree)
        val proploc = writeProperties(node.propertyBTree)
        val loc = raf.getFilePointer()
        raf.writeByte(1)
        node.id.write(raf)
        raf.writeLong(btreeloc)
        raf.writeLong(proploc)
        return loc
    }

    fun defaultRead(raf: RandomAccessFile): Node {
        val id = UUIDread(raf)
        val btreeloc = raf.readLong()
        val proploc = raf.readLong()
        val node = Node(repository, id)
        if ( btreeloc >= 0 ) {
            val store = repository.storage.refstore
            val root = store.readRoot(btreeloc)
            val children = BTree<UUID>({ store.createEntry(it) }, root)
            node.childBTree=children
        }
        if ( proploc >= 0 ) {
            val store = repository.storage.propser
            val root = store.readRoot(proploc)
            val properties = BTree<Property>({ store.createEntry(it) }, root)
            node.propertyBTree=properties
        }
        return node
    }

    private fun writeChildren(btree: BTree<UUID>?): Long {
        if ( btree != null ) {
            return repository.storage.refstore.persist(btree.root)
        }
        return -1.toLong() // indicates that node has no children
    }

    private fun writeProperties(btree: BTree<Property>?): Long {
        if ( btree != null ) {
            return repository.storage.propser.persist(btree.root)
        }
        return -1.toLong() // indicates that node has no properties
    }
}