package clustering;

import java.util.HashSet;
import java.util.Set;

import models.MyClass;

public class Cluster {
	
	private Set<MyClass> classes= new HashSet<MyClass>();
	private double quality;
	
	public Cluster(Set<MyClass> classes, Double quality){
		this.classes=classes;
		this.quality=quality;
	}

	public double getQuality() {
		return quality;
	}

	public Set<MyClass> getClasses() {
		return classes;
	}
	
	public String toString(){
		return classes.toString()+" "+quality;
	}

}
