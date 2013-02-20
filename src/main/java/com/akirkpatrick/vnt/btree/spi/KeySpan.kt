package com.akirkpatrick.vnt.btree

public class KeySpan<T: Comparable<T>>(val left : Comparable<T>, val right : Comparable<T>, val parent : Entry<T>, val childIndex : Int) {
    public fun spans(value : T) : Boolean {
        return parent.childCount > 0 && left.compareTo(value) < 0 && right.compareTo(value) > 0
    }
    public fun equals(value : T) : Boolean {
        return left.equals(value)
    }

    public val child : Entry<T> get() { return parent.getChild(childIndex) }
}
