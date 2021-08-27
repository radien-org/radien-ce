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

import junit.framework.TestCase;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
/**
 * Class that aggregates UnitTest cases for
 * S3FileUtil
 *
 */
public class S3FileUtilTest extends TestCase {

    //TODO: setup requirements for integration testing
    @Test
    public void testDeleteIfExists() throws IOException {


        Path testDir = Files.createTempDirectory("testDir");
        Path newFile = Files.createFile(testDir.resolve("testFile.txt"));
        assertTrue(Files.exists(newFile));
        S3FileUtil.deleteIfExists(newFile);
        assertFalse(Files.exists(newFile));
        S3FileUtil.deleteIfExists(testDir);
        assertFalse(Files.exists(testDir));
    }

    @Test
    public void testDeleteIfExistsNotExisting() throws IOException {

        Path testDir = Files.createTempDirectory("testDir");
        Path newFile = testDir.resolve("testFile.txt");
        assertFalse(Files.exists(newFile));
        S3FileUtil.deleteIfExists(newFile);
        assertFalse(Files.exists(newFile));
        S3FileUtil.deleteIfExists(testDir);
        assertFalse(Files.exists(testDir));
    }

    @Test
    public void testGetS3FileWithName() throws IOException {
        try {
            Path workDir = Files.createTempDirectory("testDir");
            String workDirStr = workDir.toAbsolutePath().toString();
            //URLClassLoader classLoader = s3Service.getClassLoaderForBucketFiles(workDirStr);
            Path newFile = workDir.resolve("Language.properties");
            assertFalse(Files.exists(newFile));
            S3FileUtil.getS3FileWithName("eu-west-1", "radien-initializer", workDirStr, "Language.properties");
            assertTrue(Files.exists(newFile));
            S3FileUtil.deleteIfExists(newFile);
            assertFalse(Files.exists(newFile));
            S3FileUtil.deleteIfExists(workDir);
            assertFalse(Files.exists(workDir));
        } catch (RemoteResourceNotAvailableException e) {
            //if the machine has the access to aws configured it shouldn't happen
            System.out.println(e.getMessage());

        }
    }

    @Test
    public void testGetS3FilesStartingWith() throws IOException {
        try {
            Path workDir = Files.createTempDirectory("testDir");
            String workDirStr = workDir.toAbsolutePath().toString();
            System.out.println(workDirStr);
            Path newFile = workDir.resolve("Language.properties");
            assertFalse(Files.exists(newFile));
            S3FileUtil.getS3FilesStartingWith("eu-west-1","radien-initializer",workDirStr,"Language");
            assertTrue(Files.exists(newFile));
            for(String file :workDir.toFile().list()){
                System.out.println(file);
                S3FileUtil.deleteIfExists(Paths.get(workDirStr+ File.separator + file));
            }
            S3FileUtil.deleteIfExists(workDir);
            assertFalse(Files.exists(workDir));
        } catch (RemoteResourceNotAvailableException e) {
            //if the machine has the access to aws configured it shouldn't happen
            System.out.println(e.getMessage());
        }

    }
}