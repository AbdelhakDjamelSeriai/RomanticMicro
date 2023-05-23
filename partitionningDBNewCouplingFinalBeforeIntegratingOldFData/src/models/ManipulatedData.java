package models;

public class ManipulatedData {
	
	private String name;
	private int frequency;
	
	public ManipulatedData(String dataname, int datafrequency) {
		name= dataname;
		frequency= datafrequency;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getFrequency() {
		return frequency;
	}
	
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public String toString(){
		return "<"+name+","+frequency+">";
	}
	
	public boolean equals(Object obj){
		ManipulatedData d= (ManipulatedData) obj;
		return name.equals(d.getName());
	}

	public void incrementFrequency(int i) {
		
		frequency++;
	}
}
