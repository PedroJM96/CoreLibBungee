package com.pedrojm96.core.bungee.libraryloader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import com.pedrojm96.core.bungee.CoreLog;

/**
 * Classloader that can load a jar from within another jar file.
 *
 * <p>The "loader" jar contains the loading code & public API classes,
 * and is class-loaded by the platform.</p>
 *
 * <p>The inner "plugin" jar contains the plugin itself, and is class-loaded
 * by the loading code & this classloader.</p>
 */



public class CoreClassLoader extends URLClassLoader {
    static {
        ClassLoader.registerAsParallelCapable();
    }

    private static CoreLog log;
    
    /**
     * Creates a new jar-in-jar class loader.
     *
     * @param loaderClassLoader the loader plugin's classloader (setup and created by the platform)
     * @param jarResourcePath the path to the jar-in-jar resource within the loader jar
     * @throws LoadingException if something unexpectedly bad happens
     */
    public CoreClassLoader(ClassLoader loaderClassLoader, String jarResourcePath, CoreLog paramlog) {
        super(new URL[]{extractJar(loaderClassLoader, jarResourcePath,paramlog)}, loaderClassLoader);
        
    }

    public void addJarToClasspath(URL url) {
        addURL(url);
    }

    public void deleteJarResource() {
        URL[] urls = getURLs();
        if (urls.length == 0) {
            return;
        }

        try {
            Path path = Paths.get(urls[0].toURI());
            Files.deleteIfExists(path);
        } catch (Exception e) {
            // ignore
        }
    }

    /**
     * Creates a new plugin instance.
     *
     * @param bootstrapClass the name of the bootstrap plugin class
     * @param loaderPluginType the type of the loader plugin, the only parameter of the bootstrap
     *                         plugin constructor
     * @param loaderPlugin the loader plugin instance
     * @param <T> the type of the loader plugin
     * @return the instantiated bootstrap plugin
     */
    public <T> CoreLoader instantiatePlugin(String bootstrapClass, Class<T> loaderPluginType, T loaderPlugin) {
        Class<? extends CoreLoader> plugin;
        try {
            plugin = loadClass(bootstrapClass).asSubclass(CoreLoader.class);
        } catch (ReflectiveOperationException e) {
        	log.error("Unable to load the plugin bootstrap class", e);
        	return null;
        }

        Constructor<? extends CoreLoader> constructor;
        try {
            constructor = plugin.getConstructor(loaderPluginType);
        } catch (ReflectiveOperationException e) {
        	log.error("Unable to get the plugin bootstrap constructor", e);
        	return null;
        }

        try {
            return constructor.newInstance(loaderPlugin);
        } catch (ReflectiveOperationException e) {
        	log.error("Unable to create bootstrap plugin instance", e);
        	return null;
        }
    }

    /**
     * Extracts the "jar-in-jar" from the loader plugin into a temporary file,
     * then returns a URL that can be used by the {@link JarInJarClassLoader}.
     *
     * @param loaderClassLoader the classloader for the "host" loader plugin
     * @param jarResourcePath the inner jar resource path
     * @return a URL to the extracted file
     */
    private static URL extractJar(ClassLoader loaderClassLoader, String jarResourcePath,CoreLog paramlog) {
    	log = paramlog;
        // get the jar-in-jar resource
        URL jarInJar = loaderClassLoader.getResource(jarResourcePath);
        if (jarInJar == null) {
        	log.error("Could not locate the plugin bootstrap files");
        	return null;
        }

        // create a temporary file
        // on posix systems by default this is only read/writable by the process owner
        Path path;
        try {
            path = Files.createTempFile("luckperms-jarinjar", ".jar.tmp");
        } catch (IOException e) {
        	log.error("Unable to create a temporary file to plugin bootstrap", e);
        	return null;
        }

        // mark that the file should be deleted on exit
        path.toFile().deleteOnExit();

        // copy the jar-in-jar to the temporary file path
        try (InputStream in = jarInJar.openStream()) {
            Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
        	log.error("Unable to copy the plugin bootstrap files to temporary path", e);
        	return null;
        }

        try {
            return path.toUri().toURL();
        } catch (MalformedURLException e) {
        	log.error("Unable to get URL of the plugin bootstrap from path", e);
        	return null;
        }
    }

}
