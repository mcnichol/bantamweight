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
    public Map<Class, Registration> registrations;
    public Map<Class, Converter> converters = new HashMap<>();

    interface Converter<T> {
        T convert(String valueAsString);
    }

    public Container(String staticResource) throws IoCException {
        ResourceFileLoader rfl = new ResourceFileLoader();
        File configPath = rfl.loadRelativeFile(staticResource);

        Loader loader = new Loader();

        registrations = loader.loadConfiguration(configPath);

        registerConverters();
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
        if (isByte(argumentClass) && (isByte(parameterClass))) {
            return true;
        }

        if (isShort(argumentClass) && (isShort(parameterClass))) {
            return true;
        }

        if (isCharacter(argumentClass) && (isCharacter(parameterClass))) {
            return true;
        }

        if (isInteger(argumentClass) && (isInteger(parameterClass))) {
            return true;
        }

        if (isLong(argumentClass) && (isLong(parameterClass))) {
            return true;
        }

        if (isDouble(argumentClass) && (isDouble(parameterClass))) {
            return true;
        }

        if (isFloat(argumentClass) && (isFloat(parameterClass))) {
            return true;
        }

        if (isBoolean(argumentClass) && (isBoolean(parameterClass))) {
            return true;
        }

        return false;

    }

    private boolean isDouble(Class cls) {
        return cls == double.class || cls == Double.class;
    }

    private boolean isFloat(Class cls) {
        return cls == float.class || cls == Float.class;
    }

    private boolean isBoolean(Class cls) {
        return cls == boolean.class || cls == Boolean.class;
    }

    private boolean isLong(Class cls) {
        return cls == long.class || cls == Long.class;
    }

    private boolean isCharacter(Class cls) {
        return cls == char.class || cls == Character.class;
    }

    private boolean isShort(Class cls) {
        return cls == short.class || cls == Short.class;
    }

    private boolean isByte(Class cls) {
        return cls == byte.class || cls == Byte.class;
    }

    private boolean isInteger(Class cls) {
        return cls == int.class || cls == Integer.class;
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
