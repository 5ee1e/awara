package me.rerere.awara.data.repo

import me.rerere.awara.data.dto.LoginReq
import me.rerere.awara.data.dto.ProfileDto
import me.rerere.awara.data.source.IwaraAPI

class UserRepo(private val iwaraAPI: IwaraAPI) {
    suspend fun login(
        email: String,
        password: String
    ) = iwaraAPI.login(
        LoginReq(
            email = email,
            password = password
        )
    )

    suspend fun renewToken() = iwaraAPI.renewToken()

    suspend fun getSelfProfile() = iwaraAPI.getSelfProfile()

    suspend fun getProfile(
        username: String
    ): ProfileDto = iwaraAPI.getProfile(username)

    suspend fun followUser(id: String) = iwaraAPI.followUser(id)

    suspend fun unfollowUser(id: String) = iwaraAPI.unfollowUser(id)

    suspend fun getFollowerCount(userId: String) = iwaraAPI.getUserFollowers(userId, mapOf(
        "limit" to "1"
    )).count

    suspend fun getFollowingCount(userId: String) = iwaraAPI.getUserFollowing(userId, mapOf(
        "limit" to "1"
    )).count

    suspend fun getFollowing(userId: String, page: Int) = iwaraAPI.getUserFollowing(userId, mapOf(
        "page" to page.toString(),
    ))

    suspend fun getFollowers(userId: String, page: Int) = iwaraAPI.getUserFollowers(userId, mapOf(
        "page" to page.toString(),
    ))

    suspend fun getFriendCount(userId: String) = iwaraAPI.getUserFriends(userId, mapOf(
        "limit" to "1"
    )).count

    suspend fun getFriendsStatus(userId: String) = iwaraAPI.getFriendStatus(userId)

    suspend fun addFriend(userId: String) = iwaraAPI.addFriend(userId)

    suspend fun removeFriend(userId: String) = iwaraAPI.removeFriend(userId)

    suspend fun getUserFriends(userId: String, page: Int) = iwaraAPI.getUserFriends(userId, mapOf(
        "page" to page.toString(),
    ))

    suspend fun getFriendRequests(userId: String, page: Int) = iwaraAPI.getUserFriendRequests(userId, mapOf(
        "page" to page.toString(),
    ))

    suspend fun getNotificationCounts() = iwaraAPI.getNotificationCount()
}