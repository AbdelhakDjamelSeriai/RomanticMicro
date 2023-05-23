package clustering;

import identificationProcess.FDataComputer;
import identificationProcess.MeasuringMetrics;
import identificationProcess.MeusuringMetricsNewFdata;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import org.hibernate.dialect.function.StandardAnsiSqlAggregationFunctions.MaxFunction;

import models.MyClass;



/**
 * @author Anas Shatnawi anasshatnawi@gmail.com
 */
public class ClusteringUtils {
	static Set<Node> allnoeuds = new TreeSet<Node>();
	static List<Node> peres= new ArrayList<Node>();
	static double maxFSem;
	static double maxFData;
	static double maxF;
	static double minFSem;
	static double minFData;
	static double minF;
	static double avgFSem;
	static double avgFData;
	static double avgF;
	private static double normalisedMaxFSem;
	private static double  normalisedMaxFData;
	
	public static BinaryTree clustering(Set<MyClass> classes) {
		System.err
				.println("\t1building binary tree, it may take long time depending on your data size...");
		Set<Node> noeuds = new TreeSet<Node>();
		int taille = classes.size();
		double max;
		BinaryTree b = null;
		for (MyClass c : classes) {
			noeuds.add(new Leaf(c));
			allnoeuds.add(new Leaf(c));
		}
		System.err.println("\t\tleaf nodes are added");
		System.err.println("\t\tbinary tree agromalitive aggregation starting");
		int nombre = 1;
		try {
			PrintStream out=  new PrintStream(new FileOutputStream("/home/anfel/Documents/FinalResults/dendro.txt"));
		do {
			Node[] noeudsArray = noeuds.toArray(new Node[0]);
			
			Node n1;
			Node n2;
			Set<MyClass> cluster;
			double value;
	//		nombre = MaxMinInitiliser.initialiseMaxMin(nombre, noeudsArray);
				
			n1 = noeudsArray[0];
			n2 = noeudsArray[1];
			cluster = new TreeSet<MyClass>();
			cluster.addAll(n1.getClasses());
			cluster.addAll(n2.getClasses());
			max = MeusuringMetricsNewFdata.FFMicro(new ArrayList<MyClass>(cluster));
			nombre++;
			
			for (int i = 0; i < noeudsArray.length; i++) {
				for (int j = i + 1; j < noeudsArray.length; j++) {
					Set<MyClass> cluster1 = new TreeSet<MyClass>();
					cluster1.addAll(noeudsArray[i].getClasses());
					cluster1.addAll(noeudsArray[j].getClasses());
					value =  MeusuringMetricsNewFdata.FFMicro(new ArrayList<MyClass>(cluster1));
//					nombre++;
					if (value > max) {
						n1 = noeudsArray[i];
						n2 = noeudsArray[j];
						max = value;
					}
				}
			}
			//System.err.println("level : "+nombre);
			b = new BinaryTree();
			out.println("================ n1");
			for (MyClass cl: n1.getClasses())
				out.println(cl.getName());
			out.println("================ n2");
			for (MyClass cl: n2.getClasses())
				out.println(cl.getName());
			
			b.setNode1(n1);
			b.setNode2(n2);
			noeuds.remove(n1);
			noeuds.remove(n2);
			noeuds.add(b);
			allnoeuds.add(b);
		} while (b.numberOfLeaves() != taille);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		System.err.println("\t\tbinary tree was built");
		return b;
	}



	

	//Partition the dendrograme into a set of clusters and save them in a file
	public static Set<Set<MyClass>> parcoursDendrogramme(BinaryTree dendrogramme,
			double t) {
		Set<Set<MyClass>> result = new HashSet<Set<MyClass>>();
		
		System.err
				.println("\textracting the best cluster form the binaru tree...");
		
		Stack<Node> pile = new Stack<Node>();
		pile.push(dendrogramme);
		while (!pile.isEmpty()) {
			Node pere = pile.pop();
			peres.add(pere);
			Set<MyClass> pereCluster = pere.getClasses();
			if (pere instanceof BinaryTree) {
				Set<MyClass> fils1Cluster = (((BinaryTree) pere).getNode1()
						.getClasses());
				Set<MyClass> fils2Cluster = (((BinaryTree) pere).getNode2()
						.getClasses());
				List <ArrayList<MyClass>> fatherAndSons= new ArrayList <ArrayList<MyClass>>();
				
				fatherAndSons.add(new ArrayList<MyClass>(fils1Cluster));
				fatherAndSons.add(new ArrayList<MyClass>(fils2Cluster));
				fatherAndSons.add(new ArrayList<MyClass>(pereCluster));
			//	MaxMinInitiliser.initializeMaxesMines(fatherAndSons);
				
				double valueFils1 = MeusuringMetricsNewFdata.FFMicro(new ArrayList<MyClass>(fils1Cluster));
				double valueFils2 = MeusuringMetricsNewFdata.FFMicro(new ArrayList<MyClass>(fils2Cluster));
				double valuePere =MeusuringMetricsNewFdata.FFMicro(new ArrayList<MyClass>(pereCluster));
				if (valuePere > (valueFils1 + valueFils2) * t) {
					result.add(pereCluster);
					//printPere(pereCluster, valuePere,saveInFile);
				} else {
					pile.add(((BinaryTree) pere).getNode1());
					pile.add(((BinaryTree) pere).getNode2());
				}
			} else {
			//  double valuePere =MeusuringMetricsNewFdata.FFMicro(new ArrayList<MyClass>(pereCluster));
				result.add(pereCluster);
			//	printPere(pereCluster, valuePere,saveInFile);
			}
		}
		return result;

	}

	// Save a cluster in txt file
	private static void printPere(Set<MyClass> cluster, double valuePere, String saveInFile) {
		
		FileWriter fw;
		try {
			fw = new FileWriter(saveInFile, true);
		
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw);
			out.println();
			int i = 1;
		
			out.println("====================================================================================");
			out.println(" Cluster "+i+"("+cluster.size()+" classes)");
			out.println("   F: "+valuePere);
			out.println("    *FSem: "+MeasuringMetrics.lastComputedFSem);
			out.println("     -Cohesion: "+MeasuringMetrics.lastComputedCohesion);
			out.println("     -Internal coupling: "+MeasuringMetrics.lastComputedInternalCoupling);
			out.println("     -External coupling: "+MeasuringMetrics.lastComputedExternalCoupling);
			out.println("    *FData: "+FDataComputer.lastComputedFData);
			out.println("     -FIntra: "+FDataComputer.lastComputedIntra);
			out.println("     -FInter: "+FDataComputer.lastComputedInter);
			for (MyClass clazz: cluster) {
				out.println("  " + clazz.getName());
			}
			out.println("====================================================================================");
			i++;
			out.println();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	


	public static void parcoursDendro(BinaryTree dendro, String s) {
		System.out.println(s + "pere" + dendro.getClasses());
		if (dendro.getNode1() instanceof BinaryTree)
			parcoursDendro((BinaryTree) dendro.getNode1(), s + "  ");
		else
			System.out.println(s + "  fils1"
					+ ((Leaf) dendro.getNode1()).getClasses());
		if (dendro.getNode2() instanceof BinaryTree)
			parcoursDendro((BinaryTree) dendro.getNode2(), s + "  ");
		else
			System.out.println(s + "  fils2"
					+ ((Leaf) dendro.getNode2()).getClasses());
	}
	

	public static void printClusters(Set<Set<MyClass>> clusters,
			String saveInFile) {
		DecimalFormat df = new DecimalFormat("#.##");
		int i=1;
		try {
			setMaxMinAvgFSemFData(new ArrayList<Set<MyClass>>(clusters));
			PrintStream out=  new PrintStream(new FileOutputStream(saveInFile));
			out.println("Nombre of clusters: "+clusters.size()+" (FSem Max: "+df.format(normalisedMaxFSem)+" Min: "+df.format(minFSem)+" Avg: "+ df.format(avgFSem)+")"+
			" (FData  Max: "+df.format(normalisedMaxFData)+" Min: "+df.format(minFData)+" Avg: "+ df.format(avgFData)+")"+
			"(F Max: "+df.format(maxF)+" Min: "+df.format(minF)+" Avg: "+ df.format(avgF)+")");
			
			if (clusters.size()==1){
				printOnlyOneCluster(clusters,out);
			
			}else{
				
			
				printMultipleMicroservices(clusters, i, out);
			
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private static void printMultipleMicroservices(Set<Set<MyClass>> clusters,
			int i, PrintStream out) {
		FDataComputer  fdatacomputer= new FDataComputer();
		out.println();
		setMaxMinAvgFSemFData(new ArrayList<Set<MyClass>>(clusters));
		double FSem, FData,F;
		for(Set<MyClass> cluster: clusters){
			 out.println("====================================================================================");
			 out.println("Cluster "+i+": ");
			 FSem=MeasuringMetrics.measureFFSem(new ArrayList<MyClass>(cluster));
			 FData= fdatacomputer.computeFData(MeusuringMetricsNewFdata.DFrequnencyMatrix, 
						new ArrayList<MyClass>(cluster), MeusuringMetricsNewFdata.nbrAcessDataApp);
			 if(maxFSem>1){
				 FSem= FSem/maxFSem;	
			 }
			 if(maxFData>1){
					FData=FData/maxFData;
			}
			 F=(MeusuringMetricsNewFdata.lambda*FSem+MeusuringMetricsNewFdata.beta*FData)/
					 (MeusuringMetricsNewFdata.lambda+MeusuringMetricsNewFdata.beta);
			
			 out.println("   F: "+ F);
				out.println("    *FSem: "+FSem);
				out.println("     -Cohesion: "+MeasuringMetrics.measureCohision(new ArrayList<MyClass>(cluster)));
				out.println("     -Internal coupling: "+MeasuringMetrics.measureInternalCoupling1(new ArrayList<MyClass>(cluster)));
				out.println("     -External coupling: "+MeasuringMetrics.measureExternalCoupling1(new ArrayList<MyClass>(cluster)));
				out.println("    *FData: "+FData);
				out.println("     -FIntra: "+fdatacomputer.computeFIntra(MeusuringMetricsNewFdata.DFrequnencyMatrix, 
						new ArrayList<MyClass>(cluster), MeusuringMetricsNewFdata.nbrAcessDataApp));
				out.println("     -FInter: "+fdatacomputer.computeFInter(MeusuringMetricsNewFdata.DFrequnencyMatrix, 
						new ArrayList<MyClass>(cluster), MeusuringMetricsNewFdata.nbrAcessDataApp));
			out.println("    Classes: ");
			 for (MyClass cl: cluster)
				out.println("      		 "+cl);
			 out.println("====================================================================================");
			 out.println();
			 i++;
		}
		
	}

	private static void setMaxMinAvgFSemFData(List<Set<MyClass>> clusters) {
		maxFSem=MeasuringMetrics.measureFFSem(new ArrayList<MyClass>(clusters.get(0)));
		FDataComputer  fdatacomputer= new FDataComputer();
		maxFData=fdatacomputer.computeFData(MeusuringMetricsNewFdata.DFrequnencyMatrix, 
				new ArrayList<MyClass>(clusters.get(0)), MeusuringMetricsNewFdata.nbrAcessDataApp);
		minFSem=maxFSem;
		minFData=maxFData;
		double currentFSem;
		double currentFData;
		
		for (Set<MyClass> cluster: clusters){
			currentFSem= MeasuringMetrics.measureFFSem(new ArrayList<MyClass>(cluster));
			if(currentFSem>maxFSem){
				maxFSem=currentFSem;
			}
			if(currentFSem<minFSem){
				minFSem=currentFSem;
			}
			
			currentFData= fdatacomputer.computeFData(MeusuringMetricsNewFdata.DFrequnencyMatrix, 
					new ArrayList<MyClass>(cluster), MeusuringMetricsNewFdata.nbrAcessDataApp);
			if(currentFData>maxFData){
				maxFData=currentFData;
			}
			if(currentFData<minFData){
				minFData=currentFData;
			}
		}
		
		if(maxFSem>1){
			if(maxFData>1){
				computeNormalizedMinMaxAvg(clusters,maxFSem,maxFData);
				System.err.println("Normalising FSem and FData...");
			}
			else{
				System.err.println("Normalising FSem...");
				computeNormalizedMinMaxAvg(clusters,maxFSem,1);
			}
		}else{
			if(maxFData>1){
				System.err.println("Normalising FData...");
				computeNormalizedMinMaxAvg(clusters,1,maxFData);
			}
			else{
				computeNormalizedMinMaxAvg(clusters,1,1);
				System.err.println("No normalisation...");
			}
		}
		
	}


	private static void computeNormalizedMinMaxAvg(List<Set<MyClass>> clusters, double maxf, double maxd) {
		double currentFSem;
		double currentFData;
		double sommeFSem=0;
		double sommeFData=0;
		double sommeF=0;
		double F=0;
		initialiseMinMaxF(clusters.get(0),maxf,maxd);
		FDataComputer  fdatacomputer= new FDataComputer();
		for (Set<MyClass> cluster: clusters){
			currentFSem= MeasuringMetrics.measureFFSem(new ArrayList<MyClass>(cluster))/maxf;
			sommeFSem=sommeFSem+currentFSem;
			
			currentFData= fdatacomputer.computeFData(MeusuringMetricsNewFdata.DFrequnencyMatrix, 
					new ArrayList<MyClass>(cluster), MeusuringMetricsNewFdata.nbrAcessDataApp)/maxd;
			sommeFData=sommeFData+currentFData;
			F=(MeusuringMetricsNewFdata.lambda*currentFSem+MeusuringMetricsNewFdata.beta*currentFData)/
					 (MeusuringMetricsNewFdata.lambda+MeusuringMetricsNewFdata.beta);
			if(F>maxF){
				maxF=F;
			}
			if(F<minF){
				minF=F;
			}
			sommeF=sommeF+F;
		}
		normalisedMaxFSem=maxFSem/maxf;
		normalisedMaxFData=maxFData/maxd;
		minFSem=minFSem/maxf;
		minFData=minFData/maxd;
		avgF=sommeF/clusters.size();
		avgFData=sommeFData/clusters.size();
		avgFSem=sommeFSem/clusters.size();
	}


	private static void initialiseMinMaxF(Set<MyClass> cluster, double maxFSemInit, double maxFDataInit) {
		double FSem= MeasuringMetrics.measureFFSem(new ArrayList<MyClass>(cluster))/ maxFSemInit;
		FDataComputer fdatacomputer= new FDataComputer();
		double FData= fdatacomputer.computeFData(MeusuringMetricsNewFdata.DFrequnencyMatrix, 
				new ArrayList<MyClass>(cluster), MeusuringMetricsNewFdata.nbrAcessDataApp)/maxFDataInit;
		maxF=(MeusuringMetricsNewFdata.lambda*FSem+MeusuringMetricsNewFdata.beta*FData)/
				 (MeusuringMetricsNewFdata.lambda+MeusuringMetricsNewFdata.beta);
		minF=maxF;
	}



	private static void printOnlyOneCluster(Set<Set<MyClass>> clusters, PrintStream out) {
		 
		for(Set<MyClass> cluster: clusters){
			out.println();
			out.println("====================================================================================");
			out.println("Cluster 1");
			out.println("     -Cohesion: 1");
			out.println("     -Internal coupling: 1");
			out.println("     -External coupling: 0");
			out.println("     -FIntra: 1");
			out.println("     -FInter: 0");
			out.println("     - Classes: ");
			for (MyClass cl: cluster)
				out.println("       	"+cl);
		 		out.println("====================================================================================");
		 		out.println();
		}
	}





	public static void printClustersWithDBClasses(
			Set<Set<MyClass>> clusters, String saveInFileWithDBClasses) {
		
		try {
			
			PrintStream out=  new PrintStream(new FileOutputStream(saveInFileWithDBClasses));
			out.println("Nombre of clusters: "+clusters.size());
			out.println();
			int i=1;
			for(Set<MyClass> cluster: clusters){
				 out.println("====================================================================================");
				 out.println("Cluster "+i+": ");
				 
				 out.println();
					out.println();
					out.println();
					out.println();
					out.println();
					out.println();
					out.println();
					out.println();
				out.println();
				 for (MyClass cl: cluster)
					out.println("      		 "+cl);
				 out.println("====================================================================================");
				 out.println();
				 i++;
			}
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	public static void printClusters(Set<Set<MyClass>> clusters, PrintStream out) {
		int i = 1;
		// Set<Component> components = new HashSet<Component>();
		for (Set<MyClass> cluster : clusters) {
			out.println("");
			out.println("Cluster " + i + "(" + cluster.size() + " MyClasses).");
			
			for (MyClass clazz : cluster) {
				//if(clazz.getName().equals(clazz.getFullName()))//ignor java MyClass existing on API
				out.println("" + clazz.getName());
			}
			i++;
			// components.add(new
			// Component(ComponentNaming.componentName(cluster), cluster));
		}
		// for(Component c:components){
		// System.err.println(c.toString());
		// }
	}
	
}
