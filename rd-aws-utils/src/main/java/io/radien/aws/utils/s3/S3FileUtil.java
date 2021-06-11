/*
 * Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.radien.aws.utils.s3;

import io.radien.aws.utils.s3.exceptions.RemoteResourceNotAvailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * S3 Amazon Cloud Storage file utils
 *
 * @author Nuno Santana
 */
public class S3FileUtil {

    private static final Logger log = LoggerFactory.getLogger(S3FileUtil.class);

    protected S3FileUtil(){
    }

    /**
     * Will delete (if found) the requested file/path from the S3 Amazon Cloud
     * @param x path to be deleted
     * @throws IOException to be throw if not able to find/open path
     */
    public static void deleteIfExists(Path x) throws IOException {
        try {
            Files.delete(x);
        } catch (NoSuchFileException e) {
            log.info("File not found for deletion", e);
        }
    }

    /**
     * Retrieves requested file from the S3 Amazon Cloud
     * @param s3Region to lookup
     * @param s3Bucket current bucket to find the requested file
     * @param s3WorkDir working directory of the file
     * @param name file name to be found
     * @throws IOException to be throw if not able to find/open path
     * @throws RemoteResourceNotAvailableException in case the resource is not available
     * @throws FileNotFoundException in case the file is not found
     */
    public static void getS3FileWithName(String s3Region, String s3Bucket, String s3WorkDir, String name) throws IOException,RemoteResourceNotAvailableException {
        log.info("Starting transfer of the file {}", name);
        //Authorization is done in the machine scope and SDK takes care of get it

        try {
            S3Client s3 = S3Client.builder().region(Region.of(s3Region)).build();
            Path x = Paths.get(s3WorkDir
                    + File.separator + name);
            deleteIfExists(x);

            s3.getObject(GetObjectRequest.builder().bucket(s3Bucket).key(name).build(),
                    ResponseTransformer.toFile(x));
            log.info("Transfer of file {} finished with success", name);
        } catch (NoSuchBucketException e) {
            if (e.getCause() != null) {
                log.error(e.getCause().getMessage());
            }
            throw new RemoteResourceNotAvailableException(e.getMessage());
        } catch (NoSuchKeyException e) {
            String msg = String.format("Expected file %s, not found on bucket %s", name, s3Bucket);
            log.error(msg);
            log.error(e.getMessage());
            if (e.getCause() != null) {
                log.error(e.getCause().getMessage());
            }
            throw new FileNotFoundException(msg);
        } catch (SdkClientException e) {
            if (e.getCause() != null) {
                log.error(e.getCause().getMessage());
            }
            throw new RemoteResourceNotAvailableException(e.getMessage(),e);
        } catch (IOException e) {
            if (e.getCause() != null) {
                log.error(e.getCause().getMessage());
            }
            throw e;
        }


    }

    /**
     * Counts how many files start with the following prefix in the requested directory in the S3
     * @param s3Region to lookup
     * @param s3Bucket current bucket to find the requested file
     * @param s3WorkDir working directory of the file
     * @param prefix to be found
     * @return the count of all the files that the name start with the prefix value
     * @throws RemoteResourceNotAvailableException in case the remote resource is not found or not available
     * @throws IOException to be throw if not able to find/open path
     */
    public static int getS3FilesStartingWith(String s3Region, String s3Bucket,String s3WorkDir, String prefix) throws RemoteResourceNotAvailableException, IOException{
        try {
            int count = 0;

            ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                    .bucket(s3Bucket)
                    .maxKeys(5000)
                    .build();

            boolean done = false;
            S3Client s3 = S3Client.builder().region(Region.of(s3Region)).build();
            while (!done) {
                ListObjectsV2Response listObjectsV2Response = s3.listObjectsV2(listObjectsV2Request);
                for (S3Object content : listObjectsV2Response.contents()) {
                    if (content.key().startsWith(prefix)) {
                        getS3FileWithName(s3Region, s3Bucket, s3WorkDir, content.key());
                        count++;
                    }
                }
                if (listObjectsV2Response.nextContinuationToken() == null) {
                    done = true;
                }
                listObjectsV2Request = listObjectsV2Request.toBuilder()
                        .continuationToken(listObjectsV2Response.nextContinuationToken())
                        .build();
            }
            return count;
        }  catch (SdkClientException|NoSuchBucketException e) {
            if (e.getCause() != null) {
                log.error(e.getCause().getMessage());
            }
            throw new RemoteResourceNotAvailableException(e.getMessage(),e);
        }
    }
}
