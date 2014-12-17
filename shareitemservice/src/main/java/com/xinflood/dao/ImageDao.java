package com.xinflood.dao;

import java.io.IOException;

/**
 * Created by xinxinwang on 11/16/14.
 */
public interface ImageDao {

    boolean putImage(String key, byte[] image);
    byte[] getImage(String key) throws IOException;
}
