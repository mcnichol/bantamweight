package com.mcnichol.framework;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

public class ResourceFileLoader {
    public ResourceFileLoader() {

    }

    public void validateFile(File file) throws IoCException {
        if (!file.exists()) {
            throw new IoCException(new FileNotFoundException());
        }
    }

    public File loadRelativeFile(String relativeFilePath) throws IoCException {
        URL resource = getClass().getClassLoader().getResource(relativeFilePath);

        if (resource != null) {
            File file = new File(resource.getFile());
            validateFile(file);
            return file;
        } else {
            throw new IoCException(new FileNotFoundException());
        }
    }

}
