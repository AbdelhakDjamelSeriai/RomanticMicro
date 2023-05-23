package clustering;

import java.util.Set;
import java.util.TreeSet;

import models.MyClass;

public class BinaryTree extends Node {
	private Node node1;
	private Node node2;
	
	
	
	public BinaryTree() {
		super();
		this.id=Node.generateID();
	}

	public Node getNode1() {
		return node1;
	}
	public void setNode1(Node node1) {
		this.node1 = node1;
	}
	public Node getNode2() {
		return node2;
	}
	public void setNode2(Node node2) {
		this.node2 = node2;
	}
	
	public int numberOfLeaves() {
		if (node1 instanceof Leaf) {
			if (node2 instanceof Leaf) {
				return 2;
			} else {
				return 1 + ((BinaryTree)node2).numberOfLeaves();
			}
		} else {
			if (node2 instanceof Leaf) {
				return ((BinaryTree)node1).numberOfLeaves() + 1;
			} else {
				return ((BinaryTree)node1).numberOfLeaves() + ((BinaryTree)node2).numberOfLeaves();
			}
		}
		
	}
	@Override
	public Set<MyClass> getClasses() {
		Set<MyClass> classes = new TreeSet<MyClass>();
		classes.addAll(node1.getClasses());
		classes.addAll(node2.getClasses());
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
		return s1.compareTo(s2);
	}


	
	
	
}
