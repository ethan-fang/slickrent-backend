package com.xinflood.dao;

import com.google.common.base.Optional;
import com.google.common.io.BaseEncoding;
import com.xinflood.db.UserMapper;
import com.xinflood.domainobject.User;
import org.joda.time.DateTime;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.Update;
import org.skife.jdbi.v2.sqlobject.stringtemplate.StringTemplate3StatementLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Example UserDAO, currently supports only two hard coded users.
 */
public class PostgresUserDao implements UserDao {


    private static final Logger LOG = LoggerFactory.getLogger(PostgresUserDao.class);
    private final DBI dbi;

    public PostgresUserDao(DBI dbi) {
        this.dbi = dbi;
        this.dbi.setStatementLocator(StringTemplate3StatementLocator.builder(this.getClass()).build());
    }

	public Optional<User> findUserByUsernameAndPassword(final String username, final String password) {
        Handle handle = dbi.open();
        List<User> queryResult = handle.createQuery("get_user_by_username").bind("username", username).map(new UserMapper()).list();

        Optional<User> user;
        if(queryResult.size() == 0) {
            user = Optional.absent();
        }
        else if(queryResult.size() == 1) {
            user = Optional.of(queryResult.get(0));
        } else {
            throw new RuntimeException("multiple users found for the given user name");
        }

        handle.close();
        return user;
	}


    @Override
    public Optional<User> createNewUser(String username, String password) {
        Handle handle = dbi.open();


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


        handle.close();
        return Optional.of(new User(userId, username, password, accessToken));
    }

    private String createNewAccessToken(String username) {
        String keySource = username + DateTime.now().toString() + ThreadLocalRandom.current().nextInt();
        return BaseEncoding.base64().encode(keySource.getBytes());
    }
}
