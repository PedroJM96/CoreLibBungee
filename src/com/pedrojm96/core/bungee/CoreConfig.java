package com.pedrojm96.core.bungee;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

import com.google.common.io.CharStreams;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;


/**
 * Facilita la creacion de configuracion en el servidor de minecraft implementando la api de bukkt/spigot.
 * 
 * @author PedroJM96
 * @version 1.1 19-9-2022
 *
 */
public class CoreConfig {
	private Configuration config;
	private File file;
	private String configFileName;
	
	private String header;
	
	
	
	private CoreLog log;
	
	public CoreConfig(Plugin plugin,String configfile,CoreLog log,InputStream dafaultData,InputStream dafaultData2,boolean update){
		
		
		this.file = new File(plugin.getDataFolder(),configfile + ".yml");
	
		this.configFileName = configfile;
		this.log = log;
		
		if(!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdirs();
		}
		
		
		Configuration defaultConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new InputStreamReader(dafaultData, Charset.forName("UTF-8")));
		String inpu = getInputStreamToString(dafaultData2);
		
		
		
		String[] inpuArray = inpu.split("\n");
		String header = "";
		for(int i = 0 ; i<inpuArray.length ; i++) {
			if(inpuArray[i].startsWith("#")) {
				header = header +inpuArray[i];
			}else {
				break;
			}
		}
		this.header = header;
		
		
		if (this.exists()){
			this.load();
			if(update) {
				for(String nodo : defaultConfig.getKeys()) {
					
					this.add(nodo, defaultConfig.get(nodo));
				}
				
				this.silenSave();
			}
		}else{
			this.create(defaultConfig);	
		}
	}
	
	private String getInputStreamToString(InputStream dataIn) {
		String result ="";
		if(dataIn==null) {
			return result;
		}
		try {
			result = CharStreams.toString(new InputStreamReader(
				     dataIn, Charset.forName("UTF-8")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	public boolean exists(){
		if(file.exists()){
			return true;
		}else{
			return false;
		}
	}
	
	public void save(){
		Writer fileWriter = null;
		try{
			fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8")));
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.config, fileWriter);
            String filestring = readFile(this.file.getAbsolutePath(),Charset.forName("UTF-8"));
    		fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8")));
    		fileWriter.write(this.header+filestring);
            log.alert(this.configFileName+".yml  save.");
        }catch(IOException e){
        	log.fatalError("Error on save "+this.configFileName+".yml.",e);
        }finally{
            if (fileWriter != null) {
            	try {
					fileWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
	}
	public void silenSave(){
		Writer fileWriter = null;
        try{
        	 fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8")));
             ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.config, fileWriter);
             String filestring = readFile(this.file.getAbsolutePath(),Charset.forName("UTF-8"));
     		 fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8")));
     		 fileWriter.write(this.header+filestring);
             
        }
        catch(IOException e){
        	log.fatalError("Error on save "+this.configFileName+".yml.",e);
        }finally{
            if (fileWriter != null) {
            	try {
					fileWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
    }
	public void load(){
		log.info("Load "+this.configFileName+".yml");
		try {
			FileInputStream fileinputstream = new FileInputStream(this.file);
            config =  ConfigurationProvider.getProvider(YamlConfiguration.class).load(new InputStreamReader(fileinputstream, Charset.forName("UTF-8")));
			log.alert(this.configFileName+".yml loaded.");
		} catch (IOException  e) {
			log.fatalError("Error on loaded "+this.configFileName+".yml.",e);
		}
	}
	public CoreConfig loadFromString(String text){
		config =  ConfigurationProvider.getProvider(YamlConfiguration.class).load(text);
		log.alert("String to "+this.configFileName+" loaded.");
		return this;
	}
	public void create(){
		Writer fileWriter = null;
		
		log.alert("The "+this.configFileName+".yml file does not exist yet.");
		log.info("Creating and loading file "+this.configFileName+".yml.");
        try{
        	fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8")));
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.config, fileWriter);
            log.alert(this.configFileName+".yml  create.");
        }
        catch(IOException e){
        	log.fatalError("Error on create "+this.configFileName+".yml.",e);
            e.printStackTrace();
        }finally{
            if (fileWriter != null) {
            	try {
					fileWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
    }
	
	public void create(Configuration local){
		Writer fileWriter = null;
		
		log.alert("The "+this.configFileName+".yml file does not exist yet.");
		log.info("Creating and loading file "+this.configFileName+".yml.");
        try{
        	fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8")));
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(local, fileWriter);
            String filestring = readFile(this.file.getAbsolutePath(),Charset.forName("UTF-8"));
    		fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8")));
    		fileWriter.write(this.header+filestring);
            log.alert(this.configFileName+".yml  create.");
            this.config = local;
        }
        catch(IOException e){
        	log.fatalError("Error on create "+this.configFileName+".yml.",e);
            e.printStackTrace();
        }finally{
            if (fileWriter != null) {
            	try {
					fileWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
    }
	
	
	public String readFile(String path, Charset encoding) throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	
	
	@SuppressWarnings("unused")
	private void create(String data){
		String configTex = data;
		
		log.alert("The "+this.configFileName+".yml file does not exist yet.");
		log.info("Creating and loading file "+this.configFileName+".yml.");
        try{
            Writer fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8")));
            fileWriter.write(configTex);
            
            fileWriter.close();
            log.alert(this.configFileName+".yml  create.");
        }
        catch(IOException e){
        	log.fatalError("Error on create "+this.configFileName+".yml.",e); 
        }
    }
	public void add(String path,String value){
		if(!config.contains(path)) {
			config.set(path,value);
		}
	}
	public void add(String path,long value){
		if(!config.contains(path)) {
			config.set(path,value);
		}
	}
	public void add(String path,boolean value){
		if(!config.contains(path)) {
			config.set(path,value);
		}
	}
	public void add(String path,List<String> value){
		if(!config.contains(path)) {
			config.set(path,value);
		}
	}
	public void add(String path,int value){
		if(!config.contains(path)) {
			config.set(path,value);
		}
	}
	public void add(String path, double value) {
		if(!config.contains(path)) {
			config.set(path,value);
		}
	}
	public void add(String path, Object value) {
		if(!config.contains(path)) {
			config.set(path, value);
		}
	}
	public boolean getBoolean(String path){
		return config.getBoolean(path);
	}
	public String getString(String path){
		return config.getString(path);
	}
	public int getInt(String path){
		return config.getInt(path);
	}
	public long getLong(String path){
		return config.getLong(path);
	}
	public List<String> getStringList(String path){
		
		
		return config.getStringList(path);
	}
	public Configuration getConfigurationSection(String path){
		return config.getSection(path);
	}
	public Double getDouble(String path){
		return config.getDouble(path);
	}
	public Collection<String> getKeys(){
		return config.getKeys();
	}
	public void set(String path,String value){
		 config.set(path, value);
	}
	public void setNull(String path){
		 config.set(path, null);
	}
	public void set(String path,double value){
		 config.set(path, value);
	}
	public void set(String path, List<String> value) {
		config.set(path, value);
	}
	public void set(String path,int value){
		 config.set(path, value);
	}
	public void set(String path,boolean value){
		 config.set(path, value);
	}
	public boolean isSet(String path){
		return config.contains(path);
	}
	public boolean contains(String path){
		 return config.contains(path);
	}
}
