package rev.utils;

import java.lang.reflect.Method;

public class Mapping {

    private Class<?> controllerClass;
    private Method method;

    public Mapping(Class<?> controllerClass, Method method) {
        this.controllerClass = controllerClass;
        this.method = method;
    }

    public Class<?> getControllerClass() {
        return controllerClass;
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return controllerClass.getName() + "." + method.getName() + "()";
    }
}
