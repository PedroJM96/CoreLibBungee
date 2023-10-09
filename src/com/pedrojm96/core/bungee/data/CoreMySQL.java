package com.pedrojm96.core.bungee.data;

import java.sql.Connection;
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
import com.pedrojm96.core.bungee.CoreUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;






/**
 * Facilita la creacion de base de datos mysql.
 * 
 * @author PedroJM96
 * @version 1.4 21-03-2020
 *
 */
public class CoreMySQL implements CoreSQL {

	private CoreLog log;
	private String tabla;
	private String host, database, username, password;
	private int port;
	private HikariDataSource dataSource;
	
	
	
	public Map<String, String> columns = new HashMap<String, String>();
	
	
	public CoreMySQL(CorePlugin plugin,String host,int port,String database,String username,String password,String tabla) {
		this.log = plugin.getLog();
		this.tabla = tabla;
		
		this.log.info("Data set to MySQL");
		this.host = host;
		if((this.host==null)||(this.host.equals(""))){
			this.log.alert("DMYSQL() - host nulo");
		}
		this.port = port;
		this.database = database;
		if((this.database==null)||(this.database.equals(""))){
			this.log.alert("DMYSQL() - database nulo");
		}
		this.username = username;
		if((this.username==null)||(this.username.equals(""))){
			this.log.alert("DMYSQL() - username nulo");
		}
		this.password = password;
		if((this.password==null)||(this.password.equals(""))){
			this.log.alert("DMYSQL() - password nulo");
		}
		
		HikariConfig config = new HikariConfig();
		config.setPoolName(plugin.getInstance().getDescription().getName()+"MySQLPool");
		config.setJdbcUrl("jdbc:mysql://"+this.host+":"+this.port+"/"+this.database);
		int version = CoreUtils.toint(plugin.getInstance().getProxy().getVersion().split(":")[4]);
		
		  if(version>=1610) {
			  config.setDriverClassName("com.mysql.cj.jdbc.Driver");
			  
		  }else { 
			  config.setDriverClassName("com.mysql.jdbc.Driver");
			 
		  }

		config.setUsername(this.username);
		config.setPassword(this.password);
		config.setMaxLifetime(180000L);
		config.setIdleTimeout(60000L);
		config.setMinimumIdle(2);
		config.setMaximumPoolSize(20);
		config.setConnectionTimeout(10000);
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		config.addDataSourceProperty("characterEncoding", "utf8");
		config.addDataSourceProperty("encoding", "UTF-8");
		config.addDataSourceProperty("useUnicode", "true");
		
		
		
		this.dataSource = new HikariDataSource(config);
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

		try {
			connection = dataSource.getConnection();
			String query = "ALTER TABLE "+this.tabla+" ADD "+columnname+" "+columntype;
			statement = connection.createStatement();
			statement.executeUpdate(query);
			this.log.info("Added column: "+columnname+" "+columntype);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.log.alert("The table "+this.tabla+" does not exist");
		}finally{
			cleanup(connection,statement,null);
		}
	}
	
	
	@Override
	public boolean checkStorage() {
		boolean existe;
		Connection connection = null;
		Statement statement = null;
		ResultSet result = null;
		
		try {
			connection = dataSource.getConnection();
			String query = "SELECT * FROM "+this.tabla;
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
			cleanup(connection,statement,result);
		}
		return existe;
	}
	
