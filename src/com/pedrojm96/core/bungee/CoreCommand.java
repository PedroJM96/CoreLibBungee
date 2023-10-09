package com.pedrojm96.core.bungee;

import java.util.HashMap;
import java.util.List;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;








/**
 * Facilita la creacion de comandos en el servidor de minecraft implementando la api de bungeecord.
 * 
 * @author PedroJM96
 * @version 1.3 06-04-2020
 *
 */
public abstract class CoreCommand extends Command{

	public CoreCommand(String name, String permission, String[] aliases) {
		super(name, permission, aliases);
	}

	public abstract void onCommand(CommandSender sender,String[] args);
	
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
	
	private HashMap<List<String>, CoreSubCommand> subcommand = new HashMap<List<String>,CoreSubCommand>();
	
	public void addSubCommand(List<String> subcmds,CoreSubCommand s){
    	subcommand.put(subcmds, s);
    }
	
	
	@Override
	public void execute(CommandSender sender, String[] arg3) {
		// TODO Auto-generated method stub
		if(!hasPerm(sender)) {
			sender.sendMessage(CoreColor.getColorTextComponent(getErrorNoPermission()));
			return;
		}
		boolean retorno = false;
		
		if (arg3.length == 0) {
			onCommand(sender,arg3);
			return;
			
		}

    	//se verifica si el comando coincide un un comando almacenado(Incluye el alias del comando)
    	for(List<String> s : subcommand.keySet()){
    		//si el comando coincide
    		if(s.contains(arg3[0].toLowerCase())){
    			//se executa el comando
    			String[] args = new String[arg3.length-1]; 
    			for(int i = 1; i<arg3.length;i++) {
    				args[i-1] = arg3[i];
    			}
    			
    			retorno =  subcommand.get(s).rum(sender,arg3[0].toLowerCase(),args);
    			break;
    		}
    	}
    	
    	//si es comando no es correto
    	if(!retorno){
    		onCommand(sender,arg3);
    		return;
    	}
		return;
	}
	
	

}
