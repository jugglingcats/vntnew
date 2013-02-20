package com.akirkpatrick.vnt.btree

import java.util.Arrays

public open class EntryImpl<T : Comparable<T>>(
        override var parent : Entry<T>?,
        override val order: Int=2,
        override val sort : Boolean = true) : Entry<T> {

    protected val keys : Array<T?> = Array<T?>(2 * order + 1, {null})
    protected val children : Array<Entry<T>?> = Array<Entry<T>?>(2 * order + 2, {null})

    public override var keyCount: Int = 0
    public override var childCount: Int = 0

    public override fun getKey(index : Int) : T = keys[index] as T
    public override fun getChild(index : Int) : Entry<T> = children[index] as Entry<T>

    override fun setAsRoot() {
        parent=null
    }

    override fun reparent(parent: Entry<T>) {
        this.parent=parent
    }

    class object {
        fun <T : Comparable<T>> from(other: Entry<T>) : Entry<T> {
            val value= EntryImpl<T>(other.parent, other.order, other.sort)
            var i=0; other.eachKey({value.keys[i++]=it})
            var j=0; other.eachChild({value.children[j++]=it})
            value.keyCount=other.keyCount
            value.childCount=other.childCount
            return value
        }
    }
    override fun compareTo(other: Entry<T>): Int {
        return firstKey.compareTo(other.firstKey)
    }

    override fun indexOf(value : T) : Int {
        for (i in 0..keyCount - 1) {
            if (keys[i].equals(value))
                return i
        }
        return -1
    }

    override fun indexOf(value : Entry<T>) : Int {
        for (i in 0..childCount - 1) {
            if (children[i].equals(value))
                return i
        }
        return -1
    }

    override fun addKey(value : T) : Unit {
        keys[keyCount++]=value
        if ( sort ) {
            Arrays.sort(keys, 0, keyCount)
        }
    }

    override fun removeKey(value : T) : Int {
        val index=indexOf(value)
        removeKey(index)
        return index
    }

    override fun removeKey(index : Int) : T {
        val value : T = keys[index] as T
        for (i in index+1..keyCount - 1) {
            keys[i-1]=keys[i]
        }
        keyCount--
        keys[keyCount] = null

        return value
    }

    override fun replaceKey(index : Int, value : T) {
        keys[index]=value
    }

    override fun replaceChild(index : Int, value : Entry<T>) {
        children[index]=value
    }

    override fun addChild(child : Entry<T>) : Boolean {
        children[childCount++] = child
        if ( sort ) {
            Arrays.sort(children, 0, childCount)
        }

        if ( child.parent == null ) {
            throw IllegalArgumentException("Parent must not be null!")
        }
        if ( child.parent != this ) {
            child.reparent(this)
//            throw IllegalArgumentException("Child cannot be moved to this parent")
        }
        return true
    }

    override fun removeChild(child : Entry<T>) : Int {
        val index = indexOf(child)
        removeChild(index)
        return index
    }

    override fun removeChild(index : Int) : Entry<T> {
        var value : Entry<T> = children[index] as Entry<T>
        for (i in index + 1..childCount - 1) {
            children[i - 1] = children[i]
        }
        childCount--
        children[childCount] = null

        return value
    }

}
