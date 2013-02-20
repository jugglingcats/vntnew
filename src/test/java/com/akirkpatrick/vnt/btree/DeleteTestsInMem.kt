package com.akirkpatrick.vnt.btree

import org.junit.Test
import kotlin.test.assertTrue
import java.util.ArrayList
import kotlin.test.assertEquals
import com.akirkpatrick.vnt.btree.spec.entry

/**
* see http://integrator-crimea.com/ddu0111.html, although we don't use the exact algorithms outlined there
*/


// find a way to re-run these tests with persistent storage
open public class DeleteTestsInMem {
    val order = 2

    open fun setup(): BTree<Char> {
        val btree = createBTree()
        populate(btree)
        return btree
    }

    open fun cleanup() {

    }

    fun createBTree(): BTree<Char> {
        return BTree({ EntryImpl(it, order = order) }, order = order)
    }

    fun populate(btree: BTree<Char>) {
        for ( n in array('P', 'C', 'G', 'T', 'X', 'A', 'B', 'D', 'E', 'J', 'K', 'N', 'O', 'Q', 'R', 'U', 'V', 'Y', 'Z', 'M', 'F', 'L', 'S') ) {
            btree.add(n)
        }

        entry('P') {
            child('C', 'G', 'M') {
                child('A', 'B')
                child('D', 'E', 'F')
                child('J', 'K', 'L')
                child('N', 'O')
            }
            child('T', 'X') {
                child('Q', 'R', 'S')
                child('U', 'V')
                child('Y', 'Z')
            }
        }.validate(btree)
    }

    Test fun case1() {
        val btree = setup()
        try {
            btree.remove('F')
            entry('P') {
                child('C', 'G', 'M') {
                    child('A', 'B')
                    child('D', 'E')
                    child('J', 'K', 'L')
                    child('N', 'O')
                }
                child('T', 'X') {
                    child('Q', 'R', 'S')
                    child('U', 'V')
                    child('Y', 'Z')
                }
            }.validate(btree)
        } finally {
            cleanup()
        }
    }

    Test fun case2a() {
        val btree = setup()
        try {
            btree.remove('M')
            entry('P') {
                child('C', 'G', 'L') {
                    child('A', 'B')
                    child('D', 'E', 'F')
                    child('J', 'K')
                    child('N', 'O')
                }
                child('T', 'X') {
                    child('Q', 'R', 'S')
                    child('U', 'V')
                    child('Y', 'Z')
                }
            }.validate(btree)
        } finally {
            cleanup()
        }
    }

    Test fun case2b() {
        val btree = setup()
        try {
            btree.remove('G')
            entry('P') {
                child('C', 'F', 'M') {
                    child('A', 'B')
                    child('D', 'E')
                    child('J', 'K', 'L')
                    child('N', 'O')
                }
                child('T', 'X') {
                    child('Q', 'R', 'S')
                    child('U', 'V')
                    child('Y', 'Z')
                }
            }.validate(btree)
        } finally {
            cleanup()
        }
    }

    Test fun case2c() {
        val btree = setup()
        try {
            btree.remove('F') // case 1
            btree.remove('C')
            entry('P') {
                child('G', 'M') {
                    child('A', 'B', 'D', 'E')
                    child('J', 'K', 'L')
                    child('N', 'O')
                }
                child('T', 'X') {
                    child('Q', 'R', 'S')
                    child('U', 'V')
                    child('Y', 'Z')
                }
            }.validate(btree)
        } finally {
            cleanup()
        }
    }

    Test fun case3b() {
        val btree = setup()
        try {
            btree.remove('F') // case 1
            btree.remove('C') // case 2c
            btree.remove('M')
            btree.remove('L')
            btree.remove('O')
            entry('E', 'P', 'T', 'X') {
                child('A', 'B', 'D')
                child('G', 'J', 'K', 'N')
                child('Q', 'R', 'S')
                child('U', 'V')
                child('Y', 'Z')
            }.validate(btree)
        } finally {
            cleanup()
        }
    }

    Test fun case_merge_with_child_nodes() {
        try {
            val btree = setup()
            btree.add('H')
            btree.add('I')
            entry('P') {
                child('C', 'G', 'J', 'M') {
                    child('A', 'B')
                    child('D', 'E', 'F')
                    child('H', 'I')
                    child('K', 'L')
                    child('N', 'O')
                }
                child('T', 'X') {
                    child('Q', 'R', 'S')
                    child('U', 'V')
                    child('Y', 'Z')
                }
            }.validate(btree)

            btree.remove('T')
            btree.remove('S')
            entry('M') {
                child('C', 'G', 'J') {
                    child('A', 'B')
                    child('D', 'E', 'F')
                    child('H', 'I')
                    child('K', 'L')
                }
                child('P', 'X') {
                    child('N', 'O')
                    child('Q', 'R', 'U', 'V')
                    child('Y', 'Z')
                }
            }.validate(btree)
        } finally {
            cleanup()
        }
    }
}
