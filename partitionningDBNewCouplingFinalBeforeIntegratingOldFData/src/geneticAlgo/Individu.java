package geneticAlgo;

import identificationProcess.FDataComputer;
import identificationProcess.MeusuringMetricsNewFdata;
import identificationProcess.MeasuringMetrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import models.MyClass;

public class Individu {
	 //Attributes
	private double quality;
	private double coupling;
	private double externalCoupling;
	private double lcc;
	private double probability;
	private double internalData;
	private double externalData;
	private double fFonc;
	private double fdata;
	public static int idcpt = 0;
	private int id;
	private int age;
	public static double mutRate = 0.01;
	public static double crossRate = 1.0;
	private Set<Set<MyClass>> chromosomes = new HashSet<Set<MyClass>>();
	//Constructor
	public Individu() {
		// TODO Auto-generated constructor stub
		this.setAge(0);
		this.setCoupling(0.0);
		this.setLcc(0.0);
		this.setQuality(0.0);
		this.setId(idcpt); idcpt++;
	}

	//used for Random Weighted GA
	// evaluate the coupling and lcc, externalCoupling and data of an individual	
	public void evaluate(){
		ArrayList<Double> cpl = new ArrayList<Double>();
		ArrayList<Double> extCpl = new ArrayList<Double>();
		ArrayList<Double> lcc = new ArrayList<Double>();
		ArrayList<Double> indata = new ArrayList<Double>();
		ArrayList<Double> exdata = new ArrayList<Double>();
		Set<MyClass> own, others;
		for(Set<MyClass> gene : this.getChromosomes() ){
			cpl.add(MeasuringMetrics.measureInternalCoupling1(new ArrayList<MyClass>(gene)));
			//cpl.add(QualityFunction.couplingNew(gene, MainAG.calls));
			extCpl.add(MeasuringMetrics.measureExternalCoupling1(new ArrayList<MyClass>(gene)));
			lcc.add(MeasuringMetrics.measureCohision(new ArrayList<MyClass>(gene)));
			//data part
			
			FDataComputer  fdatacomputer= new FDataComputer();
			double fIntra=fdatacomputer.computeFIntra(MeusuringMetricsNewFdata.DFrequnencyMatrix, 
			new ArrayList<MyClass>(gene), MeusuringMetricsNewFdata.nbrAcessDataApp);
			double fInter=fdatacomputer.computeFInter(MeusuringMetricsNewFdata.DFrequnencyMatrix, 
			new ArrayList<MyClass>(gene), MeusuringMetricsNewFdata.nbrAcessDataApp);
			indata.add(fIntra);
			exdata.add(fInter);
			
		}
		
	/*	double maxCpl = cpl.stream().mapToDouble(Double::doubleValue).max().getAsDouble();
		double maxLcc = lcc.stream().mapToDouble(Double::doubleValue).max().getAsDouble();
		double maxExtCpl = extCpl.stream().mapToDouble(Double::doubleValue).max().getAsDouble();
		double maxIndata = indata.stream().mapToDouble(Double::doubleValue).max().getAsDouble();
		double maxExdata = exdata.stream().mapToDouble(Double::doubleValue).max().getAsDouble();*/
		
		//normalize coupling, cohesion and data values		
/*		cpl = replaceVal(cpl, maxCpl);
		lcc = replaceVal(lcc, maxLcc);
		extCpl = replaceVal(extCpl, maxExtCpl);
		indata = replaceVal(indata, maxIndata);
		exdata = replaceVal(exdata, maxExdata);*/
		
		// Average coupling and lcc
		this.setCoupling(avg(cpl)); 
		this.setLcc(avg(lcc));
		this.setExternalCoupling(avg(extCpl));
		this.setInternalData(avg(indata));
		this.setExternalData(avg(exdata));
	}
	
