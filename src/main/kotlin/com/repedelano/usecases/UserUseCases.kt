package com.repedelano.usecases

import com.repedelano.dtos.Pager
import com.repedelano.dtos.user.UserRequest
import com.repedelano.dtos.user.UserResponse
import com.repedelano.dtos.user.UserResponseList
import com.repedelano.services.UserService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

fun interface AddUserUseCase {

    suspend fun addUser(user: UserRequest): Result<UserResponse?>
}

fun interface GetUserByIdUseCase {

    suspend fun get(id: Int): Result<UserResponse?>
}

fun interface SearchUserUseCase {

    suspend fun search(pager: Pager, user: UserRequest): Result<UserResponseList>
}

fun interface GetUsersUseCase {

    suspend fun getPage(pager: Pager): Result<UserResponseList>
}

fun interface UpdateUserUseCase {

    suspend fun update(id: Int, user: UserRequest): Result<UserResponse?>
}

fun addUserUseCase(
    dispatcher: CoroutineDispatcher,
    userService: UserService
) = AddUserUseCase { user ->
    withContext(dispatcher) {
        userService.insert(user)
    }
}

fun getUserByIdUseCase(
    dispatcher: CoroutineDispatcher,
    userService: UserService
) = GetUserByIdUseCase { id ->
    withContext(dispatcher) {
        userService.selectById(id)
    }
}

fun searchUserUseCase(
    dispatcher: CoroutineDispatcher,
    userService: UserService
) = SearchUserUseCase{pager, user ->
    withContext(dispatcher) {
        userService.search(pager, user)
    }
}


fun getUsersUseCase(
    dispatcher: CoroutineDispatcher,
    userService: UserService
) = GetUsersUseCase { pager ->
    withContext(dispatcher) {
        userService.selectPage(pager)
    }
}

fun updateUserUseCase(
    dispatcher: CoroutineDispatcher,
    userService: UserService
) = UpdateUserUseCase { id, user ->
    withContext(dispatcher) {
        userService.update(id, user)
    }
}