package com.akirkpatrick.vnt.repository

import com.akirkpatrick.vnt.btree.BTree
import com.akirkpatrick.vnt.store.Serializer
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.io.Serializable
import java.io.ObjectOutputStream
import java.util.UUID
import com.akirkpatrick.vnt.node.Property

public class RepositoryImpl(filename: String): Repository {
    val raf = RandomAccessFile(filename, "rw")
    override val storage = init()

    class object {
        fun create(filename: String) {
            val f: File = File(filename)
            if ( !f.createNewFile() ) {
                throw IllegalStateException(filename)
            }

            val raf = RandomAccessFile(filename, "rw")
            try {
                RepositoryHeader().write(raf)
            } finally {
                raf.close()
            }
        }
    }

    class Refs(val refstore: Serializer<UUID>, val propser : Serializer<Property>, val nodes: Info<Node>)
    private class Info<T: Comparable<T>>(val btree: BTree<T>, val store: Serializer<T>)

    val nodes: BTree<Node> get() {
        return storage.nodes.btree
    }

    private fun init(): Refs {
        val position = RepositoryHeader().find(raf)
        raf.seek(position)

        val nodesOffset = raf.readLong()

        val store = NodeSerializer(raf, this, DefaultNodeTypeMapper(this))
        val pser = PropertySerializer(raf)
        val root = if (nodesOffset >= 0) store.readRoot(nodesOffset) else store.createRoot()
        val btree = BTree<Node>({ store.createEntry(it) }, root)

        return Refs(UUIDSerializer(raf), pser, Info<Node>(btree, store))
    }

    fun close() {
        raf.close()
    }

    override fun createNode(id: UUID): Node {
        val node = Node(this, id)
        if ( nodes.contains(node) ) {
            throw IllegalArgumentException("Node '${id}' already exists")
        }
        nodes.add(node)
        return node
    }

    override fun newNode(node: Node) {
        if ( nodes.contains(node) ) {
            throw IllegalArgumentException("Node '${node.id}' already exists")
        }
        nodes.add(node)
    }

    override fun get(id: UUID): Node {
        return nodes.find( { id.compareTo(it.id) } )!!
    }

    fun dump() {
        println(nodes.toString())
    }

    fun commit() {
        val loc = storage.nodes.store.persist(nodes.getRoot())
        RepositoryHeader().write(raf, loc)
    }
}