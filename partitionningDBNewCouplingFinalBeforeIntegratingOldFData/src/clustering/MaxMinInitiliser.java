package clustering;
			
import identificationProcess.FDataComputer;
import identificationProcess.MeasuringMetrics;
import identificationProcess.MeusuringMetricsNewFdata;
			


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
			


import models.MyClass;
			
public class MaxMinInitiliser {
				
				// Initialize maxes and mins used to normalize values when building 
				//a dendrogramme
		public static int initialiseMaxMin(int nombre, Node[] noeudsArray) {
			double maxCohesion;
			double minCohesion;
			double maxInternalCoupling;
			double minInternalCoupling;
			double maxExternalCoupling;
			double minExternalCoupling;
			double maxFIntra;
			double minFIntra;
			double maxFInter;
			double minFInter;
			Node n1 = noeudsArray[0];
			Node n2 = noeudsArray[1];
			Set<MyClass> cluster = new TreeSet<MyClass>();
			cluster.addAll(n1.getClasses());
			cluster.addAll(n2.getClasses());
			maxCohesion=  MeasuringMetrics.measureCohision(new ArrayList<MyClass>(cluster));
			minCohesion=maxCohesion;
			maxInternalCoupling= MeasuringMetrics.measureInternalCoupling1(new ArrayList<MyClass>(cluster));
			minInternalCoupling= maxInternalCoupling;
			maxExternalCoupling=MeasuringMetrics.measureExternalCoupling1(new ArrayList<MyClass>(cluster));
			minExternalCoupling=maxExternalCoupling;
			FDataComputer  fdatacomputer= new FDataComputer();
			maxFIntra=fdatacomputer.computeFIntra(MeusuringMetricsNewFdata.DFrequnencyMatrix, 
			new ArrayList<MyClass>(cluster), MeusuringMetricsNewFdata.nbrAcessDataApp);
			minFIntra=maxFIntra;
			maxFInter=fdatacomputer.computeFInter(MeusuringMetricsNewFdata.DFrequnencyMatrix, 
			new ArrayList<MyClass>(cluster), MeusuringMetricsNewFdata.nbrAcessDataApp);
			minFInter=maxFInter;
			nombre++;
			double valueCohision, valueInternlCoupling, valueExternalCoupling,
			valueFInter, valueFIntra;
			//System.out.println("//////////////////////////////////////////////");
			//System.out.println(maxFIntra+" "+minFInter);
			for (int i = 0; i < noeudsArray.length; i++) {
			for (int j = i + 1; j < noeudsArray.length; j++) {
			Set<MyClass> cluster1 = new TreeSet<MyClass>();
			cluster1.addAll(noeudsArray[i].getClasses());
			cluster1.addAll(noeudsArray[j].getClasses());
			valueCohision =  MeasuringMetrics.measureCohision(new ArrayList<MyClass>(cluster1));
			valueInternlCoupling= MeasuringMetrics.measureInternalCoupling1(new ArrayList<MyClass>(cluster1));
			valueExternalCoupling= MeasuringMetrics.measureExternalCoupling1(new ArrayList<MyClass>(cluster1));
			valueFIntra= fdatacomputer.computeFIntra(MeusuringMetricsNewFdata.DFrequnencyMatrix, 
				new ArrayList<MyClass>(cluster1), MeusuringMetricsNewFdata.nbrAcessDataApp);
			valueFInter=fdatacomputer.computeFInter(MeusuringMetricsNewFdata.DFrequnencyMatrix, 
				new ArrayList<MyClass>(cluster1), MeusuringMetricsNewFdata.nbrAcessDataApp);
			maxCohesion = getMax(maxCohesion, valueCohision);
			
			minCohesion = getMin(minCohesion, valueCohision);
			
			maxInternalCoupling= getMax(maxInternalCoupling, valueInternlCoupling);
				
			minInternalCoupling= getMin(minInternalCoupling, valueInternlCoupling);
			
			maxExternalCoupling= getMax(maxExternalCoupling, valueExternalCoupling);
			
			minExternalCoupling= getMin(minExternalCoupling, valueExternalCoupling);
			
			maxFIntra= getMax(maxFIntra, valueFIntra);
			
			minFIntra= getMin(minFIntra, valueFIntra);
			
			maxFInter= getMax(maxFInter, valueFInter);
			
			minFInter= getMin(minFIntra, valueFInter);
			//System.out.println(valueFIntra+" "+valueFInter);
			}
			}
			//	System.out.println(" Max FIntra: "+maxFIntra+" Min FIntra: "+minFIntra);
			//	System.out.println(" Max FInter: "+maxFInter+" Min FInter: "+minFInter);
			//System.out.println("//////////////////////////////////////////////");
			FDataComputer.setMaxesMins(maxFIntra,minFIntra,maxFInter,minFInter);
			MeasuringMetrics.setMaxesMins(maxInternalCoupling, minInternalCoupling, maxCohesion, minCohesion, maxExternalCoupling, minExternalCoupling);
			return nombre;
			}
			
		

