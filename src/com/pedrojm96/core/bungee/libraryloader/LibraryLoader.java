package com.pedrojm96.core.bungee.libraryloader;

import static java.util.Objects.requireNonNull;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.pedrojm96.core.bungee.CoreLog;
import com.pedrojm96.core.bungee.CorePlugin;




/**
 * Para cargar librerias en el servidor de minecraft implementando la api de bukkt/spigot.
 * 
 * @author PedroJM96
 * @version 1.2 26-08-2022
 *
 */
public class LibraryLoader {
	  
	/**
     * User agent string to use when downloading libraries
     */
      public static final String HTTP_USER_AGENT = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2";  
	
	  private CoreLog log;
	  
	  private Path saveDirectory;
	  
	  private CoreURLClassLoaderHelper classLoader;
	  
	  
	  public LibraryLoader(CoreURLClassLoaderHelper classLoader, CoreLog log,CorePlugin plugin) {
		  this(classLoader,log, plugin.getInstance().getDataFolder().toPath());
	  }
	  
	  public LibraryLoader(CoreURLClassLoaderHelper classLoader, CoreLog log, Path dataDirectory) {
	    this.log = log;
	    this.classLoader = classLoader;
	    saveDirectory = requireNonNull(dataDirectory, "dataDirectory").toAbsolutePath().resolve("libs");
	  }
	  
	  public void loadLib(String groupId, String artifactId, String version) throws IOException {
	    loadLib(groupId, artifactId, version, "https://repo1.maven.org/maven2");
	  }
	  
	  public void loadLib(String groupId, String artifactId, String version, String url) throws IOException {
	    loadLib(new MavenArtifact(groupId, artifactId, version, url));
	  }
	  
	  public void loadLib(MavenArtifact maven) throws IOException {
		  
		  String name = maven.getGroupId() + "-" + maven.getArtifactId() + "-" + maven.getVersion();
		  Path file = downloadLibrary(maven,name);
		  classLoader.addJarToClasspath(file);
		  this.log.info(name+" library loaded successfully.");
	  } 
	  
	  
	  
	  /**
	     * Downloads a library jar to the save directory if it doesn't already
	     * exist and returns the local file path.
	     * <p>
	     * If the library has a checksum, it will be compared against the
	     * downloaded jar's checksum to verify the integrity of the download. If
	     * the checksums don't match, a warning is generated and the next download
	     * URL is attempted.
	     * <p>
	     * Checksum comparison is ignored if the library doesn't have a checksum
	     * or if the library jar already exists in the save directory.
	     * <p>
	     * Most of the time it is advised to use {@link #loadLibrary(Library)}
	     * instead of this method because this one is only concerned with
	     * downloading the jar and returning the local path. It's usually more
	     * desirable to download the jar and add it to the plugin's classpath in
	     * one operation.
	     *
	     * @param library the library to download
	     * @return local file path to library
	     * @see #loadLibrary(Library)
	     */
	    public Path downloadLibrary(MavenArtifact library,String name) {
	        Path file = saveDirectory.resolve(requireNonNull(library, "library").getPath());

	        if (Files.exists(file)) {
	        	log.info("Loading library " + name);
	        	return file;
	        }
	        
	        Path out = file.resolveSibling(file.getFileName() + ".tmp");
	        out.toFile().deleteOnExit();

	        try {
	            Files.createDirectories(file.getParent());

                byte[] bytes = downloadLibrary(library.getUrl(),name);
                if (bytes == null) {
                	throw new RuntimeException("Failed to download library '" + name + "'");
                }
                Files.write(out, bytes);
                Files.move(out, file);
                return file;
	        } catch (IOException e) {
	            throw new UncheckedIOException(e);
	        } finally {
	            try {
	                Files.deleteIfExists(out);
	            } catch (IOException ignored) {
	            }
	        }
	    }
	  
	  
	  
	  
	  /**
	     * Downloads a library jar and returns the contents as a byte array.
	     *
	     * @param url the URL to the library jar
	     * @return downloaded jar as byte array or null if nothing was downloaded
	     */
	    private byte[] downloadLibrary(URL url,String name) {
	        try {
	            URLConnection connection = url.openConnection();
	            connection.setConnectTimeout(5000);
	            connection.setReadTimeout(5000);
	            connection.setRequestProperty("User-Agent", HTTP_USER_AGENT);
	            log.info("Downloading librarie " + name);
	            try (InputStream in = connection.getInputStream()) {
	                int len;
	                byte[] buf = new byte[8192];
	                ByteArrayOutputStream out = new ByteArrayOutputStream();
	                try {
	                    while ((len = in.read(buf)) != -1) {
	                        out.write(buf, 0, len);
	                    }
	                } catch (SocketTimeoutException e) {
	                    log.error("Download timed out: " + connection.getURL());
	                    return null;
	                }
	                log.info("Librarie '" + name + "' successfully downloaded.");
	                return out.toByteArray();
	            }
	        } catch (MalformedURLException e) {
	            throw new IllegalArgumentException(e);
	        } catch (IOException e) {
	            if (e instanceof FileNotFoundException) {
	                log.error("File not found: " + url);
	            } else if (e instanceof SocketTimeoutException) {
	                log.error("Connect timed out: " + url);
	            } else if (e instanceof UnknownHostException) {
	                log.error("Unknown host: " + url);
	            } else {
	                log.error("Unexpected IOException", e);
	            }

	            return null;
	        }
	    }
	  
	  
	  
	  
	}

