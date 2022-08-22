package com.pedrojm96.core.bungee.data;

public class CoreField {

	private String name;
	private String type;
	private Class<?> classtype;
	
	public CoreField(String name, String type, Class<?> classtype) {
		this.name = name;
		this.type = type;
		this.classtype = classtype;
	}

	@Override
	public String toString() {
		return name+" "+type;
	}
	
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the classtype
	 */
	public Class<?> getClasstype() {
		return classtype;
	}

	/**
	 * @param classtype the classtype to set
	 */
	public void setClasstype(Class<?> classtype) {
		this.classtype = classtype;
	}




	
	
}
