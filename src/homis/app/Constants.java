/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package homis.app;

import java.util.HashMap;

/**
 *
 * @author DarkMatter
 */
public class Constants {
    	private static HashMap<String, String>  constantsmap;
	
	// Singleton
		private  Constants() {
			constantsmap = new HashMap<String, String>();
		}
		  
		private static Constants instance;
		  public static Constants getInstance(){
		    if ( instance == null ){
		      synchronized(Constants.class){
		        if ( instance == null ){
		          instance = new Constants();
		          
		        }
		      } 
		    }
		    return instance;
		  }
		  
			public void addValue(String id, String value) {

				if(!constantsmap.containsKey(id)){
					
					 constantsmap.put(id, value);
					       
				}else{
					constantsmap.remove(id);
					constantsmap.put(id, value);
				}
			}

			public String getValue(String identifier) {
			    return constantsmap.get(identifier);
			}
			
			public boolean hasKey(String Key){
				return constantsmap.containsKey(Key);
				
			}

			public HashMap<String, String> getEnteredValues() {
			    return constantsmap;
			}

			public void remove(String key){
				constantsmap.remove(key);
			}
			
			public int count(){
				return constantsmap.size();
			}
			
			public void clean() {
			    constantsmap.clear();
			  
			}
		  
}
