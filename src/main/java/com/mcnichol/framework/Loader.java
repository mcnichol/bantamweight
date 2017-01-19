package com.mcnichol.framework;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Loader {
    public Map<Class, Registration> loadConfiguration(String filename) throws IoCException {

        Map<Class, Registration> registrations = new HashMap<Class, Registration>();

        try {
            Path path = FileSystems.getDefault().getPath(filename);
            String contents = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);

            ObjectMapper mapper = new ObjectMapper();
            List<Registration> parsedReg = mapper.readValue(contents, mapper.getTypeFactory().constructCollectionType(List.class, Registration.class));

            for (Registration r : parsedReg) {
                Class<?> cls = Class.forName(r.getType());
                registrations.put(cls, r);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new IoCException(e);
        }

        return registrations;
    }
}
