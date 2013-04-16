package com.akirkpatrick.vnt.btree

public class TreePrinter() {
    class object {
        fun getString<T: Comparable<T>>(tree: BTree<T>): String {
            return getString(tree.root, "", true)
        }
        private open fun getString<T: Comparable<T>>(node: Entry<T>, prefix: String, isTail: Boolean): String {
            var builder: StringBuilder = StringBuilder()
            builder.append(prefix).append(if (isTail) "└ " else "│ ")
            for (i in 0..node.keyCount - 1) {
                var value: T = node.getKey(i)
                builder.append(value)

                if (i < node.keyCount - 1)
                    builder.append(", ")

            }
            builder.append("\n")
            if (node.hasChildren)
            {
                for (i in 0..node.childCount - 1 - 1) {
                    var obj: Entry<T> = node.getChild(i)
                    builder.append(getString(obj, prefix + (if (isTail) "    " else "│   "), false))
                }
                if (node.childCount >= 1) {
                    var obj: Entry<T> = node.lastChild
                    builder.append(getString(obj, prefix + (if (isTail) "    " else "│   "), true))
                }
            }

            return builder.toString()
        }
    }
}
