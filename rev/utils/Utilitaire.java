package rev.utils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
}