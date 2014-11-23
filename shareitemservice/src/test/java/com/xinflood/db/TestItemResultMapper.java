package com.xinflood.db;

import com.xinflood.DaoHelper;
import com.xinflood.dao.PostgresShareItemDao;
import com.xinflood.domainobject.Item;
import org.junit.Test;

import java.util.List;

public class TestItemResultMapper {

    private PostgresShareItemDao postgresShareItemDao = DaoHelper.getPostgresShareItemDao();

    @Test
    public void testMapper() {
        List<Item> item = postgresShareItemDao.getItems(1);
    }
}

