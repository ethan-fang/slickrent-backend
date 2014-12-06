package com.xinflood.auth;

import com.google.common.base.Optional;
import com.xinflood.dao.UserDao;
import com.xinflood.domainobject.User;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

/**
 * Created by xinxinwang on 12/6/14.
 */
public class BearerTokenOAuth2Provider implements Authenticator<String, User> {

    private final UserDao userDao;

    public BearerTokenOAuth2Provider(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Optional<User> authenticate(String token) throws AuthenticationException {
        return userDao.findUserByToken(token);
    }
}
