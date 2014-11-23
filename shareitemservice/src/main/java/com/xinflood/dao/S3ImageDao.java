package com.xinflood.dao;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.google.common.io.ByteStreams;
import com.google.common.net.MediaType;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by xinxinwang on 11/16/14.
 */
public class S3ImageDao implements ImageDao {
    private final AmazonS3 s3Client;
    private final String bucketName;

    public S3ImageDao(AmazonS3 s3Client, String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    @Override
    public PutObjectResult putImage(String key, byte[] inputStream) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(MediaType.PNG.toString());
        objectMetadata.setContentLength(inputStream.length);
        return s3Client.putObject(bucketName, key, new ByteArrayInputStream(inputStream), objectMetadata);
    }

    public byte[] getImage(String key) throws IOException {
        S3Object s3Object = s3Client.getObject(new GetObjectRequest(
                bucketName, key));

        return ByteStreams.toByteArray(s3Object.getObjectContent());
    }

//    private static void displayTextInputStream(InputStream input)
//            throws IOException {
//        // Read one text line at a time and display.
//        BufferedReader reader = new BufferedReader(new
//                InputStreamReader(input));
//        while (true) {
//            String line = reader.readLine();
//            if (line == null) break;
//
//            System.out.println("    " + line);
//        }
//        System.out.println();
//    }
}
