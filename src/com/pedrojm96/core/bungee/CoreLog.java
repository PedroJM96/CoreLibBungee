package com.pedrojm96.core.bungee;






import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;



/**
 * Contiene los metodos para enviar mensajes en consola en el servidor de minecraft implementando la api de bukkt/spigot.
 * 
 * @author PedroJM96
 * @version 1.1 30-08-2022
 *
 */
public class CoreLog {

	public enum Color
	{
		AQUA(ChatColor.AQUA), BLACK(ChatColor.BLACK), BLUE(ChatColor.BLUE), DARK_AQUA(ChatColor.DARK_AQUA), DARK_BLUE(ChatColor.DARK_BLUE),
		DARK_GRAY(ChatColor.DARK_GRAY), DARK_GREEN(ChatColor.DARK_GREEN), DARK_PURPLE(ChatColor.DARK_PURPLE), DARK_RED(ChatColor.DARK_RED),
		GOLD(ChatColor.GOLD), GRAY(ChatColor.GRAY), GREEN(ChatColor.GREEN), LIGHT_PURPLE(ChatColor.LIGHT_PURPLE), RED(ChatColor.RED), 
		WHITE(ChatColor.WHITE), YELLOW(ChatColor.YELLOW);
		
		private ChatColor color;
		
		private Color(final ChatColor color) {
			this.color = color;
		}
		
		public ChatColor get() {
			return this.color;
		}
		
	}
	private Plugin plugin;
	private Color color;
	private String prefix;
	private boolean debug = false;
	
	
	public CoreLog(Plugin plugin,String name, Color color,boolean debug) {
		this.plugin = plugin;
		this.color = color;
		this.prefix = this.color.get()+ "["+Color.GRAY.get()+name+this.color.get()+"]";
		this.debug = debug;

	}
	
	public void seDebug(boolean debug) {
		this.debug = debug;
	}
	
	
	
	public CoreLog(Plugin plugin,String name,Color color) {
		this.plugin = plugin;
		this.color = color;
		this.prefix = this.color.get()+ "["+Color.GRAY.get()+name+this.color.get()+"]";
		
	}
	
	public void info(String info)
	{
		plugin.getProxy().getConsole().sendMessage(new TextComponent(prefix+CoreColor.colorCodes("&7 " + info)));
	}
	
	public void alert(String info)
	{
		plugin.getProxy().getConsole().sendMessage(new TextComponent(prefix+CoreColor.colorCodes("&8 " + info)));
		
	}
	
	public void debug(String info)
	{
		if(debug) {
			
			plugin.getProxy().getConsole().sendMessage(new TextComponent(prefix+CoreColor.colorCodes(" &8Debug:&c "+info)));
		}	
	}
	
	
	public void debug(String info,Throwable e)
	{
		if(debug) {
			plugin.getProxy().getConsole().sendMessage(new TextComponent(prefix+CoreColor.colorCodes(" &8Debug:&c "+info)));
			e.printStackTrace();
		}	
	}
	
	
	public void error(String info,Throwable e)
	{
		plugin.getProxy().getConsole().sendMessage(new TextComponent(prefix+CoreColor.colorCodes(" &4Error:&c "+info)));
		e.printStackTrace();
	}
	
	public void fatalError(String info,Throwable e)
	{
		plugin.getProxy().getConsole().sendMessage(new TextComponent(prefix+CoreColor.colorCodes(" &4Fatal-Error:&c "+info)));
		e.printStackTrace();
	}
	
	public void fatalError(String info)
	{
		plugin.getProxy().getConsole().sendMessage(new TextComponent(prefix+CoreColor.colorCodes(" &4Fatal-Error:&c "+info)));
	}
	
	
	public void error(String info)
	{
		plugin.getProxy().getConsole().sendMessage(new TextComponent(prefix+CoreColor.colorCodes("&c "+info)));
		
	}
	
	public void line()
	{
		plugin.getProxy().getConsole().sendMessage(new TextComponent(this.color.get()+"-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-="));
	}
	
	public void println(String string)
	{
		System.out.println(string);
	}
	
	public void print(String string)
	{
		System.out.print(string);
	}
}
