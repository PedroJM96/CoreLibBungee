package com.pedrojm96.core.bungee;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;

import net.md_5.bungee.api.plugin.Plugin;


public class LibraryLoader {
	
	private static final Method ADD_URL_METHOD;
    static {
        try {
            ADD_URL_METHOD = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            ADD_URL_METHOD.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
	
	
	private Plugin plugin;
	
	private CoreLog log;
	
	public LibraryLoader(Plugin plugin, CoreLog log) {
		this.log = log;
		this.plugin = plugin;
	}
	
	public void loadLib(File jarsDir,String groupId, String artifactId, String version) throws IOException {
		loadLib(jarsDir,groupId,artifactId,version,"https://repo1.maven.org/maven2");
	}
	
	public void loadLib(File jarsDir,String groupId, String artifactId, String version,String url) throws IOException {
		loadLib(jarsDir,new MavenArtifact(groupId,artifactId,version,url));
	}
	
	
	public void loadLib(File jarsDir,MavenArtifact maven) throws IOException {
		if (jarsDir.exists() && !jarsDir.isDirectory()) {
			Files.delete(jarsDir.toPath()); 
		    if (!jarsDir.exists() && !jarsDir.mkdirs()) {
		    	 throw new IOException("Could not create parent directory structure."); 
		    }
		     
		}else if(!jarsDir.exists()){
			if (!jarsDir.mkdirs()) {
		    	 throw new IOException("Could not create parent directory structure."); 
			}
		}
		      
		log.info(String.format("Loading lib %s:%s:%s from %s", maven.getGroupId(), maven.getArtifactId(), maven.getVersion(), maven.getRepo()));
		String name = maven.getGroupId() + "-" + maven.getArtifactId() + "-" + maven.getVersion();
		File saveLocation = new File(jarsDir, name + ".jar");
		if (!saveLocation.exists()) {
			try {
				log.info("Downloading librarie '" + name + "'...");
				URL url = maven.getUrl();
				try (InputStream is = url.openStream()) {
					Files.copy(is, saveLocation.toPath());
				}

			} catch (Exception e) {
	                e.printStackTrace();
			}

			log.info("Librarie '" + name + "' successfully downloaded.");
		}
		if (!saveLocation.exists()) {
			throw new RuntimeException("Unable to download lib: " + name);
		}
		URLClassLoader classLoader = (URLClassLoader)this.plugin.getClass().getClassLoader();
		try {
	            ADD_URL_METHOD.invoke(classLoader, saveLocation.toURI().toURL());
		} catch (Exception e) {
	            throw new RuntimeException("Unable to load lib: " + saveLocation.toString(), e);
		}
		log.info("Loaded Librarie '" + name + "' successfully.");
	}
}
