package com.pedrojm96.core.bungee.libraryloader;

import java.nio.file.Path;

public interface CoreURLClassLoaderHelper extends AutoCloseable {
	void addJarToClasspath(Path file);

    @Override
    default void close() {

    }
}
