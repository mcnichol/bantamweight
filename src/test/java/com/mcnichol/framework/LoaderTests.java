package com.mcnichol.framework;

import com.mantiso.Drivable;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class LoaderTests {
    Map<Class, Registration> registrations;


    @Before
    public void beforeEach() {
        File configurationPath = new File("static/config.json");
        try {
            Loader loader = new Loader();
            registrations = loader.loadConfiguration(configurationPath);

        } catch (IoCException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldHaveRegistrationForSpecifiedClass() {
        assertThat(registrations.get(Drivable.class), is(notNullValue()));
    }

    @Test
    public void shouldHaveAConstructorForSpecifiedClass() {
        Registration registration = registrations.get(Drivable.class);
        assertThat(registration.getConstructorParams().get(0), is(notNullValue()));
    }

    @Test
    public void shouldHaveAConstructorValueForSpecifiedClass() {
        Registration registration = registrations.get(Drivable.class);
        Constructor ctor = registration.getConstructorParams().get(0);
        assertThat(ctor.getName(), is("age"));
        assertThat(ctor.getValue(), is(23));
    }

    @Test(expected = ClassNotFoundException.class)
    public void shouldThrowExceptionWhenLoadingClassThatDoesNotExist() throws Throwable {
        File configurationPath = new File("static/invalidclassnameconfig.json");
        try {
            Loader loader = new Loader();
            registrations = loader.loadConfiguration(configurationPath);

        } catch (IoCException e) {
            throw e.getCause();
        }
    }

    @Test(expected = IoCException.class)
    public void shouldThrowAnExceptionWhenTheConfigurationIsInvalid() throws IoCException {
        Loader loader = new Loader();
        registrations = loader.loadConfiguration(new File("invalidConfiguration.json"));
    }

}
