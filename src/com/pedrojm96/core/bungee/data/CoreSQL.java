package com.pedrojm96.core.bungee.data;

import java.util.HashMap;
import java.util.List;
/**
 * Implementacion para facilitar la creacion de base de datos.
 * 
 * @author PedroJM96
 * @version 1.3 02-01-2020
 *
 */
public abstract interface CoreSQL {
	public abstract boolean checkStorage();
	
	
	
	
	public abstract void build(CoreField ...args);
	public abstract void build(String tabla, CoreField ...args);
	
	
	public abstract void insert(String ...args);
	
	public abstract void table(String table);
	
	
	public abstract void executeUpdate(String query);
	
	public abstract void update(CoreWHERE paranWhere,String ...args);
	public abstract void update(String ...args);
	
	public abstract void delete(CoreWHERE paranWhere);
	
	public abstract boolean columnExists(String columnname);
	public abstract String getColumnType(String columnname);
	public abstract void addColumn(String columnname,String columntype);
	
	public abstract String get(CoreWHERE paranWhere,String paranString);
	
	public abstract HashMap<String, String> get(CoreWHERE paranWhere,String... paranString);
	public abstract List<HashMap<String, String>> getAll(CoreWHERE paranWhere, String criterio, String... paranString);
	public abstract List<HashMap<String, String>> getAll(String criterio, String... paranString);
	
	public abstract boolean checkData(CoreWHERE paranWhere,String paranString);
	
	public static CoreWHERE WHERE(String ...args) {
		return new CoreWHERE(args);
	}
	
	public abstract void close();
	
	public static CoreField FIELD(String name, String type, Class<?> classtype) {
		return new CoreField(name, type, classtype);
	}
	
}
