package com.pedrojm96.core.bungee;

import net.md_5.bungee.api.CommandSender;

/**
 * Facilita la creacion de subcomandos en el servidor de minecraft implementando la api de bungeecoord.
 * 
 * @author PedroJM96
 * @version 1.0 29-12-2018
 *
 */
public abstract class CoreSubCommand {
	public abstract boolean onSubCommand(CommandSender sender,String[] args);
	
	public abstract String getErrorNoPermission();
	
	
	public abstract String getPerm();
	
	public boolean hasPerm(CommandSender sender){
		if(getPerm() == null){
			return true;
		}
		if(sender.hasPermission(getPerm())){
			return true;
		}
		return false;
	}
	
	
	public boolean rum(CommandSender sender,String cmd,String[] args){
		if (!hasPerm(sender)){
			sender.sendMessage(CoreColor.getColorTextComponent(getErrorNoPermission()));
			return true;
    	}
		
		return onSubCommand(sender,args);
	}
} 
