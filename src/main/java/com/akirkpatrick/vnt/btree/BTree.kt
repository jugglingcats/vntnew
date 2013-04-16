package com.akirkpatrick.vnt.btree

import java.util.Comparator
import java.util.Arrays
import com.akirkpatrick.vnt.repository.RepositoryHeader.Matcher
import java.util.ArrayList
import java.util.Collections

public class BTree<T: Comparable<T>>(val factory: (Entry<T>?) -> Entry<T>, root: Entry<T>? = null, val order: Int=2) : Iterable<T> {
    private var minChildrenSize: Int = order + 1
    private var maxKeySize: Int = 2 * order
    private var maxChildrenSize: Int = maxKeySize + 1
    private var rootInternal: Entry<T> = if ( root != null ) root else factory(null)
    private var size: Int = 0

    var ordered : Boolean = true

    val root: Entry<T> get() { return rootInternal }

    public fun add(value: T): Unit {
        var entry: Entry<T> = rootInternal
        while (true) {
            if (!entry.hasChildren) {
                entry.addKey(value)
                if (entry.keyCount > maxKeySize) {
                    split(entry)
                }
                break
            } else {
                entry = findChildEntryForValue(entry, value)
            }
        }
        size++
    }

    public fun findEntry(value: T): Entry<T>? {
        var entry: Entry<T> = rootInternal
        @main while (true) {
            val i = KeyIterator(entry)
            for ( span in i ) {
                if ( span.equals(value) ) {
                    return entry
                } else if ( span.spans(value) ) {
                    entry = span.child
                    continue @main
                }
            }
            return null
        }
    }

    public fun find(f: (value:T) -> Int) : T? {
        var entry: Entry<T> = rootInternal
        var k=0
        while (true) {
            val key=entry.getKey(k)
            when ( f(key) ) {
                0 -> return(key)
                -1 -> {
                    if ( entry.hasChildren ) {
                        entry=entry.getChild(k)
                        k=0
                        continue
                    } else {
                        return null
                    }
                }
                else -> {
                    if ( k == entry.keyCount-1 ) {
                        if ( entry.hasChildren ) {
                            entry=entry.getChild(k+1)
                            k=0
                            continue
                        } else {
                            return null
                        }
                    }
                }
            }
            k++
        }
    }

    private fun findChildEntryForValue(entry: Entry<T>, value: T): Entry<T> {
        val i = KeyIterator(entry)
        for ( span in i ) {
            if ( span.spans(value) ) {
                return span.child
            }
        }
        throw IllegalStateException("Failed to find child entry for value!")
    }

    private fun split(entry: Entry<T>): Unit {
        val medianIndex: Int = entry.keyCount / 2
        val medianValue = entry.getKey(medianIndex)

        fun prepare(): Entry<T> {
            if ( entry.parent == null ) {
                rootInternal = factory(null)
                return rootInternal
            }
            entry.parent!!.removeChild(entry)
            return entry.parent!!
        }
        //      [C k C k C] | k | [C k C k C]
        //                    ^ median key
        val parent = prepare()

        val left = factory(parent)
        entry.collectKeys(0, medianIndex - 1, { left.addKey(it) })
        entry.collectChildren(0, medianIndex - 1, {
            it.reparent(left)
            left.addChild(it)
        })

        val right = factory(parent)
        entry.collectKeys(medianIndex + 1, entry.keyCount - 1, { right.addKey(it) })
        entry.collectChildren(medianIndex + 1, entry.keyCount - 1, {
            it.reparent(right)
            right.addChild(it)
        })

        parent.addKey(medianValue)
        parent.addChild(left)
        parent.addChild(right)
        if (parent.keyCount > maxKeySize) {
            split(parent)
        }
    }

    fun contains(value: T): Boolean = findEntry(value) != null

    public fun remove(value: T) {
        var entry: Entry<T> = this.findEntry(value) as Entry<T>

        val index = entry.indexOf(value)
        if (entry.hasChildren) {
            //      C k C k C k C
            //        ^ removed key, index = 0

            val left: Entry<T> = entry.getChild(index)
            val lastEntry: Entry<T> = getRightLeaf(left)
            val replaceValue: T = lastEntry.removeKey(lastEntry.keyCount - 1)
            entry.replaceKey(index, replaceValue)

            if (/* lastEntry.parent != null && */ lastEntry.keyCount < order) {
                // underflow
                combine(lastEntry)
            }
            if (lastEntry.childCount > maxChildrenSize) {
                split(lastEntry)
                // TODO: understand this better
                throw IllegalArgumentException("unexpected overflow of child nodes - look into")
            }
        } else {
            //      k k k
            //        ^ removed key, index = 1
            entry.removeKey(index)
            if (entry.parent != null && entry.keyCount < order) {
                this.combine(entry)
            }
        }
        size--
    }

