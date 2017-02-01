package com.mcnichol.framework;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Container {
    Map<Class, Registration> registrations;
    private Map<Class, Converter> converters = new HashMap<>();
    private Map<Class, Class> javaTypes = new HashMap<>();

    @FunctionalInterface
    interface Converter<T> {
        T convert(String valueAsString);
    }

    public Container(String staticResource) throws IoCException {
        ResourceFileLoader rfl = new ResourceFileLoader();
        File configPath = rfl.loadRelativeFile(staticResource);

        Loader loader = new Loader();

        registrations = loader.loadConfiguration(configPath);

        registerConverters();
        loadJavaTypes();
    }

    public <T> T resolve(Class<T> type) throws IoCException {
        Registration registration = registrations.get(type);
        List<com.mcnichol.framework.Constructor> constructorParams = registration.getConstructorParams();
        T instance;

        try {
            Class cls = Class.forName(registration.getMapTo());
            Constructor longestConstructor = getLongestConstructor(cls);
            Parameter[] parameters = longestConstructor.getParameters();
            List<Object> parameterInstances = populateParameterInstances(constructorParams, parameters);
            instance = createInstance(longestConstructor, parameterInstances);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IoCException(e);
        }
        return instance;
    }

    private List<Object> populateParameterInstances(List<com.mcnichol.framework.Constructor> constructorParams, Parameter[] parameters) throws IoCException {
        List<Object> parameterInstances = new ArrayList<>();

        for (Parameter parameter : parameters) {
            Class parameterClass = parameter.getType();
            if (parameterClass.isPrimitive() || parameterClass.isAssignableFrom(String.class)) {
                getNonReferenceParameters(constructorParams, parameterInstances, parameter, parameterClass);
            } else {
                getConfiguredParameters(parameterInstances, parameterClass);
            }
        }
        return parameterInstances;
    }

    private void registerConverters() {
        converters.put(boolean.class, Boolean::parseBoolean);
        converters.put(byte.class, Byte::parseByte);
        converters.put(short.class, Short::parseShort);
        converters.put(int.class, Integer::parseInt);
        converters.put(long.class, Long::parseLong);
        converters.put(float.class, Float::parseFloat);
        converters.put(double.class, Double::parseDouble);
        converters.put(String.class, s -> s);
        converters.put(Character.class, c -> c);
    }

    private void loadJavaTypes() {
        javaTypes.put(boolean.class, Boolean.class);
        javaTypes.put(byte.class, Byte.class);
        javaTypes.put(short.class, Short.class);
        javaTypes.put(char.class, Character.class);
        javaTypes.put(int.class, Integer.class);
        javaTypes.put(long.class, Long.class);
        javaTypes.put(double.class, Double.class);
        javaTypes.put(float.class, Float.class);
    }

    private void getNonReferenceParameters(List<com.mcnichol.framework.Constructor> constructorParams, List<Object> parameterInstances, Parameter parameter, Class parameterClass) {
        Object value = null;
        for (com.mcnichol.framework.Constructor ctor : constructorParams) {
            if (ctor.getName().equals(parameter.getName())) {
                value = ctor.getValue();
                break;
            }
        }

        Converter c = converters.get(parameterClass);
        parameterInstances.add(c.convert(String.valueOf(value)));
    }

    private void getConfiguredParameters(List<Object> parameterInstances, Class<?> parameterClass) throws IoCException {
        Object resolvedInstance = resolve(parameterClass);
        parameterInstances.add(resolvedInstance);
    }

    private <T> T createInstance(Constructor longestConstructor, List<Object> parameterInstances) throws InstantiationException, InvocationTargetException, IllegalAccessException {
        T instance;

        Parameter[] parameterTypes = longestConstructor.getParameters();
        Object[] parameters = new Object[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            Class argumentClass = parameterInstances.get(i).getClass();
            Class parameterClass = parameterTypes[i].getType();
            if (parameterClass.isPrimitive() || argumentClass.isPrimitive() && primitivesMatch(argumentClass, parameterClass)) {
                parameters[i] = parameterInstances.get(i);
            }

            if (parameterClass.isAssignableFrom(argumentClass)) {
                parameters[i] = parameterInstances.get(i);
            }
        }

        instance = (T) longestConstructor.newInstance(parameters);
        return instance;
    }

    private boolean primitivesMatch(Class argumentClass, Class parameterClass) {
        final boolean[] matches = {false};

        javaTypes.forEach((primitiveClass, objectClass) -> {
            if ((argumentClass == primitiveClass || argumentClass == objectClass) && (parameterClass == primitiveClass && parameterClass == objectClass)) {
                matches[0] = true;
            }
        });

        return matches[0];
    }

    private Constructor getLongestConstructor(Class cls) {
        Constructor[] constructors = cls.getConstructors();

        Constructor longestConstructor = constructors[0];
        for (Constructor constructor : constructors) {
            if (constructor.getParameterCount() > longestConstructor.getParameterCount()) {
                longestConstructor = constructor;
            }
        }
        return longestConstructor;
    }
}
