package geneticAlgo;

public class Chromosome {

	private double quality;
	private double coupling;
	private double lcc;
	
	public double getQuality(){
		return quality;
	}
	public double getCoupling(){
		return coupling;
	}
	public double getLcc() {
		return lcc;
	}
	public void setQuality(double q){
		quality = q;
	}
	public void setCoupling(double cpl){
		coupling = cpl;
	}
	public void setLcc(double lc){
		lcc = lc;
	}
}
