package com.xinflood.dao;

import com.google.common.base.Optional;
import com.xinflood.domainobject.SocialSignInRequest;
import com.xinflood.domainobject.User;

/**
 */
public interface UserDao {
    Optional<User> createNewUser(String username, String password);

    Optional<User> findUserByUsernameAndPassword(String username, String password);

    Optional<User> findUserByToken(String token);

    Optional<User> updateSocialLogin(SocialSignInRequest socialSignInRequest);
}