    private fun getRightLeaf(start: Entry<T>): Entry<T> {
        var entry = start
        while ( entry.hasChildren ) {
            entry = entry.lastChild
        }
        return entry
    }

    private fun combine(entry: Entry<T>) {
        val parent: Entry<T> = entry.parent as Entry<T>
        val index: Int = parent.indexOf(entry)

        // TODO: is it better to pick the most or least populated sibling?
        val mergeIndex = selectMergeEntry(parent, index)
        val mergeEntry = parent.getChild(mergeIndex)

        val seq=if ( mergeIndex < index ) array(mergeEntry, entry) else array(entry, mergeEntry)
        val keyIndex=if ( mergeIndex < index ) index-1 else index

        if ( entry.keyCount + mergeEntry.keyCount + 1 <= maxKeySize ) {
            // push key down into a new entry
            val newEntry=factory(parent);
            seq[0].eachKey {newEntry.addKey(it)}
            newEntry.addKey(parent.getKey(keyIndex))
            seq[1].eachKey {newEntry.addKey(it)}

            seq[0].eachChild {newEntry.addChild(it)}
            seq[1].eachChild {newEntry.addChild(it)}

            parent.removeKey(keyIndex)
            if ( mergeIndex < index ) {
                parent.removeChild(index)
                parent.replaceChild(mergeIndex, newEntry)
            } else {
                parent.removeChild(mergeIndex)
                parent.replaceChild(index, newEntry)
            }
            if ( parent.keyCount < order ) {
                if ( parent.keyCount == 0 ) {
                    rootInternal =newEntry
                } else {
                    combine(parent)
                }
            }
        } else {
            // TODO: create test case where balancing entries with children is needed - broken
            // need to balance the two entries // this is the less expensive operation
            val keylist=ArrayList<T>()
            seq[0].eachKey { keylist.add(it) }
            keylist.add(parent.getKey(keyIndex))
            seq[1].eachKey { keylist.add(it) }

            val childlist=ArrayList<Entry<T>>()
            seq[0].eachChild { childlist.add(it) }
            seq[1].eachChild { childlist.add(it) }

            val median=keylist.size / 2
            val leftEntry=factory(parent)
            for ( n in 0..median-1 ) {
                leftEntry.addKey(keylist[n])
                if ( !childlist.isEmpty() )
                    leftEntry.addChild(childlist[n])
            }
            if ( !childlist.isEmpty() )
                leftEntry.addChild(childlist[median])

            val rightEntry=factory(parent)
            for ( n in median+1..keylist.size()-1 ) {
                rightEntry.addKey(keylist[n])
                if ( !childlist.isEmpty() )
                    rightEntry.addChild(childlist[n])
            }
            if ( !childlist.isEmpty() )
                rightEntry.addChild(childlist.last())

            if ( mergeIndex < index ) {
                parent.replaceChild(mergeIndex, leftEntry)
                parent.replaceChild(index, rightEntry)
            } else {
                parent.replaceChild(index, leftEntry)
                parent.replaceChild(mergeIndex, rightEntry)
            }
            parent.replaceKey(keyIndex, keylist[median])
        }
    }

    private fun selectMergeEntry(parent : Entry<T>, index : Int) : Int {
        val leftcount=if ( index > 0 ) parent.getChild(index-1).keyCount else 0
        val rightcount=if ( index < parent.keyCount ) parent.getChild(index+1).keyCount else 0

        assert(leftcount > 0 || rightcount > 0) // otherwise entry has no siblings

        if ( leftcount > rightcount ) {
            return index-1
        }
        return index+1
    }

    private fun getIndexOfPreviousValue(entry: Entry<T>, value: T): Int {
        for (i in 1..entry.keyCount - 1) {
            val t: T = entry.getKey(i)
            if (t.compareTo(value) >= 0)
                return i - 1
        }
        return entry.keyCount - 1
    }

    private fun getIndexOfNextValue(entry: Entry<T>, value: T): Int {
        for (i in 0..entry.keyCount - 1) {
            var t: T = entry.getKey(i)
            if (t.compareTo(value) >= 0)
                return i
        }
        return entry.keyCount - 1
    }

    public fun size(): Int {
        return size
    }

    public fun toString(): String? {
        return TreePrinter.getString(this)
    }

    public override fun iterator(): Iterator<T> {
        return BTreeIterator<T>(this)
    }
}


