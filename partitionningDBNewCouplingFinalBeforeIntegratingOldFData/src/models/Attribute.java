package models;

public class Attribute {
	
	private String name;
	private String attrOfClass;
	
	public Attribute(String attName, String receiver) {
		name= attName;
		attrOfClass=receiver;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAttrOfClass() {
		return attrOfClass;
	}
	public void setAttrOfClass(String attrOfClass) {
		this.attrOfClass = attrOfClass;
	}
	
	public boolean equals(Object obj){
		Attribute att= (Attribute)obj;
		return att.toString().equals(this.toString());
	}
	
	public String toString(){
		return name+" "+attrOfClass;
	}

}
