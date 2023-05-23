package models;

public class PairsDFreq {
	
	private double D;
	private double freq;
	private MyClass pairClass;
	
	public double getD() {
		return D;
	}
	public void setD(double access) {
		D = access;
	}
	public double getFreq() {
		return freq;
	}
	public void setFreq(double freq2) {
		this.freq = freq2;
	}
	
	public String toString(){
		return "<"+pairClass+","+D+","+freq+">";
	}
	public MyClass getPairClass() {
		return pairClass;
	}
	public void setPairClass(MyClass pairClass) {
		this.pairClass = pairClass;
	}

}
