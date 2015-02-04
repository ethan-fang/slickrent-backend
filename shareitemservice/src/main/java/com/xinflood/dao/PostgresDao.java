package com.xinflood.dao;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.io.BaseEncoding;
import com.xinflood.db.DateTimeArgumentFactory;
import com.xinflood.db.ItemResultMapper;
import com.xinflood.db.PostgresUuidSqlArrayArgumentFactory;
import com.xinflood.db.SqlArray;
import com.xinflood.db.UserMapper;
import com.xinflood.domainobject.Item;
import com.xinflood.domainobject.User;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.Update;
import org.skife.jdbi.v2.sqlobject.stringtemplate.StringTemplate3StatementLocator;
import org.skife.jdbi.v2.tweak.HandleCallback;
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
            update.bind("rental_start", item.getRentalPeriod().lowerEndpoint());
            update.bind("rental_end", item.getRentalPeriod().upperEndpoint());
            update.bind("image_uuids", SqlArray.arrayOf(UUID.class, item.getImageUuids()));
            update.bind("user_id", userId);

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
    public List<Item> getItems(int numItems, int offset, Optional<UUID> userId) {
        HandleCallback<List<Item>> callback = handle -> {
            Query<Map<String, Object>> query = handle.createQuery("retrieve_items");
            query.bind("offset", offset);
            if(numItems >=0) {
                query.define("size", numItems);
            }

            if(userId.isPresent()) {
                query.bind("userId", userId.get()).define("userId", userId.get());
            }
            return query.map(ItemResultMapper.INSTANCE).list();
        };
        return dbi.withHandle(callback);
    }


    @Override
    public Optional<User> findUserByUsernameAndPassword(final String username, final String password) {
        HandleCallback<Optional<User>> callback = handle -> {
            List<User> queryResult = handle.createQuery("get_user_by_username").bind("username", username).map(UserMapper.INSTANCE).list();

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

    private String createNewAccessToken(String username) {
        String keySource = username + DateTime.now().toString() + ThreadLocalRandom.current().nextInt();
        return BaseEncoding.base64().encode(keySource.getBytes(Charsets.UTF_8));
    }
}
