package com.xinflood.dao;

import com.amazonaws.services.s3.model.PutObjectResult;

import java.io.IOException;

/**
 * Created by xinxinwang on 11/16/14.
 */
public interface ImageDao {

    PutObjectResult putImage(String key, byte[] image) throws IOException;
    byte[] getImage(String key) throws IOException;
}
