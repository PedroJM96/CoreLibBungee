package com.pedrojm96.core.bungee;

import java.net.MalformedURLException;
import java.net.URL;

public class MavenArtifact {
	
	
	
	public String groupId;
	public MavenArtifact(String groupId, String artifactId, String version, String repo) {
		super();
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
		this.repo = repo;
	}

	public String artifactId;
	public String version;
	
	public String repo;

	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	/**
	 * @return the artifactId
	 */
	public String getArtifactId() {
		return artifactId;
	}

	/**
	 * @param artifactId the artifactId to set
	 */
	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the url
	 * @throws MalformedURLException 
	 */
	public URL getUrl() throws MalformedURLException {
		String repo = this.repo;
        if (!repo.endsWith("/")) {
            repo += "/";
        }
        repo += "%s/%s/%s/%s-%s.jar";

        String url = String.format(repo, this.groupId.replace(".", "/"), this.artifactId, this.version, this.artifactId, this.version);
        return new URL(url);
	}

	/**
	 * @param repoUrl the url to set
	 */
	public void setRepo(String repo) {
		this.repo = repo;
	}
	
	public String getRepo() {
		return this.repo;
		 
	}

}
