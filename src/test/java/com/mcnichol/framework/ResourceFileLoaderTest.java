package com.mcnichol.framework;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class ResourceFileLoaderTest {
    @Test(expected = IoCException.class)
    public void validateFile_throwsExceptionIfFileNotExist() throws Exception {
        ResourceFileLoader subject = new ResourceFileLoader();

        subject.validateFile(new File("non-existant-file.file"));
    }
}