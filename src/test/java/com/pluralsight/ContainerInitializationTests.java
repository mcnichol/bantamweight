package com.pluralsight;

import com.mcnichol.framework.Container;
import com.mcnichol.framework.IoCException;
import org.junit.Test;

public class ContainerInitializationTests {
    @Test(expected = IoCException.class)
    public void shouldThrowAnExceptionWhenTheConfigurationIsInvalid() throws IoCException {
        Container container = new Container("invalidConfiguration.json");
    }


}
