package com.jd.mja.rdp.storm.util;

import java.util.*;

public class InitProperties {
	  private ResourceBundle prop;


	public InitProperties(String path) {
		prop = ResourceBundle.getBundle(path.trim());

	}
	  public  String read(String name){
		  if (prop==null) {
			return null;
		  }
	      return  prop.getString(name);
	  }
		public  Integer readInteger(String name) {
			return readInteger(name,null);
		}
		public  Integer readInteger(String name,Integer defult){
		if (prop==null) {
			return defult;
		}
		try {
			String _p=prop.getString(name);
			return Integer.parseInt(_p);
		} catch (NumberFormatException e) {
 			return defult;
		}
	}

	  
 	  
}