	public ArrayList<Set<MyClass>> sortByMode(){
		ArrayList<Set<MyClass>> list = new ArrayList<Set<MyClass>>(this.getChromosomes());
		//list.addAll(this.getChromosomes());
		
		switch (GABasicOps.mode) {
		case 0: //coupling first
			Collections.sort(list, new Comparator<Set<MyClass>>() {
				@Override
				public int compare(Set<MyClass> e1, Set<MyClass> e2) {
					Double cpl1 = MeasuringMetrics.measureInternalCoupling1(new ArrayList<MyClass>(e1));
					Double cpl2 = MeasuringMetrics.measureInternalCoupling1(new ArrayList<MyClass>(e2));
					int cmp = cpl1.compareTo(cpl2);
					if (cmp != 0) {
			               return cmp;
			            }

					Double lcc1 = MeasuringMetrics.measureCohision(new ArrayList<MyClass>(e1));
					Double lcc2 = MeasuringMetrics.measureCohision(new ArrayList<MyClass>(e2));
					return lcc1.compareTo(lcc2);
						
				}
			});
			//list.sort(Comparator.comparing(s -> QualityFunction.coupling((Set<MyClass>)s)).thenComparing(s -> QualityFunction.lccMyClasses((Set<MyClass>)s)).reversed());	
				//result = list.stream().sorted((gene1, gene2) -> Double.compare(QualityFunction.coupling(gene1), QualityFunction.coupling(gene2))).collect(Collectors.toCollection(ArrayList::new));
			break;
		default : 
		//	QualityFunction.initWeights();
			/*Collections.sort(list, new Comparator<Set<MyClass>>() {
				@Override
				public int compare(Set<MyClass> e1, Set<MyClass> e2) {
					Double q1 = QualityFunction.QualityFun(e1);
					Double q2 = QualityFunction.QualityFun(e2);
					
					if (q1>q2)
						return 1;
					else if (q1==q2)
						return 0;
					else
						return -1;
						
				}
			});*/
			//list.sort(Comparator.comparing(s -> QualityFunction.QualityFun((Set<MyClass>)s)).reversed());
			//list = list.stream().sorted((gene1, gene2) -> Double.compare(QualityFunction.QualityFun2(gene1, MainAG.calls), QualityFunction.QualityFun2(gene2, MainAG.calls))).collect(Collectors.toCollection(ArrayList::new));
			list = list.stream().sorted((gene1, gene2) -> Double.compare(MeusuringMetricsNewFdata.FFMicro(new ArrayList<MyClass>(gene1)), MeusuringMetricsNewFdata.FFMicro(new ArrayList<MyClass>(gene2)))).collect(Collectors.toCollection(ArrayList::new));
			break;
		case 1:
			//cohesion first   MeusuringMetricsNewFdata.FFMicro(new ArrayList<MyClass>(gene1))
			Collections.sort(list, new Comparator<Set<MyClass>>() {
				@Override
				public int compare(Set<MyClass> e1, Set<MyClass> e2) {
					Double lcc1 = MeasuringMetrics.measureCohision(new ArrayList<MyClass>(e1));
					Double lcc2 = MeasuringMetrics.measureCohision(new ArrayList<MyClass>(e2));
					int cmp = lcc1.compareTo(lcc2);
					if (cmp != 0) {
			               return cmp;
			            }

					Double cpl1 = MeasuringMetrics.measureInternalCoupling1(new ArrayList<MyClass>(e1));
					Double cpl2 =  MeasuringMetrics.measureInternalCoupling1(new ArrayList<MyClass>(e2));
					//Double cpl1 = QualityFunction.couplingNew(e1,MainAG.calls);
					//Double cpl2 = QualityFunction.couplingNew(e2,MainAG.calls);
					return cpl1.compareTo(cpl2);
						
				}
			});
			//list.sort(Comparator.comparing(s -> QualityFunction.lccMyClasses((Set<MyClass>)s)).thenComparing(s -> QualityFunction.coupling((Set<MyClass>)s)).reversed());
			break;
			
		}
		Collections.reverse(list);
		return list;
		
	}

/*	public static ArrayList<Double> replaceVal(ArrayList<Double> values, double maxi){
		ArrayList<Double> news = new ArrayList<Double>();
		for(double d : values){
			news.add( maxi!= 0? d / maxi : 0);
		}
		return news;
	}*/
	
