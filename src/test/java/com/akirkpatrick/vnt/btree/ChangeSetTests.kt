package com.akirkpatrick.vnt.btree

import org.junit.Test

/**
 * Created with IntelliJ IDEA.
 * User: alfie
 * Date: 03/03/13
 * Time: 12:15
 * To change this template use File | Settings | File Templates.
 */
class ChangeSetTests() {
    Test fun simple() {
        var btree1 = BTree<Int>({ EntryImpl(it)})
        var btree2 = BTree<Int>({ EntryImpl(it)})
    }
}