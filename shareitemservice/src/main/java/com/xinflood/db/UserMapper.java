package com.xinflood.db;

import com.xinflood.domainobject.User;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Created by xinxinwang on 12/3/14.
 */
public class UserMapper implements ResultSetMapper<User> {
    @Override
    public User map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        UUID id = UUID.fromString(r.getString("id"));
        String username = r.getString("username");
        String password = r.getString("password");
        String accessToken = r.getString("access_token");

        return new User(id, username, password, accessToken);
    }
}
