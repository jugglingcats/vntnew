package com.akirkpatrick.vnt.repository

import java.io.RandomAccessFile
import java.io.EOFException

public class RepositoryHeader {
    val HEADER_START = "#VNT>"
    val HEADER_END = "<VNT#"
    val HEADER_SIZE: Long = (8+HEADER_START.length+HEADER_END.length).toLong()

    class ReverseInputStream(val raf: RandomAccessFile, val len: Long) {
        val BUFFER_SIZE=3
        var buf=ByteArray(BUFFER_SIZE)
        var offset : Long = 0
        var ptr=0
        var avail=0
        var init=true
        //                var prev=raf.length()

        val position: Long get() {
            return len - offset + avail - ptr
        }
        val EOF : Boolean get() {
            return position < 0
        }
        fun readByte() : Byte {
            if ( avail == 0 || ptr >= avail ) {
                fill()
            }
            val byte = buf[avail - ptr - 1]
            ptr++
            return byte
        }
        private fun fill() {
            val wanted : Int = if (len - offset  > BUFFER_SIZE) BUFFER_SIZE else (len-offset).toInt()
            if ( wanted == 0 )
                throw EOFException()

            if ( ptr >= avail ) {
                if ( !init && avail < BUFFER_SIZE ) {
                    // last buffer
                    throw EOFException()
                }
                val next=offset + BUFFER_SIZE
                offset=if ( next <= len ) next else len
            }
            init=false
            ptr=0

            raf.seek(len - offset);
            //                    prev=raf.getFilePointer()
            avail=raf.read(buf, 0, wanted)
        }
    }

    class Matcher(val value : String) {
        var ptr=0
        fun eat(c : Char) {
            if ( c == value[ptr] ) {
                ptr++
            } else if ( ptr > 0 ) {
                ptr=0
                eat(c)
            }
        }
        val found : Boolean get() {
            return ptr == value.length
        }
    }

    val String.reverse : String get() {
        val sb=StringBuilder(this.length)
        for ( i in 1..this.length ) {
            sb.append(this[this.length-i])
        }
        return sb.toString()
    }

    public fun find(raf : RandomAccessFile) : Long {
        val len=raf.length()
        if ( raf.length() < HEADER_SIZE ) {
            throw IllegalArgumentException("File is too small!")
        }

        val ris=ReverseInputStream(raf, len)
        val m=Matcher(HEADER_START.reverse)
        while ( !ris.EOF ) {
            m.eat(ris.readByte().toChar())
            if ( m.found )
                break
        }
        if ( !m.found ) {
            throw EOFException()
        }
        return ris.position + HEADER_START.length
    }

    fun write(raf : RandomAccessFile, loc : Long = -1.toLong()) {
        raf.writeBytes(HEADER_START);
        raf.writeLong(loc);
        raf.writeBytes(HEADER_END);
    }
}

