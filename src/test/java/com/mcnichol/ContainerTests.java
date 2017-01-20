package com.mcnichol;

import com.mantiso.*;
import com.mcnichol.framework.Container;
import com.mcnichol.framework.IoCException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;

public class ContainerTests {

    Container container;

    @Before
    public void beforeEach() {
        String configurationPath = "testConfigurations/config.json";
        try {
            container = new Container(configurationPath);
        } catch (IoCException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldLoadRegistrations() {
        String configurationPath = "testConfigurations/simpleconfig.json";
        try {
            container = new Container(configurationPath);
        } catch (IoCException e) {
            e.printStackTrace();
        }
        assertThat(container.registrations.keySet().size(), equalTo(4));
    }

    @Test
    public void shouldCreateInstanceOfClassWithAConstructorWhichHasNoParameters() throws IoCException {
        assertThat(container.resolve(Logger.class), is(notNullValue()));
    }

    @Test
    public void shouldCreateInstanceOfClassWithAConstructorWhichHasOneRegisteredParameter() throws IoCException {
        assertThat(container.resolve(Drivable.class), is(notNullValue()));
    }

    @Test
    public void shouldCreateInstanceOfClassWithAConstructorWhichHasOneIntegerParameter() throws IoCException {
        assertThat(container.resolve(SingleCtorWithFixedArgItf.class), is(notNullValue()));
    }

    @Test
    public void shouldCreateInstanceOfClassWithAConstructorWhichHasOneIntegerParameterAndOneReferenceParameter() throws IoCException {
        SingleCtorWithSingleFixedArgAndSingleReferenceArgItf itf = container.resolve(SingleCtorWithSingleFixedArgAndSingleReferenceArgItf.class);
        assertThat(itf, is(notNullValue()));
        assertThat(itf.getValue(), is("23"));
        Assert.assertEquals(FileLogger.class, itf.getLogger().getClass());
    }
}

