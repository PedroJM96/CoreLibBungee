package com.pedrojm96.core.bungee.libraryloader;

import java.net.MalformedURLException;
import java.net.URL;

public class MavenArtifact {
	  public String groupId;
	  
	  public String artifactId;
	  
	  public String version;
	  
	  public String repo;
	  
	  public MavenArtifact(String groupId, String artifactId, String version, String repo) {
	    this.groupId = groupId;
	    this.artifactId = artifactId;
	    this.version = version;
	    this.repo = repo;
	  }
	  
	  public String getGroupId() {
	    return this.groupId;
	  }
	  
	  public void setGroupId(String groupId) {
	    this.groupId = groupId;
	  }
	  
	  public String getArtifactId() {
	    return this.artifactId;
	  }
	  
	  public void setArtifactId(String artifactId) {
	    this.artifactId = artifactId;
	  }
	  
	  public String getVersion() {
	    return this.version;
	  }
	  
	  public void setVersion(String version) {
	    this.version = version;
	  }
	  
	  public URL getUrl() throws MalformedURLException {
	    String repo = this.repo;
	    if (!repo.endsWith("/"))
	      repo = String.valueOf(repo) + "/"; 
	    repo = String.valueOf(repo) + "%s/%s/%s/%s-%s.jar";
	    String url = String.format(repo, new Object[] { this.groupId.replace(".", "/"), this.artifactId, this.version, this.artifactId, this.version });
	    return new URL(url);
	  }
	  
	  public String getPath() {
		    String path = this.groupId.replace('.', '/') + '/' + this.artifactId + '/' + this.version + '/' + this.artifactId + '-' + this.version+".jar"; 
		    return path;
	  }
	  
	  public void setRepo(String repo) {
	    this.repo = repo;
	  }
	  
	  public String getRepo() {
	    return this.repo;
	  }
	}
