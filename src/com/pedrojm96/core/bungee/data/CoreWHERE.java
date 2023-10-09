package com.pedrojm96.core.bungee.data;


/**
 * Facilita la selecion de consultas sql.
 * 
 * @author PedroJM96
 * @version 1.0 13-10-2018
 *
 */
public class CoreWHERE {

	private String where = "";
	
	public CoreWHERE(String ...args) {
		
		for (int i = 0; i<args.length;i++) {
			if(!args[i].trim().contains(":")) {
				continue;
			}
			String[] local = args[i].trim().split(":");
			if(local.length<2) {
				continue;
			}
			String locaWhere = local[0].trim();
			String locaValue = local[1].trim();
			if(i==(args.length - 1)) {
				this.where = this.where +locaWhere+" = '"+locaValue+"'";
			}else {
				this.where = this.where +locaWhere+" = '"+locaValue+"'"+" AND ";
			}
		}
	}
	
	public String get() {
		return this.where.trim();
	}
}
