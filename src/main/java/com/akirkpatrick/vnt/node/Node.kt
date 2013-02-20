package com.akirkpatrick.vnt.repository

import com.akirkpatrick.vnt.btree.BTree
import java.util.UUID
import com.akirkpatrick.vnt.btree.EntryIterator
import com.akirkpatrick.vnt.btree.BTreeIterator
import com.akirkpatrick.vnt.MappingIterator
import com.akirkpatrick.vnt.EmptyIterator
import com.akirkpatrick.vnt.node.Property
import com.akirkpatrick.vnt.store.Serializer

open class Node(val repository: Repository, val id: UUID): Comparable<Node> {
    var childBTree: BTree<UUID>? = null
    var propertyBTree: BTree<Property>? = null

    fun addNode(id: UUID) : Node {
        if ( childBTree == null ) {
            childBTree =BTree<UUID>({repository.storage.refstore.createEntry(it)})
        }
        val child=Node(repository, id)
        childBTree!!.add(child.id)
        repository.newNode(child)
        return child
    }

    fun addProperty(name: String, value: String) {
        if ( propertyBTree == null ) {
            propertyBTree =BTree<Property>({repository.storage.propser.createEntry(it)})
        }
        propertyBTree!!.add(Property(name, value))
    }

    public override fun compareTo(other: Node): Int {
        return id.compareTo(other.id)
    }

    open fun toString() : String {
        return id.toString()
    }

    val children : Iterator<Node> get() {
        if (childBTree == null) {
            return EmptyIterator<Node>()
        }
        return MappingIterator<UUID, Node>(BTreeIterator(childBTree!!), { repository.get(it) })
    }

    val properties : Iterator<Property> get() {
        if ( propertyBTree == null ) {
            return EmptyIterator<Property>()
        }
        return BTreeIterator<Property>(propertyBTree!!)
    }
}

