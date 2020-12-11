/*
 * Copyright (c) 2006-present openappframe.org & its legal owners. All rights reserved.
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
package io.radien.ms.ecm.legacy;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
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

/**
 * @author Marco Weiland
 */
@RequestScoped
public class S3FileUtil {
    private static final Logger log = LoggerFactory.getLogger(S3FileUtil.class);

    @Inject
    @ConfigProperty(name = "system.cms.workdir")
    private String s3WorkDir;

    @Inject
    @ConfigProperty(name = "system.cms.s3.region")
    private String s3Region;
    
    @Inject
    @ConfigProperty(name = "system.cms.s3.bucket")
    private String s3Bucket;
    
    @Inject
    @ConfigProperty(name = "system.cms.files.local")
    private String isFilesLocal;
    

    public URLClassLoader getClassLoaderForBucketFiles() throws MalformedURLException {
        File file = new File(s3WorkDir);
        URL[] urls = {file.toURI().toURL()};
        return new URLClassLoader(urls);
    }

    public boolean isLoadLocalFiles() {
        return Boolean.parseBoolean(isFilesLocal);
    }

    public void delete(Path x) throws IOException {
        try {
            Files.delete(x);
        } catch (NoSuchFileException e) {
            log.info("File not found for deletion", e);
        }
    }

    public void getS3FileWithName(String name) {
        log.info("Starting transfer of the file {}", name);
        //Authorization is done in the machine scope and SDK takes care of get it
       

        try {
            S3Client s3 = S3Client.builder().region(Region.of(s3Region)).build();
            Path x = Paths.get(s3WorkDir
                    + File.separator + name);
            delete(x);

            s3.getObject(GetObjectRequest.builder().bucket(s3Bucket).key(name).build(),
                    ResponseTransformer.toFile(x));
            log.info("Transfer of file {} finished with success", name);
        } catch (NoSuchBucketException e) {
            log.error("Bucket: {} {}", s3Bucket , e.getMessage());
            if (e.getCause() != null) {
                log.error(e.getCause().getMessage());
            }
            throw new RuntimeException(e.getMessage());
        } catch (NoSuchKeyException e) {
            String msg = String.format("Expected file %s, not found on bucket %s", name, s3Bucket);
            log.error(msg);
            log.error(e.getMessage());
            if (e.getCause() != null) {
                log.error(e.getCause().getMessage());
            }
            throw new RuntimeException(msg);
        } catch (SdkClientException e) {
            log.error(e.getMessage());
            if (e.getCause() != null) {
                log.error(e.getCause().getMessage());
            }
            throw new RuntimeException(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
            if (e.getCause() != null) {
                log.error(e.getCause().getMessage());
            }
            throw new RuntimeException(e.getMessage());
        }


    }

    public int getS3FilesStartingWith(String prefix) {
        

        try {
            int count = 0;

            ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                    .bucket(s3Bucket)
                    .maxKeys(5000)
                    .build();

            boolean done = false;
            S3Client s3 = S3Client.builder().region(Region.of(s3Region)).build();
            while(!done) {
                ListObjectsV2Response listObjectsV2Response = s3.listObjectsV2(listObjectsV2Request);
                for(S3Object content : listObjectsV2Response.contents()) {
                    if(content.key().startsWith(prefix)) {
                        getS3FileWithName(content.key());
                        count++;
                    }
                }
                if(listObjectsV2Response.nextContinuationToken() == null) {
                    done = true;
                }
                listObjectsV2Request = listObjectsV2Request.toBuilder()
                        .continuationToken(listObjectsV2Response.nextContinuationToken())
                        .build();
            }
            return count;
        } catch (NoSuchBucketException e) {
            log.error("Bucket: " + s3Bucket + e.getMessage());
            if (e.getCause() != null) {
                log.error(e.getCause().getMessage());
            }
            throw new RuntimeException(e.getMessage());
        }  catch (SdkClientException e) {
            log.error(e.getMessage());
            if (e.getCause() != null) {
                log.error(e.getCause().getMessage());
            }
            throw new RuntimeException(e.getMessage());
        }
    }

}