		//Return the minimum
		private static double getMin(double minCohesion, double value) {
			if (value < minCohesion) {
				minCohesion = value;
			}
			return minCohesion;
		}

		// Return the maximum
		private static double getMax(double maxCohesion, double value) {
			if (value > maxCohesion) {
				maxCohesion = value;
			}
			return maxCohesion;
		}
		
		// Initialize maxes and mins used to normalize values when partitionning 
		//a dendrogramme
		public static void initializeMaxesMines( List<ArrayList<MyClass>> fatherAndSons) {
			double maxCohesion;
			double minCohesion;
			double maxInternalCoupling;
			double minInternalCoupling;
			double maxExternalCoupling;
			double minExternalCoupling;
			double maxFIntra;
			double minFIntra;
			double maxFInter;
			double minFInter;
			
			maxCohesion=  MeasuringMetrics.measureCohision(fatherAndSons.get(0));
			minCohesion=maxCohesion;
			maxInternalCoupling= MeasuringMetrics.measureInternalCoupling1(fatherAndSons.get(0));
			minInternalCoupling= maxInternalCoupling;
			maxExternalCoupling=MeasuringMetrics.measureExternalCoupling1(fatherAndSons.get(0));
			minExternalCoupling=maxExternalCoupling;
			FDataComputer  fdatacomputer= new FDataComputer();
			maxFIntra=fdatacomputer.computeFIntra(MeusuringMetricsNewFdata.DFrequnencyMatrix, 
			new ArrayList<MyClass>(fatherAndSons.get(0)), MeusuringMetricsNewFdata.nbrAcessDataApp);
			minFIntra=maxFIntra;
			maxFInter=fdatacomputer.computeFInter(MeusuringMetricsNewFdata.DFrequnencyMatrix, 
			new ArrayList<MyClass>(fatherAndSons.get(0)), MeusuringMetricsNewFdata.nbrAcessDataApp);
			minFInter=maxFInter;
			double valueCohision;
			double valueInternlCoupling;
			double valueExternalCoupling;
			double valueFInter, valueFIntra;
			
			for (int i=1; i<fatherAndSons.size(); i++){
			
				valueCohision =  MeasuringMetrics.measureCohision(fatherAndSons.get(i));
				valueInternlCoupling= MeasuringMetrics.measureInternalCoupling1(fatherAndSons.get(i));
				valueExternalCoupling= MeasuringMetrics.measureExternalCoupling1(fatherAndSons.get(i));	
				valueFIntra= fdatacomputer.computeFIntra(MeusuringMetricsNewFdata.DFrequnencyMatrix, 
				new ArrayList<MyClass>(fatherAndSons.get(i)), MeusuringMetricsNewFdata.nbrAcessDataApp);
				valueFInter=fdatacomputer.computeFInter(MeusuringMetricsNewFdata.DFrequnencyMatrix, 
				new ArrayList<MyClass>(fatherAndSons.get(i)), MeusuringMetricsNewFdata.nbrAcessDataApp);
				maxCohesion = getMax(maxCohesion, valueCohision);
				
				minCohesion = getMin(minCohesion, valueCohision);
				
				maxInternalCoupling= getMax(maxInternalCoupling, valueInternlCoupling);
				
				minInternalCoupling= getMin(minInternalCoupling, valueInternlCoupling);
				
				maxExternalCoupling= getMax(maxExternalCoupling, valueExternalCoupling);
				
				minExternalCoupling= getMin(minExternalCoupling, valueExternalCoupling);
				
				maxFIntra= getMax(maxFIntra, valueFIntra);
				
				minFIntra= getMin(minFIntra, valueFIntra);
				
				maxFInter= getMax(maxFInter, valueFInter);
				
				minFInter= getMin(minFInter, valueFInter);
				
				//		System.out.println(valueFIntra+" "+valueFInter);
			}
			
			//	System.out.println(" Max FIntra: "+maxFIntra+" Min FIntra: "+minFIntra);
			//	System.out.println(" Max FInter: "+maxFInter+" Min FInter: "+minFInter);
			MeasuringMetrics.setMaxesMins(maxInternalCoupling, minInternalCoupling, maxCohesion, minCohesion, maxExternalCoupling, minExternalCoupling);
			FDataComputer.setMaxesMins(maxFIntra,minFIntra,maxFInter,minFInter);
		}
		
