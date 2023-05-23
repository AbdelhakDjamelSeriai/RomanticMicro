package clustering;

import java.util.Set;

import models.MyClass;


public abstract class Node implements Comparable<Node> {
	public int id;
	public abstract Set<MyClass> getClasses();
	
	public static int currentID=0;
	public static int generateID(){
//		System.err.println("current id : "+currentID);
		return currentID++;
	}
}
