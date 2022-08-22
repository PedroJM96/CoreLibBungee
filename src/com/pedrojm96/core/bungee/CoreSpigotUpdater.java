package com.pedrojm96.core.bungee;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import net.md_5.bungee.api.plugin.Plugin;


/**
 * Permite comprobar si ay actualizaciones para el plugin en el servidor de minecraft implementando la api de bukkt/spigot.
 * 
 * @author PedroJM96
 * @version 1.0 22-09-2018
 *
 */
public class CoreSpigotUpdater {
	private int project = 0;
    private URL checkURL;
    private String newVersion = "";
    private Plugin plugin;
 
    public CoreSpigotUpdater(Plugin plugin, int projectID) {
        this.plugin = plugin;
        this.newVersion = plugin.getDescription().getVersion();
        this.project = projectID;
        try {
            this.checkURL = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + projectID);
        } catch (MalformedURLException e) {
        }
    }
    
    public CoreSpigotUpdater(Plugin plugin, int projectID,CoreLog log) {
        this.plugin = plugin;
        this.newVersion = plugin.getDescription().getVersion();
        this.project = projectID;
        try {
            this.checkURL = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + projectID);
        } catch (MalformedURLException e) {
        }
    }
    
    
    
 
    public int getProjectID() {
        return project;
    }
 
    public Plugin getPlugin() {
        return plugin;
    }
    
   
 
    public String getLatestVersion() {
        return newVersion;
    }
 
    public String getResourceURL() {
        return "https://www.spigotmc.org/resources/" + project;
    }
 
    public boolean checkForUpdates() throws Exception {
        URLConnection con = checkURL.openConnection();
        this.newVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
        return !plugin.getDescription().getVersion().equals(newVersion);
    }
}
