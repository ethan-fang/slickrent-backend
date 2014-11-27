package com.xinflood.db;

import com.xinflood.DaoHelper;
import com.xinflood.dao.PostgresShareItemDao;
import com.xinflood.domainobject.Item;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestItemResultMapper {

    private PostgresShareItemDao postgresShareItemDao = DaoHelper.getPostgresShareItemDao();

    @Test
    public void testMapper() {
        List<Item> item = postgresShareItemDao.getItems(1);
        assertEquals(1, item.size());
    }
}

