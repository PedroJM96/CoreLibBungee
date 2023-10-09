package com.pedrojm96.core.bungee;

/**
 * Utilidades para el core.
 * 
 * @author PedroJM96
 * @version 1.5 03-09-2020
 *
 */
public class CoreUtils {
	
	public static Class<?> getClass(String name)
	  {
	    try
	    {
	      return Class.forName(name);
	    }
	    catch (ClassNotFoundException e) {
	    	
	    }
	    return null;
	  }
	
	public static boolean isdouble(String s){
		try{
			@SuppressWarnings("unused")
			double i = Double.parseDouble(s);
			return true;
		}
		catch(NumberFormatException er){
			return false;
		}
		
	}
	
	public static boolean isint(String s){
		try{
			@SuppressWarnings("unused")
			int i = Integer.parseInt(s);
			return true;
		}
		catch(NumberFormatException er){
			return false;
		}
		
	}
	
	public static int toint(String s){
		try{
			int i = Integer.parseInt(s);
			return i;
		}
		catch(NumberFormatException er){
			return 0;
		}
		
	}
	
	
	public static long toLong(String s){
		try{
			
			long i = Long.parseLong(s);
			return i;
		}
		catch(NumberFormatException er){
			return 0;
		}
		
	}
	
	public static boolean isEnum(Class<?> class1, String value ) {
		try {
			@SuppressWarnings({ "unused", "unchecked", "rawtypes" })
			Object obj = Enum.valueOf((Class<Enum>)class1, value);
			return true;
		}catch(IllegalArgumentException ex){
			return false;
		}
		
	}
	
	
	public static boolean isinteger(String s){
		try{
			@SuppressWarnings("unused")
			Integer i = Integer.valueOf(s);
			return true;
		}
		catch(NumberFormatException er){
			return false;
		}
		
	}
	public static Integer integerValue(String s){
		try{
			Integer i = Integer.valueOf(s);
			return i;
		}
		catch(NumberFormatException er){
			return 0;
		}
		
	}
	
	public static Double doubleValue(String s){
		try{
			Double i = Double.valueOf(s);
			return i;
		}
		catch(NumberFormatException er){
			return 0.0;
		}
		
	}
	
	 public  static String formatime(long segundos){
		    String format = "<dd>d:<hh>h:<mm>m:<ss>s";
	    	int d =0;
	    	int h = 0;
	    	int m = 0;
	    	while(segundos>60){
	    		if(h>12){
	    			d++;
	    			h=0;
	    		}
	    		if(m>60){
	    			h++;
	    			m=0;
	    		}
	    		segundos = segundos - 60;
	    		m++;
	    	}
	    	if(d==0 && h==0 && m==0){
	    		String forma = format.replaceAll("<dd>", String.valueOf(d)).replaceAll("<hh>", String.valueOf(h)).replaceAll("<mm>", String.valueOf(m)).replaceAll("<ss>", String.valueOf(segundos));
	    		return forma;
	    	}
	    	
	    	if(d==0 && h==0){
	    		String forma = format.replaceAll("<dd>", String.valueOf(d)).replaceAll("<hh>", String.valueOf(h)).replaceAll("<mm>", String.valueOf(m)).replaceAll("<ss>", String.valueOf(segundos));
	    		return forma;
	    	}
	    	if(d==0){
	    		String forma = format.replaceAll("<dd>", String.valueOf(d)).replaceAll("<hh>", String.valueOf(h)).replaceAll("<mm>", String.valueOf(m)).replaceAll("<ss>", String.valueOf(segundos));
	    		return forma;
	    	}
	    	String forma = format.replaceAll("<dd>", String.valueOf(d)).replaceAll("<hh>", String.valueOf(h)).replaceAll("<mm>", String.valueOf(m)).replaceAll("<ss>", String.valueOf(segundos));
			return forma;
	    }
	
	 
	 public  static String formatimeClear(long segundos){
		 	int d =0;
	    	int h = 0;
	    	int m = 0;
	    	while(segundos>60){
	    		if(h>12){
	    			d++;
	    			h=0;
	    		}
	    		if(m>60){
	    			h++;
	    			m=0;
	    		}
	    		segundos = segundos - 60;
	    		m++;
	    	}
	    	if(d==0 && h==0 && m==0){
	    		String forma = segundos + "s";
	    		return forma;
	    	}
	    	
	    	if(d==0 && h==0){
	    		String forma = m+"m " + segundos + "s";
	    		return forma;
	    	}
	    	if(d==0){
	    		String forma = h+"h " + m+"m " + segundos + "s";
	    		return forma;
	    	}
	    	String forma = d + "d " + h+"h " + m+"m " + segundos + "s";
			return forma;
	    }
	 
	 
	 public static String timeLeft(int timeoutSeconds)
	 {
	     int days = (int) (timeoutSeconds / 86400);
	     int hours = (int) (timeoutSeconds / 3600) % 24;
	     int minutes = (int) (timeoutSeconds / 60) % 60;
	     int seconds = timeoutSeconds % 60;
	     return (days > 0 ? " " + days + " d" : "") + (hours > 0 ? " " + hours + " h"  : "")
	             + (minutes > 0 ? " " + minutes + " m"  : "") + (seconds > 0 ? " " + seconds + " s" : "");
	 }
	 
	 
}
