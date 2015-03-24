package com.xinflood.db;

import com.google.common.base.Optional;
import com.xinflood.domainobject.UserAddress;
import com.xinflood.domainobject.UserProfile;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Created by xinxinwang on 3/23/15.
 */
public class UserProfileResultMapper implements ResultSetMapper<UserProfile> {

    public static final UserProfileResultMapper INSTANCE = new UserProfileResultMapper();

    @Override
    public UserProfile map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        String addressLine1 = r.getString("address_line1");
        String addressLine2 = r.getString("address_line2");
        String city = r.getString("city");
        String state = r.getString("state");
        String zipCode = r.getString("zip_code");
        String countryCode = r.getString("country_code");

        String username = r.getString("username");
        String email = r.getString("email");
        UUID photoUuid = UUID.fromString(r.getString("photo_uuid"));
        String fullName = r.getString("full_name");
        String phoneNumber = r.getString("phone_number");


        UserAddress userAddress = new UserAddress(addressLine1, Optional.fromNullable(addressLine2), city, state, zipCode, countryCode);
        UserProfile userProfile = new UserProfile(username, email, photoUuid, fullName, phoneNumber, userAddress);
        return userProfile;
    }
}
