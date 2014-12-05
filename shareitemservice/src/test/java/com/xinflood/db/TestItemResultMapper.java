package com.xinflood.db;

import com.xinflood.DaoHelper;
import com.xinflood.dao.PostgresDao;
import com.xinflood.domainobject.Item;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestItemResultMapper {

    private PostgresDao postgresDao = DaoHelper.getPostgresShareItemDao();

    @Test
    public void testMapper() {
        List<Item> item = postgresDao.getItems(1);
        assertEquals(1, item.size());
    }
}

