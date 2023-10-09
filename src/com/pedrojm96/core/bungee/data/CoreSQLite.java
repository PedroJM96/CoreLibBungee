package com.pedrojm96.core.bungee.data;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pedrojm96.core.bungee.CoreLog;
import com.pedrojm96.core.bungee.CorePlugin;

import net.md_5.bungee.api.plugin.Plugin;








/**
 * Facilita la creacion de base de datos sqlite.
 * 
 * @author PedroJM96
 * @version 1.4 21-03-2020
 *
 */
public class CoreSQLite implements CoreSQL{

	private CoreLog log;
	private String tabla;

	private Plugin plugin;
	private Connection connection;
	
	public Map<String, String> columns = new HashMap<String, String>();
	
	public CoreSQLite(CorePlugin cplugin,String tabla) {
		this.log = cplugin.getLog();
		this.log.info("Data set to SQLite");
		this.tabla = tabla;
		this.plugin = cplugin.getInstance();
	
		connection = getConnection();
		
		
		
	}
	
	
	protected Connection getConnection() {
		File dataFolder = new File(this.plugin.getDataFolder(),"sqlite.db");
	    
	    if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
            	 this.log.error("File write error: data.db",e); 
            }
        }
	    
	    try {
            if(connection!=null&&!connection.isClosed()){
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
            
        } catch (SQLException e) {
        	 this.log.error("SQLite exception on initialize.",e); 
        	
        } catch (ClassNotFoundException e) {
        	 this.log.error("You need the SQLite JBDC library. Google it. Put it in /lib folder.",e); 
        	
        }
	    return null;
	}
	
	
	
	protected void cleanup(Statement statement,ResultSet result){
		if (statement != null) {
			try
			{
		        statement.close();
			}
			catch (SQLException e)
			{
				this.log.error("SQLException on cleanup [statement].");
			}
		}
		
		if (result != null) {
			try
			{
		        result.close();
			}
			catch (SQLException e)
			{
		    	  this.log.error("SQLException on cleanup [result].");  
			}
		}
		
	}
	
	
	@Override
	public boolean columnExists(String columnname) {
		
		return this.columns.containsKey(columnname.toLowerCase());
	}
	
	@Override
	public String getColumnType(String columnname) {
		
		return this.columns.get(columnname.toLowerCase());
	}
	
	@Override
	public void addColumn(String columnname,String columntype) {
		
		Connection connection = null;
		Statement statement = null;
		ResultSet result = null;
		
		try {
			connection = getConnection();
			String query = "ALTER TABLE "+this.tabla+" ADD "+columnname+" "+columntype;
			statement = connection.createStatement();
			result = statement.executeQuery(query);
			this.log.info("Added column: "+columnname+" "+columntype);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			
			this.log.alert("The table "+this.tabla+" does not exist");
		}finally{
			cleanup(statement,result);
		}
	}
	
	
	@Override
	public boolean checkStorage() {
		boolean existe;
		Connection connection = null;
		Statement statement = null;
		ResultSet result = null;
		try {
			String query = "SELECT * FROM "+this.tabla;
			connection = getConnection();
			statement = connection.createStatement();
			result = statement.executeQuery(query);
			existe = true;
			this.log.info("Loaded database");
			
			String listcolumns = "";
			
			ResultSetMetaData metaData = result.getMetaData();
			int rowCount = metaData.getColumnCount();
			for (int i = 1; i <= rowCount; i++) {
				this.columns.put(metaData.getColumnName(i).toLowerCase(), metaData.getColumnTypeName(i).toLowerCase());
				listcolumns = listcolumns +"["+metaData.getColumnName(i).toLowerCase()+","+metaData.getColumnTypeName(i).toLowerCase()+"] ";
			}
			
			this.log.info(listcolumns);
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			existe = false;
			this.log.alert("The table "+this.tabla+" does not exist");
		}finally{
			cleanup(statement,result);
		}
		return existe;
	}


	@Override
	public void build(CoreField ...paramsField) {
		// TODO Auto-generated method stub
		Connection connection = null;
		Statement statement = null;
		this.log.info("Creating table " + this.tabla);
		String query = "";
		for (int i = 0; i<paramsField.length;i++) {
			if(i==(paramsField.length - 1)) {
				query = query + paramsField[i].toString()+");";
			}else {
				query = query + paramsField[i].toString()+", ";
			}
		}
		
		try {
			connection = getConnection();
			statement = connection.createStatement();
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS "+this.tabla+" (id INTEGER PRIMARY KEY AUTOINCREMENT, " + query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			cleanup(statement,null);
		}
	}
	
	@Override
	public boolean checkData(CoreWHERE paranWhere,String paranString) {
		boolean existe = false;
		Connection connection = null;
		Statement statement = null;
		ResultSet result = null;
		try {
			String query = "SELECT '"+paranString+"' FROM "+this.tabla+" WHERE "+paranWhere.get()+";";
			connection = getConnection();
			statement = connection.createStatement();
			result = statement.executeQuery(query);
			existe = result.next();
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			existe = true;
		}finally{
			cleanup(statement,result);
		}
		return existe;
		
	}
	
	@Override
	public void insert(String... args) {
		// TODO Auto-generated method stub
		String data = "";
		String value = "";
		for(int i =0; i<args.length;i++) {
			if(!args[i].trim().contains(":")) {
				continue;
			}
			String[] local = args[i].trim().split(":");
			if(local.length<2) {
				continue;
			}
			String locaData = local[0].trim();
			String locaValue = local[1].trim();
			
			if(i==(args.length - 1)) {
				data = data + locaData;
				value = value + "'"+locaValue+"'";
			}else {
				data = data + locaData +",";
				value = value + "'"+locaValue +"',";
			}		
		}
		Connection connection = null;
		Statement statement = null;
		
	    ResultSet result = null;
	    String query = "INSERT INTO "+this.tabla+" ("+data+") VALUES ("+value+");";
		try {
			connection = getConnection();
			statement = connection.createStatement();
			statement.executeUpdate(query);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			this.log.error("Could not create getter statement on insert: "+query);
			e.printStackTrace();
		}
		finally{
			cleanup(statement,result);
		}
		
	}
	
	
	@Override
	public String get(CoreWHERE paranWhere,String paranString) {
		
		// TODO Auto-generated method stub
		String value = "";
		String query = "SELECT "+paranString+" FROM "+this.tabla+" WHERE "+paranWhere.get()+";";
		Connection connection = null;
		Statement statement = null;
		ResultSet result = null;
		try {
			connection = getConnection();
			statement = connection.createStatement();
			result = statement.executeQuery(query);
			if ((result != null) && (result.next())){
				value = result.getString(paranString);
			}
		}catch (SQLException e) {
			this.log.error("Could not create getter statement on get: "+query);
			e.printStackTrace();
		}finally{
			cleanup(statement,result);
		}
		return value;
	}
	
	
	@Override
	public void build(String tabla, CoreField ...paramsField) {
		// TODO Auto-generated method stub
		Connection connection = null;
		Statement statement = null;
		
		this.log.info("Creating table " + String.valueOf(tabla));
		String query = "";
		for (int i = 0; i<paramsField.length;i++) {
			if(i==(paramsField.length - 1)) {
				query = query + paramsField[i].toString()+");";
			}else {
				query = query + paramsField[i].toString()+", ";
			}
		}
		
		try {
			connection = getConnection();
			statement = connection.createStatement();
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS "+String.valueOf(tabla)+" (id int(64) NOT NULL AUTO_INCREMENT PRIMARY KEY, " + query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			cleanup(statement,null);
		}
	}
	
	
	@Override
	public HashMap<String, String> get(CoreWHERE paranWhere,String... paranString) {
		
		// TODO Auto-generated method stub
		HashMap<String, String> value = new HashMap<String, String>();
		String getData = "";
		
		for(int i =0 ; i<paranString.length ; i++) {
			if(i==(paranString.length - 1)) {
				getData = getData +paranString[i].trim();
			}else {
				getData = getData +paranString[i].trim()+",";
			}
		}
		
		String query = "SELECT "+getData+" FROM "+this.tabla+" WHERE "+paranWhere.get()+";";
		Connection connection = null;
		Statement statement = null;
		ResultSet result = null;
		try {
			connection = getConnection();
			statement = connection.createStatement();
			result = statement.executeQuery(query);
			if ((result != null) && (result.next())){
				
				for(int i =0 ; i<paranString.length ; i++) {
					value.put(paranString[i], result.getString(paranString[i]));
				}
			}
		}catch (SQLException e) {
			this.log.fatalError("Could not create getter statement on get: "+query,e);
			e.printStackTrace();
		}finally{
			cleanup(statement,result);
		}
		return value;
	}
	@Override
	public List<HashMap<String, String>> getAll(CoreWHERE paranWhere, String criterio, String... paranString) {
		
		// TODO Auto-generated method stub
		
		String getData = "";
		
		for(int i =0 ; i<paranString.length ; i++) {
			if(i==(paranString.length - 1)) {
				getData = getData +paranString[i].trim();
			}else {
				getData = getData +paranString[i].trim()+",";
			}
		}
		List<HashMap<String, String>> lista = new ArrayList<HashMap<String, String>>();
		
		String query;
		if(criterio != null && criterio.isEmpty() && criterio == " ") {
			query = "SELECT "+getData+" FROM "+this.tabla+" WHERE "+paranWhere.get()+" "+criterio+";";
		}else {
			query = "SELECT "+getData+" FROM "+this.tabla+" WHERE "+paranWhere.get()+";";
		}
		
		Connection connection = null;
		Statement statement = null;
		ResultSet result = null;
		try {
			connection = getConnection();
			statement = connection.createStatement();
			result = statement.executeQuery(query);
			while ((result != null) && (result.next())){
				HashMap<String, String> values = new HashMap<String, String>();
				
				
				for(int i =0 ; i<paranString.length ; i++) {
					values.put(paranString[i], result.getString(paranString[i]));
				}
				
				lista.add(values);
			}
		}catch (SQLException e) {
			this.log.fatalError("Could not create getter statement on get: "+query,e);
			e.printStackTrace();
		}finally{
			cleanup(statement,result);
		}
		return lista;
	}
	
	@Override
	public List<HashMap<String, String>> getAll(String criterio, String... paranString) {
		
		// TODO Auto-generated method stub
		
		String getData = "";
		
		for(int i =0 ; i<paranString.length ; i++) {
			if(i==(paranString.length - 1)) {
				getData = getData +paranString[i].trim();
			}else {
				getData = getData +paranString[i].trim()+",";
			}
		}
		List<HashMap<String, String>> lista = new ArrayList<HashMap<String, String>>();
		String query = "SELECT "+getData+" FROM "+this.tabla+" "+criterio+";";
		Connection connection = null;
		Statement statement = null;
		ResultSet result = null;
		try {
			connection = getConnection();
			statement = connection.createStatement();
			result = statement.executeQuery(query);
			while ((result != null) && (result.next())){
				HashMap<String, String> values = new HashMap<String, String>();
				
				
				for(int i =0 ; i<paranString.length ; i++) {
					values.put(paranString[i], result.getString(paranString[i]));
				}
				
				lista.add(values);
			}
		}catch (SQLException e) {
			this.log.fatalError("Could not create getter statement on get: "+query,e);
			e.printStackTrace();
		}finally{
			cleanup(statement,result);
		}
		return lista;
	}
	@Override
	public void update(CoreWHERE paranWhere,String... args) {
		// TODO Auto-generated method stub
		
		String data = "";
		
		for(int i =0; i<args.length;i++) {
			if(!args[i].trim().contains(":")) {
				continue;
			}
			String[] local = args[i].trim().split(":");
			if(local.length<2) {
				continue;
			}
			String locaData = local[0].trim();
			String locaValue = local[1].trim();
			
			if(i==(args.length - 1)) {
				data = data + locaData +"='"+locaValue+"'";
				
			}else {
				data = data + locaData +"='"+locaValue+"',";
			}	
		}
		Connection connection = null;
		Statement statement = null;
		
	    ResultSet result = null;
	    String query = "UPDATE "+this.tabla+" SET "+data+" WHERE "+paranWhere.get() +";";
		try {
			connection = getConnection();
			statement = connection.createStatement();
			statement.executeUpdate(query);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			this.log.fatalError("Could not create getter statement on update: "+query,e);
		}
		finally{
			cleanup(statement,result);
		}
	}
	
	@Override
	public void update(String... args) {
		// TODO Auto-generated method stub
		
		String data = "";
		
		for(int i =0; i<args.length;i++) {
			if(!args[i].trim().contains(":")) {
				continue;
			}
			String[] local = args[i].trim().split(":");
			if(local.length<2) {
				continue;
			}
			String locaData = local[0].trim();
			String locaValue = local[1].trim();
			
			if(i==(args.length - 1)) {
				data = data + locaData +"='"+locaValue+"'";
				
			}else {
				data = data + locaData +"='"+locaValue+"',";
			}	
		}
		Connection connection = null;
		Statement statement = null;
		
	    ResultSet result = null;
	    String query = "UPDATE "+this.tabla+" SET "+data+";";
		try {
			connection = getConnection();
			statement = connection.createStatement();
			statement.executeUpdate(query);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			this.log.fatalError("Could not create getter statement on update: "+query,e);
		}
		finally{
			cleanup(statement,result);
		}
	}


	@Override
	public void delete(CoreWHERE paranWhere) {
		
		// TODO Auto-generated method stub
		Connection connection = null;
		Statement statement = null;
		
	    ResultSet result = null;
	    String query = "DELETE FROM "+this.tabla+" WHERE "+paranWhere.get() +";";
		try {
			connection = getConnection();
			statement = connection.createStatement();
			statement.executeUpdate(query);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			this.log.fatalError("Could not create getter statement on delete: "+query,e);
		}
		finally{
			cleanup(statement,result);
		}
	}
	
	public void close() {
		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void table(String table) {
		// TODO Auto-generated method stub
		this.tabla = table;
	}
	
	@Override
	public void executeUpdate(String query) {
		Connection connection = null;
		Statement statement = null;
	    ResultSet result = null;
		try {
			connection = getConnection();
			statement = connection.createStatement();
			statement.executeUpdate(query);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			this.log.fatalError("Could not create getter statement on insert: "+query,e);
			e.printStackTrace();
		}
		finally{
			cleanup(statement,result);
		}
		
	}

}
