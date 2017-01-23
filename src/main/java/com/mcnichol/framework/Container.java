package com.mcnichol.framework;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Container {
    public Map<Class, Registration> registrations;
    public Map<Class, Converter> converters = new HashMap<>();

    interface Converter<T> {
        T convert(String valueAsString);
    }

    public Container(String configurationPath) throws IoCException {
        File file = new File(configurationPath);
        validateFileExists(file);

        Loader loader = new Loader();

        registrations = loader.loadConfiguration(configurationPath);

        registerConverters();
    }

    private void validateFileExists(File file) throws IoCException {
        if (!file.exists()) {
            throw new IoCException(new FileNotFoundException());
        }
    }

    public <T> T resolve(Class<T> type) throws IoCException {

        Registration registration = registrations.get(type);

        List<com.mcnichol.framework.Constructor> constructorParams = registration.getConstructorParams();
        T instance = null;
        try {
            Class cls = Class.forName(registration.getMapTo());
            Constructor longestConstructor = getLongestConstructor(cls);

            Parameter[] parameters = longestConstructor.getParameters();

            List<Object> parameterInstances = populateParameterInstances(constructorParams, parameters);

            instance = createInstance(longestConstructor, parameterInstances);

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
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
        converters.put(boolean.class, Integer::parseInt);
        converters.put(byte.class, Integer::parseInt);
        converters.put(int.class, Integer::parseInt);
        converters.put(short.class, Integer::parseInt);
        converters.put(long.class, Integer::parseInt);
        converters.put(float.class, Integer::parseInt);
        converters.put(double.class, Integer::parseInt);
        converters.put(String.class, s -> s);
        converters.put(Character.class, c -> c);
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
        parameterInstances.add(c.convert(value.toString()));
    }

    private void getConfiguredParameters(List<Object> parameterInstances, Class parameterClass) throws IoCException {
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
            if (parameterClass.isPrimitive() || argumentClass.isPrimitive()) {
                if (primitivesMatch(argumentClass, parameterClass)) {
                    parameters[i] = parameterInstances.get(i);
                }
            }

            if (parameterClass.isAssignableFrom(argumentClass)) {
                parameters[i] = parameterInstances.get(i);
            }
        }

        instance = (T) longestConstructor.newInstance(parameters);
        return instance;
    }

    private boolean primitivesMatch(Class argumentClass, Class parameterClass) {
        if ((argumentClass == int.class || argumentClass == Integer.class) && (parameterClass == int.class || parameterClass == Integer.class)) {
            return true;
        }
        if ((argumentClass == byte.class || argumentClass == Byte.class) && (parameterClass == byte.class || parameterClass == Byte.class)) {
            return true;
        }
        if ((argumentClass == short.class || argumentClass == Short.class) && (parameterClass == short.class || parameterClass == Short.class)) {
            return true;
        }
        if ((argumentClass == long.class || argumentClass == Long.class) && (parameterClass == long.class || parameterClass == Long.class)) {
            return true;
        }
        if ((argumentClass == char.class || argumentClass == Character.class) && (parameterClass == char.class || parameterClass == Character.class)) {
            return true;
        }
        if ((argumentClass == double.class || argumentClass == Double.class) && (parameterClass == double.class || parameterClass == Double.class)) {
            return true;
        }
        if ((argumentClass == float.class || argumentClass == Float.class) && (parameterClass == float.class || parameterClass == Float.class)) {
            return true;
        }
        if ((argumentClass == boolean.class || argumentClass == Boolean.class) && (parameterClass == boolean.class || parameterClass == Boolean.class)) {
            return true;
        }
        if ((argumentClass == int.class || argumentClass == Integer.class) && (parameterClass == int.class || parameterClass == Integer.class)) {
            return true;
        }
        return false;

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
