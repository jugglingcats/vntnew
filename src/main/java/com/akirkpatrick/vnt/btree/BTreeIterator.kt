package com.akirkpatrick.vnt.btree

import java.util.Stack

public class BTreeIterator<T: Comparable<T>>(btree : BTree<T>) : Iterator<T> {
    private inner class Frame(val node : Entry<T>) {
        private var keyIndex = 0
        private var childIndex = 0

        var visitChild = node.hasChildren

        fun nextKey() : T {
            val value=node.getKey(keyIndex++)
            if ( node.hasChildren ) {
                visitChild=true
            }
            return value
        }
        val hasChildren : Boolean get() {
            return node.hasChildren
        }
        val exhausted : Boolean get() {
            return keyIndex == node.keyCount && childIndex == node.childCount
        }
        fun nextChild() : Entry<T> {
            visitChild=false
            return node.getChild(childIndex++)
        }
    }

    private val stack : Stack<Frame> = Stack<Frame>();

    {
        // init func
        stack.push(Frame(btree.getRoot()))
    }

    val frame : Frame
        get() { return stack.last() }

    public override fun next(): T {
        while ( frame.hasChildren && frame.visitChild ) {
            stack.push(Frame(frame.nextChild()))
        }

        val value=frame.nextKey()

        while ( stack.size > 0 && frame.exhausted ) {
            stack.pop()
        }

        return value
    }

    public override fun hasNext(): Boolean {
        return stack.size > 0
    }
}