package com.akirkpatrick.vnt.repository

import com.akirkpatrick.vnt.repository.RepositoryImpl.Refs
import java.util.UUID

public trait Repository {
    fun createNode(id : UUID) : Node
    fun newNode(node : Node)
    fun get(id: UUID): Node
    val storage : Refs
}