package bankexample;

import java.util.HashSet;
import java.util.Set;

public class BlackList {	
	private static BlackList instance = new BlackList();	
	public static BlackList getInstance(){		return instance;	}	
	private Set<String> blackSet = new HashSet<String>();	
	public void add(String user){		blackSet.add(user);	}	
	public void remove(String user){	blackSet.remove(user);	}	
	boolean contains(String user){		return blackSet.contains(user);	}	
} 
