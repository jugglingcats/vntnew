package com.akirkpatrick.vnt.btree

class LOW_MATCH<T> : Comparable<T> {
    public override fun compareTo(other: T): Int = -1
}

class HIGH_MATCH<T> : Comparable<T> {
    public override fun compareTo(other: T): Int = 1
}

public class KeyIterator<T: Comparable<T>>(val entry : Entry<T>) : Iterator<KeySpan<T>> {
    val low_match=LOW_MATCH<T>()
    val high_match=HIGH_MATCH<T>()
    var index = 0
    var prevKey : Comparable<T> = low_match

    public override fun next(): KeySpan<T> {
        val nextKey = if (index < entry.keyCount)
            entry.getKey(index)
        else
            high_match

        val value=KeySpan(prevKey, nextKey, entry, index)
        prevKey=nextKey
        index++
        return value
    }
    public override fun hasNext(): Boolean {
        return entry.keyCount > 0 && index <= entry.keyCount
    }
}