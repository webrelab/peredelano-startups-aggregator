package com.repedelano.services

import com.repedelano.dtos.Pager
import com.repedelano.dtos.user.UserRequest
import com.repedelano.dtos.user.UserResponse
import com.repedelano.dtos.user.UserResponseList
import com.repedelano.extensions.page
import com.repedelano.flatMap
import com.repedelano.orm.helpers.toUser
import com.repedelano.orm.helpers.toUserResponseList
import com.repedelano.repositories.UserRepository

interface UserService {

    suspend fun insert(user: UserRequest): Result<UserResponse>
    suspend fun selectById(id: Int): Result<UserResponse>
    suspend fun selectByPassportId(passportId: String): Result<UserResponse>
    suspend fun selectByEmail(email: String): Result<UserResponse>
    suspend fun selectPage(pager: Pager): Result<UserResponseList>
    suspend fun update(id: Int, user: UserRequest): Result<UserResponse>
}

class UserServiceImpl(
    private val userRepository: UserRepository
) : UserService {

    override suspend fun insert(user: UserRequest) =
        userRepository.insert(user).flatMap { selectById(it!!) }

    override suspend fun selectById(id: Int) =
        userRepository.selectById(id).map { it!!.toUser() }

    override suspend fun selectByPassportId(passportId: String) =
        userRepository.selectByPassportId(passportId).map { it!!.toUser() }

    override suspend fun selectByEmail(email: String) =
        userRepository.selectByEmail(email).map { it!!.toUser() }

    override suspend fun selectPage(pager: Pager) =
        userRepository.selectAll().map { list ->
            list.page(pager).map { it.toUser() }.toUserResponseList(pager.page, list.size)
        }

    override suspend fun update(id: Int, user: UserRequest) =
        userRepository.update(id, user).flatMap { selectById(id) }
}