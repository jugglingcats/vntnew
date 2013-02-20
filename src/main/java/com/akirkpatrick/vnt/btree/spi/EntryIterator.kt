package com.akirkpatrick.vnt.btree

import java.util.Stack

public class EntryIterator<T: Comparable<T>>(btree : BTree<T>) : Iterator<Entry<T>> {
    private inner class Frame(val node : Entry<T>, var index : Int) {
        fun next() { index++ }
    }

    private val stack = Stack<Frame>();

    {
        // init func
        stack.push(Frame(btree.getRoot(), 0))
    }

    val frame : Frame
        get() { return stack.last() }

    public override fun next(): Entry<T> {
        val current=frame.node
        val index=frame.index

        if ( current.hasChildren && index < current.childCount ) {
            stack.push(Frame(current.getChild(index), 0))
        } else {
            stack.pop()
            if ( stack.size > 0 ) {
                frame.next()
            }
        }
        return current
    }

    public override fun hasNext(): Boolean {
        return stack.size > 0
    }

}