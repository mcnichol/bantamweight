package com.mcnichol.framework;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Loader {
    public Map<Class, Registration> loadConfiguration(File file) throws IoCException {

        Map<Class, Registration> registrations = new HashMap<>();

        try {

            String configContents = readConfigFile(file.toPath());

            List<Registration> parsedReg = parseRegistrationFromConfig(configContents);

            populateRegistrations(registrations, parsedReg);

        } catch (IOException | ClassNotFoundException | NullPointerException e) {
            throw new IoCException(e);
        }

        return registrations;
    }

    private void populateRegistrations(Map<Class, Registration> registrations, List<Registration> parsedReg) throws ClassNotFoundException {
        for (Registration r : parsedReg) {
            Class<?> cls = Class.forName(r.getType());
            registrations.put(cls, r);
        }
    }

    private List<Registration> parseRegistrationFromConfig(String contents) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(contents, mapper.getTypeFactory().constructCollectionType(List.class, Registration.class));
    }

    private String readConfigFile(Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }
}
