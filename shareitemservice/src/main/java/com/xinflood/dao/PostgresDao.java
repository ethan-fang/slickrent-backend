package com.xinflood.dao;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.io.BaseEncoding;
import com.xinflood.db.DateTimeArgumentFactory;
import com.xinflood.db.ItemResultMapper;
import com.xinflood.db.PostgresUuidSqlArrayArgumentFactory;
import com.xinflood.db.SqlArray;
import com.xinflood.db.UserMapper;
import com.xinflood.db.UserProfileResultMapper;
import com.xinflood.domainobject.Item;
import com.xinflood.domainobject.LoginPlatform;
import com.xinflood.domainobject.SocialSignInRequest;
import com.xinflood.domainobject.User;
import com.xinflood.domainobject.UserProfile;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.Update;
import org.skife.jdbi.v2.sqlobject.stringtemplate.StringTemplate3StatementLocator;
import org.skife.jdbi.v2.tweak.HandleCallback;
import org.skife.jdbi.v2.util.StringMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class PostgresDao implements ShareItemDao, UserDao {

    private static final Logger LOG = LoggerFactory.getLogger(PostgresDao.class);
    private final DBI dbi;

    public PostgresDao(DBI dbi) {
        this.dbi = dbi;

        this.dbi.setStatementLocator(StringTemplate3StatementLocator.builder(this.getClass()).build());
        this.dbi.registerArgumentFactory(DateTimeArgumentFactory.getDateTimeArgumentFactory(DateTimeZone.UTC));
        this.dbi.registerArgumentFactory(new PostgresUuidSqlArrayArgumentFactory());
    }


    @Override
    public void addShareItem(Item item, UUID userId) {
        HandleCallback<Void> callback = handle -> {
            Update update = handle.createStatement("add_item");
            update.bind("id", item.getId());
            update.bind("item_name", item.getItemName());
            update.bind("item_description", item.getItemDescription());
            update.bind("image_uuids", SqlArray.arrayOf(UUID.class, item.getImageUuids()));
            update.bind("user_id", userId);

            if(item.getPrice().isPresent()) {
                update.bind("price_usd_cent_per_min", item.getPrice().get().getAmount().getAmountMinorInt());
            } else {
                update.bind("price_usd_cent_per_min", 0);
            }

            if(item.getRentalPeriod().isPresent()) {
                update.bind("rental_start", item.getRentalPeriod().get().lowerEndpoint());
                update.bind("rental_end", item.getRentalPeriod().get().upperEndpoint());
            } else {
                update.bind("rental_start", (DateTime)null);
                update.bind("rental_end", (DateTime)null);
            }


            int rowsModified = update.execute();
            if(rowsModified != 1) {
                LOG.error("fail to upload item to db %s", item);
                throw new IllegalStateException("failed to upload item to db");
            }

            return null;
        };
        dbi.withHandle(callback);

    }

    @Override
    public void updateShareItem(Item item, UUID userId) {
        HandleCallback<Void> callback = handle -> {
            Update update = handle.createStatement("update_item");
            update.bind("id", item.getId());
            update.bind("item_name", item.getItemName());
            update.bind("item_description", item.getItemDescription());
            update.bind("image_uuids", SqlArray.arrayOf(UUID.class, item.getImageUuids()));

            if(item.getPrice().isPresent()) {
                update.bind("price_usd_cent_per_min", item.getPrice().get().getAmount().getAmountMinorInt());
            } else {
                update.bind("price_usd_cent_per_min", 0);
            }

            DateTime lowerBound = null;
            DateTime upperBound = null;
            if(item.getRentalPeriod().isPresent()) {
                if(item.getRentalPeriod().get().hasLowerBound()) {
                    lowerBound = item.getRentalPeriod().get().lowerEndpoint();
                }

                if(item.getRentalPeriod().get().hasUpperBound()) {
                    upperBound = item.getRentalPeriod().get().upperEndpoint();
                }
            }

            update.bind("rental_start", lowerBound);
            update.bind("rental_end", upperBound);


            int rowsModified = update.execute();
            if(rowsModified != 1) {
                LOG.error("fail to update item to db %s", item);
                throw new IllegalStateException("failed to update item to db");
            }

            return null;
        };
        dbi.withHandle(callback);

    }

    @Override
    public List<Item> getItems(int numItems, int offset, Optional<UUID> userId) {
        HandleCallback<List<Item>> callback = handle -> {
            Query<Map<String, Object>> query = handle.createQuery("retrieve_items");
            query.bind("offset", offset);
            if(numItems >=0) {
                query.define("size", numItems).bind("size", numItems);
            }

            if(userId.isPresent()) {
                query.bind("userId", userId.get()).define("userId", userId.get());
            }
            return query.map(ItemResultMapper.INSTANCE).list();
        };
        return dbi.withHandle(callback);
    }

    @Override
    public Optional<Item> getItem(UUID itemId) {
        HandleCallback<Optional<Item>> callback = handle -> {
            Query<Map<String, Object>> query = handle.createQuery("get_item");
            query.bind("itemId", itemId);


            List<Item> result = query.map(ItemResultMapper.INSTANCE).list();
            if(result.isEmpty()) {
                return Optional.absent();
            } else {
                return Optional.of(Iterators.getOnlyElement(result.iterator()));
            }
        };
        return dbi.withHandle(callback);
    }


    @Override
    public Optional<User> findUserByUsernameAndPassword(final String username, final String password) {
        HandleCallback<Optional<User>> callback = handle -> {
            List<User> queryResult =
                    handle
                    .createQuery("get_user_by_username_password")
                    .bind("username", username)
                    .bind("password", password)
                    .map(UserMapper.INSTANCE).list();

            Optional<User> user;
            if(queryResult.size() == 0) {
                user = Optional.absent();
            }
            else if(queryResult.size() == 1) {
                user = Optional.of(queryResult.get(0));
            } else {
                throw new RuntimeException("multiple users found for the given user name");
            }
            return user;

        };

        return dbi.withHandle(callback);
    }


    @Override
    public Optional<User> createNewUser(String username, String password) {
        HandleCallback<Optional<User>> callback = handle -> {
            Query<Map<String, Object>> query = handle.createQuery("get_user_by_username").bind("username", username);
            if(query.list().size() > 0) {
                return Optional.absent();
            }

            UUID userId = UUID.randomUUID();
            String accessToken = createNewAccessToken(username);

            Update update = handle.createStatement("add_user");
            update.bind("id", userId);
            update.bind("username", username);
            update.bind("password", password);
            update.bind("access_token", accessToken);
            update.execute();

            return Optional.of(new User(userId, username, password, accessToken));
        };

        return dbi.withHandle(callback);
    }

    @Override
    public Optional<User> findUserByToken(String token) {
        HandleCallback<Optional<User>> callback = handle -> {
            User user = handle.createQuery("get_user_by_token")
                              .bind("access_token", token)
                              .map(UserMapper.INSTANCE)
                              .first();
            
            return Optional.of(user);
        };

        return dbi.withHandle(callback);
    }

    @Override
    public Optional<User> updateSocialLogin(SocialSignInRequest socialSignInRequest) {
        HandleCallback<Optional<User>> callback = handle -> {
            Query<Map<String, Object>> query = handle.createQuery("upsert_user_social_login");

            String username = socialSignInRequest.getUsername();
            String password = null;
            String accessToken = socialSignInRequest.getToken();
            LoginPlatform loginPlatform = socialSignInRequest.getLoginPlatform();


            String updatedUserId =
                    Iterables.getOnlyElement(query.define("id", UUID.randomUUID())
                            .define("username", username)
                            .define("access_token", accessToken)
                            .define("login_platform", loginPlatform.getName())
                            .bind("username", username)
                            .bind("access_token", accessToken)
                            .bind("login_platform", loginPlatform.getName())
                            .map(new StringMapper())
                            .list()
            );

            return Optional.of(new User(UUID.fromString(updatedUserId), username, password, accessToken, loginPlatform));
        };

        return dbi.withHandle(callback);
    }



    @Override
    public Optional<UserProfile> getUserProfileByUserId(UUID userId) {
        HandleCallback<Optional<UserProfile>> callback = handle -> {
            Query<Map<String, Object>> query = handle.createQuery("get_user_profile_by_user_id");
            query.bind("user_id", userId);
            UserProfile userProfile = query.map(UserProfileResultMapper.INSTANCE).first();

            return Optional.fromNullable(userProfile);
        };

        return dbi.withHandle(callback);
    }

    @Override
    public UUID createOrUpdateUserProfile(UserProfile userProfile, UUID userId) {
        HandleCallback<UUID> callback = handle -> {
            Query<Map<String, Object>> query = handle.createQuery("upsert_user_profile");

            query.define("id", UUID.randomUUID())
                    .define("username", userProfile.getUsername())
                    .define("email", userProfile.getEmail())
                    .define("photo_uuid", userProfile.getPhotoUuid())
                    .define("full_name", userProfile.getFullName())
                    .define("phone_number", userProfile.getPhoneNumber())
                    .define("address_line1", userProfile.getUserAddress().getAddressLine1())
                    .define("address_line2", userProfile.getUserAddress().getAddressLine2().or(""))
                    .define("city", userProfile.getUserAddress().getCity())
                    .define("state", userProfile.getUserAddress().getState())
                    .define("zip_code", userProfile.getUserAddress().getZipCode())
                    .define("country_code", userProfile.getUserAddress().getCountryCode())
                    .define("user_id", userId)
                    .bind("username", userProfile.getUsername())
                    .bind("email", userProfile.getEmail())
                    .bind("photo_uuid", userProfile.getPhotoUuid())
                    .bind("full_name", userProfile.getFullName())
                    .bind("phone_number", userProfile.getPhoneNumber())
                    .bind("address_line1", userProfile.getUserAddress().getAddressLine1())
                    .bind("address_line2", userProfile.getUserAddress().getAddressLine2().or(""))
                    .bind("city", userProfile.getUserAddress().getCity())
                    .bind("state", userProfile.getUserAddress().getState())
                    .bind("zip_code", userProfile.getUserAddress().getZipCode())
                    .bind("country_code", userProfile.getUserAddress().getCountryCode())
                    .bind("user_id", userId)
                    .bind("updated_time", DateTime.now());


            String updatedUserProfileId = query.map(new StringMapper()).first();

            return UUID.fromString(updatedUserProfileId);
        };

        return dbi.withHandle(callback);
    }

    @Override
    public Optional<UUID> updatePassword(UUID userId, String oldPassword, String newPassword) {
        HandleCallback<Optional<UUID>> callback = handle -> {
            Update update = handle.createStatement("update_password");
            update.bind("user_id", userId);
            update.bind("old_password", oldPassword);
            update.bind("new_password", newPassword);

            int rowAffected = update.execute();

            if(rowAffected != 1) {
                return Optional.absent();
            } else {
                return Optional.of(userId);
            }
        };

        return dbi.withHandle(callback);
    }

    private String createNewAccessToken(String username) {
        String keySource = username + DateTime.now().toString() + ThreadLocalRandom.current().nextInt();
        return BaseEncoding.base64().encode(keySource.getBytes(Charsets.UTF_8));
    }
}
