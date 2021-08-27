package io.radien.aws.utils.services;

import io.radien.aws.utils.s3.S3FileUtil;
import io.radien.aws.utils.s3.exceptions.RemoteResourceNotAvailableException;
import junit.framework.TestCase;
import org.junit.Test;
import software.amazon.awssdk.core.exception.SdkClientException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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