	protected void cleanup(Connection connection, Statement statement,ResultSet result){
		if (connection != null) {
			try
			{
				connection.close();
			}
			catch (SQLException e)
			{
		    	  this.log.error("SQLException on cleanup [connection].");  
			}
		}
		
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
	public void build(CoreField ...paramsField) {
		// TODO Auto-generated method stub
		this.log.info("Creating table " + this.tabla);
		Connection connection = null;
		Statement statement = null;
		String query = "";
		for (int i = 0; i<paramsField.length;i++) {
			if(i==(paramsField.length - 1)) {
				query = query + paramsField[i].toString()+");";
			}else {
				query = query + paramsField[i].toString()+", ";
			}
		}
		
		try {
			
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS "+this.tabla+" (id int(64) NOT NULL AUTO_INCREMENT PRIMARY KEY, " + query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			cleanup(connection,statement,null);
		}
	}
	

	@Override
	public void build(String tabla, CoreField ...paramsField) {
		// TODO Auto-generated method stub
		
		
		
		this.log.info("Creating table " + String.valueOf(tabla));
		Connection connection = null;
		Statement statement = null;
		String query = "";
		for (int i = 0; i<paramsField.length;i++) {
			if(i==(paramsField.length - 1)) {
				query = query + paramsField[i].toString()+");";
			}else {
				query = query + paramsField[i].toString()+", ";
			}
		}
		
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS "+tabla+" (id int(64) NOT NULL AUTO_INCREMENT PRIMARY KEY, " + query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			cleanup(connection,statement,null);
		}
	}
	
	

	@Override
	public boolean checkData(CoreWHERE paranWhere,String paranString) {
		Connection connection = null;
		boolean existe = false;
		Statement statement = null;
		ResultSet result = null;
		try {
			connection = dataSource.getConnection();
			String query = "SELECT '"+paranString+"' FROM "+this.tabla+" WHERE "+paranWhere.get()+";";
			statement = connection.createStatement();
			result = statement.executeQuery(query);
			existe = result.next();
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			existe = true;
		}finally{
			cleanup(connection,statement,result);
		}
		return existe;
		
	}

	@Override
	public void insert(String... args) {
		Connection connection = null;
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
		Statement statement = null;
		
	    ResultSet result = null;
	    String query = "INSERT INTO "+this.tabla+" ("+data+") VALUES ("+value+");";
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			statement.executeUpdate(query);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			this.log.fatalError("Could not create getter statement on insert: "+query,e);
			e.printStackTrace();
		}
		finally{
			cleanup(connection,statement,result);
		}
		
	}
	
	
	
	
	
	

	@Override
	public String get(CoreWHERE paranWhere,String paranString) {
		Connection connection = null;
		// TODO Auto-generated method stub
		String value = "";
		String query = "SELECT "+paranString+" FROM "+this.tabla+" WHERE "+paranWhere.get()+";";
		Statement statement = null;
		ResultSet result = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			result = statement.executeQuery(query);
			if ((result != null) && (result.next())){
				value = result.getString(paranString);
			}
		}catch (SQLException e) {
			this.log.fatalError("Could not create getter statement on get: "+query,e);
			e.printStackTrace();
		}finally{
			cleanup(connection,statement,result);
		}
		return value;
	}
	@Override
	public HashMap<String, String> get(CoreWHERE paranWhere,String... paranString) {
		Connection connection = null;
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
		Statement statement = null;
		ResultSet result = null;
		try {
			connection = dataSource.getConnection();
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
			cleanup(connection,statement,result);
		}
		return value;
	}
	
	@Override
	public List<HashMap<String, String>> getAll(CoreWHERE paranWhere, String criterio, String... paranString) {
		Connection connection = null;
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
		if(criterio != null && !criterio.isEmpty() && criterio != " ") {
			query = "SELECT "+getData+" FROM "+this.tabla+" WHERE "+paranWhere.get()+" "+criterio+";";
		}else {
			query = "SELECT "+getData+" FROM "+this.tabla+" WHERE "+paranWhere.get()+";";
		}
		
		
		Statement statement = null;
		ResultSet result = null;
		try {
			connection = dataSource.getConnection();
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
			cleanup(connection,statement,result);
		}
		return lista;
	}
	
	@Override
	public List<HashMap<String, String>> getAll(String criterio, String... paranString) {
		Connection connection = null;
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
		Statement statement = null;
		ResultSet result = null;
		try {
			connection = dataSource.getConnection();
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
			cleanup(connection,statement,result);
		}
		return lista;
	}
	
	
	

	@Override
	public void update(CoreWHERE paranWhere,String... args) {
		// TODO Auto-generated method stub
		Connection connection = null;
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
		Statement statement = null;
		
	    ResultSet result = null;
	    String query = "UPDATE "+this.tabla+" SET "+data+" WHERE "+paranWhere.get() +";";
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			statement.executeUpdate(query);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			this.log.fatalError("Could not create getter statement on update: "+query,e);
		}
		finally{
			cleanup(connection,statement,result);
		}
	}
	
	@Override
	public void update(String... args) {
		// TODO Auto-generated method stub
		Connection connection = null;
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
		Statement statement = null;
		
	    ResultSet result = null;
	    String query = "UPDATE "+this.tabla+" SET "+data+";";
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			statement.executeUpdate(query);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			this.log.fatalError("Could not create getter statement on update: "+query,e);
		}
		finally{
			cleanup(connection,statement,result);
		}
	}
	

	@Override
	public void delete(CoreWHERE paranWhere) {
		Connection connection = null;
		// TODO Auto-generated method stub
		Statement statement = null;
		
	    ResultSet result = null;
	    String query = "DELETE FROM "+this.tabla+" WHERE "+paranWhere.get() +";";
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			statement.executeUpdate(query);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			this.log.fatalError("Could not create getter statement on delete: "+query,e);
		}
		finally{
			cleanup(connection,statement,result);
		}
	}



	@Override
	public void close() {
		// TODO Auto-generated method stub
		dataSource.close();
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
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			statement.executeUpdate(query);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			this.log.fatalError("Could not create getter statement on insert: "+query,e);
			e.printStackTrace();
		}
		finally{
			cleanup(connection,statement,result);
		}
		
	}
	
	

}
