package com.pedrojm96.core.bungee.libraryloader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;

public class CoreURLClassLoader implements CoreURLClassLoaderHelper{
	private final CoreClassLoader classLoader;

    public CoreURLClassLoader(ClassLoader classLoader) {
        if (!(classLoader instanceof CoreClassLoader)) {
            throw new IllegalArgumentException("Loader is not a CoreClassLoader: " + classLoader.getClass().getName());
        }
        this.classLoader = (CoreClassLoader) classLoader;
    }

    @Override
    public void addJarToClasspath(Path file) {
        try {
            this.classLoader.addJarToClasspath(file.toUri().toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        this.classLoader.deleteJarResource();
        try {
            this.classLoader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
