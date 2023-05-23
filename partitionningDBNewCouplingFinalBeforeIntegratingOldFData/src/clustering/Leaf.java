package clustering;

import java.util.Set;
import java.util.TreeSet;

import models.MyClass;

public class Leaf extends Node{
	private MyClass clazz;

	public Leaf() {
		super();
		this.id=Node.generateID();
//		System.err.println("leaf id : "+id);
	}

	public MyClass getClazz() {
		return clazz;
	}

	public void setClazz(MyClass clazz) {
		this.clazz = clazz;
	}

	public Leaf(MyClass clazz) {
		super();
		this.clazz = clazz;
		this.id=Node.generateID();
	}

	@Override
	public Set<MyClass> getClasses() {
		Set<MyClass> classes = new TreeSet<MyClass>();
		classes.add(clazz);
		return classes;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Node o) {
		String s1="", s2="";
		s1+=this.id;
		s2+=o.id;
//		System.err.println(s1.compareTo(s2));
		return s1.compareTo(s2);
	}

	
	
	
}