		public static void initialiseMaxMin(Set<Set<MyClass>> clusters) {
			double maxCohesion;
			double minCohesion;
			double maxInternalCoupling;
			double minInternalCoupling;
			double maxExternalCoupling;
			double minExternalCoupling;
			double maxFIntra;
			double minFIntra;
			double maxFInter;
			double minFInter;
			
			Iterator iter = clusters.iterator();

			Set<MyClass> setcluster = (Set<MyClass>) iter.next();
		
			List<MyClass> cluster= new ArrayList<MyClass>(setcluster);
			maxCohesion=  MeasuringMetrics.measureCohision((List<MyClass>) cluster);
			minCohesion=maxCohesion;
			maxInternalCoupling= MeasuringMetrics.measureInternalCoupling1(cluster);
			minInternalCoupling= maxInternalCoupling;
			maxExternalCoupling=MeasuringMetrics.measureExternalCoupling1(cluster);
			minExternalCoupling=maxExternalCoupling;
			FDataComputer  fdatacomputer= new FDataComputer();
			maxFIntra=fdatacomputer.computeFIntra(MeusuringMetricsNewFdata.DFrequnencyMatrix, 
					cluster, MeusuringMetricsNewFdata.nbrAcessDataApp);
			minFIntra=maxFIntra;
			maxFInter=fdatacomputer.computeFInter(MeusuringMetricsNewFdata.DFrequnencyMatrix, 
					cluster, MeusuringMetricsNewFdata.nbrAcessDataApp);
			minFInter=maxFInter;

			double valueCohision, valueInternlCoupling, valueExternalCoupling,
						valueFInter, valueFIntra;
			//System.out.println("//////////////////////////////////////////////");
			//System.out.println(maxFIntra+" "+minFInter);
			for (Set<MyClass> cluster1: clusters) {
					valueCohision =  MeasuringMetrics.measureCohision(new ArrayList<MyClass>(cluster1));
					valueInternlCoupling= MeasuringMetrics.measureInternalCoupling1(new ArrayList<MyClass>(cluster1));
					valueExternalCoupling= MeasuringMetrics.measureExternalCoupling1(new ArrayList<MyClass>(cluster1));
					valueFIntra= fdatacomputer.computeFIntra(MeusuringMetricsNewFdata.DFrequnencyMatrix, 
							new ArrayList<MyClass>(cluster1), MeusuringMetricsNewFdata.nbrAcessDataApp);
					valueFInter=fdatacomputer.computeFInter(MeusuringMetricsNewFdata.DFrequnencyMatrix, 
							new ArrayList<MyClass>(cluster1), MeusuringMetricsNewFdata.nbrAcessDataApp);
					maxCohesion = getMax(maxCohesion, valueCohision);
					
					minCohesion = getMin(minCohesion, valueCohision);
					
					maxInternalCoupling= getMax(maxInternalCoupling, valueInternlCoupling);
							
					minInternalCoupling= getMin(minInternalCoupling, valueInternlCoupling);
					
					maxExternalCoupling= getMax(maxExternalCoupling, valueExternalCoupling);
					
					minExternalCoupling= getMin(minExternalCoupling, valueExternalCoupling);
					
					maxFIntra= getMax(maxFIntra, valueFIntra);
					
					minFIntra= getMin(minFIntra, valueFIntra);
					
					maxFInter= getMax(maxFInter, valueFInter);
					
					minFInter= getMin(minFIntra, valueFInter);
			}

			//System.out.println("//////////////////////////////////////////////");
			FDataComputer.setMaxesMins(maxFIntra,minFIntra,maxFInter,minFInter);
			MeasuringMetrics.setMaxesMins(maxInternalCoupling, minInternalCoupling, maxCohesion, minCohesion, maxExternalCoupling, minExternalCoupling);	
		}

}	
