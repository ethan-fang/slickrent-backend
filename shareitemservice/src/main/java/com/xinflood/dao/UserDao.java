package com.xinflood.dao;

import com.google.common.base.Optional;
import com.xinflood.domainobject.SocialSignInRequest;
import com.xinflood.domainobject.User;
import com.xinflood.domainobject.UserProfile;

import java.util.UUID;

/**
 */
public interface UserDao {
    Optional<User> createNewUser(String username, String password);

    Optional<User> findUserByUsernameAndPassword(String username, String password);

    Optional<User> findUserByToken(String token);

    Optional<User> updateSocialLogin(SocialSignInRequest socialSignInRequest);
    Optional<UUID> updatePassword(UUID userId, String oldPassword, String newPassword);


    Optional<UserProfile> getUserProfileByUserId(UUID userId);
    UUID createOrUpdateUserProfile(UserProfile userProfile, UUID userId);
}
