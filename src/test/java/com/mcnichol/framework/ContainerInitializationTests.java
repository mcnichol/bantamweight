package com.mcnichol.framework;

import org.junit.Test;

public class ContainerInitializationTests {
    @Test(expected = IoCException.class)
    public void shouldThrowAnExceptionWhenTheConfigurationIsInvalid() throws IoCException {
        Container container = new Container("invalidConfiguration.json");
    }


}
