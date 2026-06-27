package rev.utils;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rev.annotation.controller.UrlMapping;

public class Utilitaire {

    public Utilitaire() {}

    public List<String> getClassLists(String packageName) throws Exception {
        List<String> classLists = new ArrayList<>();
        String pathPackage = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        URL resource = classLoader.getResource(pathPackage);
        File directory = new File(resource.getFile());

        if (directory.exists()) {
            File[] files = directory.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().endsWith(".class") && files[i].isFile()) {
                    String className = files[i].getName().substring(0, files[i].getName().length() - 6);
                    classLists.add(packageName + "." + className); // nom complet, sinon Class.forName plante
                }
            }
        } else {
            throw new Exception(" not found is the " + directory.getName());
        }
        return classLists;
    }

    public List<String> getAnnotedClassList(Class<?> annotationClass, String type, List<String> classList) throws Exception {
        List<String> annotedClassLists = new ArrayList<>();
        for (String className : classList) {
            Class<?> clazz = Class.forName(className);
            if (type.equalsIgnoreCase("classe")) {
                if (clazz.isAnnotationPresent((Class) annotationClass)) {
                    annotedClassLists.add(className); 
                }
            }
        }
        return annotedClassLists;
    }

    public Map<String, Mapping> getUrlMappings(List<String> annotedClassList) throws Exception {
        Map<String, Mapping> mappings = new HashMap<>();
        for (String className : annotedClassList) {
            Class<?> clazz = Class.forName(className);
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(UrlMapping.class)) {
                    String url = method.getAnnotation(UrlMapping.class).value();
                    mappings.put(url, new Mapping(clazz, method));
                }
            }
        }
        return mappings;
    }
}