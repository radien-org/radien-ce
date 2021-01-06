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

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



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
        io.radien.aws.utils.s3.S3FileUtil.deleteIfExists(x);
    }

    public void getS3FileWithName(String name) throws IOException {
        log.info("Starting transfer of the file {}", name);
        //Authorization is done in the machine scope and SDK takes care of get it
        io.radien.aws.utils.s3.S3FileUtil.getS3FileWithName(s3Region,s3Bucket,s3WorkDir,name);

    }

    public int getS3FilesStartingWith(String prefix) throws IOException {
        return io.radien.aws.utils.s3.S3FileUtil.getS3FilesStartingWith(s3Region,s3Bucket,s3WorkDir,prefix);
    }

}