	public double fitness(){
		double val = 0;
		for(Set<MyClass> gene : this.getChromosomes() ){
			if(MeusuringMetricsNewFdata.FFMicro(new ArrayList<MyClass>(gene)) > 0.6){
				val++;
			}
		}
		val /= this.getChromosomes().size();
		this.setQuality(val);
		return val;
		
	}
	//check if an individual is dominated - case two objectives
	public boolean dominance1(Individu e){
		if(e.getCoupling() > this.getCoupling()){
			return true;
		}else if(e.getLcc() > this.getLcc()){
			return true;
		}else if(e.getExternalCoupling() < this.getExternalCoupling()){
			return true;
		} else 
			return false;
		
	}
	public boolean dominanceAttempt(Individu e){
		if(this.getFFonc() > e.getFFonc()){
			return false;
		}else if(this.getFFonc() < e.getFFonc()){
			if(this.getFData() > e.getFData()){
				return false;
			}else
				return true;
			
		}else if(this.getFFonc() == e.getFFonc()){
			if(this.getFData() < e.getFData()){
				return true;
			}
		}
		return false;
		
	}
	public boolean dominance(Individu e){
		if(e.getFFonc() > this.getFFonc()){
			if(e.getFData() >= this.getFData()){
				return true;
			}
		}else if(e.getFFonc() == this.getFFonc()){
			if(e.getFData() > this.getFData()){
				return true;
			}
		}
		return false;
		
	}
	public static double avg(ArrayList<Double> list){
		double avgr = 0;
		for(double d : list){
			avgr += d;
		}
		return avgr / list.size();
	}
	
	
	/**
	 * Calculates a value between 0 and 1, given the precondition that value
	 * is between min and max. 0 means value = max, and 1 means value = min.
	 */
	static double normalize(double value, double min, double max) {
	    return 1 - ((value - min) / (max - min));
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((chromosomes == null) ? 0 : chromosomes.hashCode());
		long temp;
		temp = Double.doubleToLongBits(fFonc);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(fdata);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Individu other = (Individu) obj;
		if (chromosomes == null) {
			if (other.chromosomes != null)
				return false;
		} else if (!chromosomes.equals(other.chromosomes))
			return false;
		if (Double.doubleToLongBits(fFonc) != Double
				.doubleToLongBits(other.fFonc))
			return false;
		if (Double.doubleToLongBits(fdata) != Double
				.doubleToLongBits(other.fdata))
			return false;
		return true;
	}

	public void setAge(int a){
		age = a;
	}
	public void setId(int a){
		id = a;
	}
	public void setQuality(double q){
		quality = q;
	}
	public void setCoupling(double cpl){
		coupling = cpl;
	}
	public void setExternalCoupling(double cpl){
		externalCoupling = cpl;
	}
	public void setLcc(double lc){
		lcc = lc;
	}
	public void setFFonc(double d){
		fFonc = d;
	}
	public void setFData(double d){
		fdata = d;
	}
	public void setExternalData(double d){
		externalData = d;
	}
	public void setInternalData(double d){
		internalData = d;
	}
	public void setProbability(double p){
		probability = p;
	}
	public void setChromosomes(Set<Set<MyClass>> ch){
		chromosomes.addAll(ch);
	}
	public void addChromosome(Set<MyClass> ch){
		chromosomes.add(ch);
	}
	public int getAge(){
		return age;
	}
	public int getId(){
		return id;
	}
	public double getQuality(){
		return quality;
	}
	public double getCoupling(){
		return coupling;
	}
	public double getExternalCoupling(){
		return externalCoupling;
	}
	public double getLcc() {
		return lcc;
	}
	public double getFFonc(){
		return fFonc;
	}
	public double getFData(){
		return fdata;
	}
	public double getExternalData(){
		return externalData;
	}
	public double getInternalData(){
		return internalData;
	}
	public double getProbability(){
		return probability;
	}
	public Set<Set<MyClass>> getChromosomes(){
		return chromosomes;
	}
	
	
}
