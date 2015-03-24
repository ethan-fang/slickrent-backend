package com.xinflood;

import com.google.common.base.Optional;
import com.xinflood.dao.UserDao;
import com.xinflood.domainobject.SocialSignInRequest;
import com.xinflood.domainobject.User;
import com.xinflood.domainobject.UserProfile;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by xinxinwang on 3/23/15.
 */
public class UserController {
    private final UserDao userDao;
    private final ExecutorService executorService;


    public UserController(UserDao userDao, ExecutorService executorService) {
        this.userDao = userDao;
        this.executorService = executorService;
    }


    public Optional<UserProfile> getUserProfileByUserId(UUID userId) throws ExecutionException, InterruptedException {
        Future<Optional<UserProfile>> future = executorService.submit(new GetUserProfileTask(userDao, userId));
        return future.get();
    }

    public UUID createOrUpdateUserProfile(UserProfile userProfile, UUID userId) throws ExecutionException, InterruptedException {
        return executorService.submit(new CreateOrUpdateUserProfile(userDao, userProfile, userId)).get();
    }

    public Optional<User> findUserByUsernameAndPassword(String username, String password) throws ExecutionException, InterruptedException {
        return executorService.submit(new FindUserByUsernameAndPassword(userDao, username, password)).get();
    }

    public Optional<User> createNewUser(String username, String password) throws ExecutionException, InterruptedException {
        return executorService.submit(new CreateNewUser(userDao, username, password)).get();
    }

    public Optional<User> updateSocialLogin(SocialSignInRequest socialSignInRequest) throws ExecutionException, InterruptedException {
        return executorService.submit(new UpdateSocialLogin(userDao, socialSignInRequest)).get();
    }

    private static class FindUserByUsernameAndPassword implements Callable<Optional<User>> {

        private final UserDao userDao;
        private final String username;
        private final String password;

        private FindUserByUsernameAndPassword(UserDao userDao, String username, String password) {
            this.userDao = userDao;
            this.username = username;
            this.password = password;
        }


        @Override
        public Optional<User> call() throws Exception {
            return userDao.findUserByUsernameAndPassword(username, password);
        }
    }

    private static class UpdateSocialLogin implements Callable<Optional<User>> {
        private final UserDao userDao;
        private final SocialSignInRequest socialSignInRequest;

        private UpdateSocialLogin(UserDao userDao, SocialSignInRequest socialSignInRequest) {
            this.userDao = userDao;
            this.socialSignInRequest = socialSignInRequest;
        }


        @Override
        public Optional<User> call() throws Exception {
            return userDao.updateSocialLogin(socialSignInRequest);
        }
    }

    private static class CreateNewUser implements Callable<Optional<User>> {

        private final UserDao userDao;
        private final String username;
        private final String password;

        private CreateNewUser(UserDao userDao, String username, String password) {
            this.userDao = userDao;
            this.username = username;
            this.password = password;
        }


        @Override
        public Optional<User> call() throws Exception {
            return userDao.createNewUser(username, password);
        }
    }

    private static class GetUserProfileTask implements Callable<Optional<UserProfile>> {

        private final UserDao userDao;
        private final UUID userId;

        private GetUserProfileTask(UserDao userDao, UUID userId) {
            this.userDao = userDao;
            this.userId = userId;
        }

        @Override
        public Optional<UserProfile> call() throws Exception {
            return userDao.getUserProfileByUserId(userId);
        }
    }

    private static class CreateOrUpdateUserProfile implements  Callable<UUID> {

        private final UserDao userDao;
        private final UserProfile userProfile;
        private final UUID userId;

        private CreateOrUpdateUserProfile(UserDao userDao, UserProfile userProfile, UUID userId) {
            this.userDao = userDao;
            this.userProfile = userProfile;
            this.userId = userId;
        }

        @Override
        public UUID call() throws Exception {
            return userDao.createOrUpdateUserProfile(userProfile, userId);
        }
    }

}
