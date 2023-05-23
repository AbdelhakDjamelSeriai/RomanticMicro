package clustering;

import identificationProcess.MeasuringMetrics;
import identificationProcess.MeusuringMetricsNewFdata;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import models.MyClass;

public class ClusteringWithCluterNumber {
	
	
	
	
	public static Set<Set<MyClass>> cluster(int clusterNumber, List<MyClass> classes, String saveInFile){
		
		
		Set<MyClass> setClasses = new TreeSet<MyClass>(classes);
		BinaryTree binaryTree = ClusteringUtils.clustering( setClasses);
		Set<Set<MyClass>> clusters = ClusteringUtils.parcoursDendrogramme(binaryTree,0.5);
		System.out.println("***** The clustering starts.");
		int nbIdentifiedClusters= clusters.size();
		
			if(nbIdentifiedClusters<clusterNumber){
				decomposeClusters(clusters,clusterNumber);
			}
				
			else
				if(nbIdentifiedClusters>clusterNumber){
					composeClusters(clusters,clusterNumber);
				}
			
		ClusteringUtils.printClusters(clusters,saveInFile);
		System.out.println("***** The clustering is done.");
		
		return clusters;
	}
	
	

	private static Set<Set<MyClass>> composeClusters(Set<Set<MyClass>> clusters, int clusterNumber) {
		
		int nbIdentifiedClusters= clusters.size();	
		
		while(nbIdentifiedClusters>clusterNumber){
			
			int i=0;
			List<Node> binarypere= new ArrayList <Node>();
			List<Double> qualityValues= new ArrayList <Double>();
			List<Set<MyClass>> listCluters= new ArrayList<Set<MyClass>>(clusters);
			
			while(i<listCluters.size()){
				Set<MyClass> currentCluster= listCluters.get(i);
				Node node=findPereNode1(currentCluster, clusters);
				if (node!=null)	{
					List<Set<MyClass>> templistCluters= new ArrayList<Set<MyClass>>(clusters);
				
					templistCluters= replaceSons(node, templistCluters, currentCluster);
					Double quality= getSumQualityClusters(templistCluters);
				
					binarypere.add(node);
					qualityValues.add(quality);
				}
				i++;
			}
			
			// Finding the node that produce the best results when partitioned
			Node bestNode= getBestPere(binarypere, qualityValues);
			
			// Replacing a father cluster in the list of clusters by its sons
			
			clusters.remove(((BinaryTree) bestNode).getNode1().getClasses());
			clusters.remove(((BinaryTree) bestNode).getNode2().getClasses());
			clusters.add(bestNode.getClasses());
			
			nbIdentifiedClusters--;
		}
	    return clusters;	
		
	}

	private static Node findPereNode1(Set<MyClass> currentCluster,
			Set<Set<MyClass>> clusters) {
		Node pere= null;
		boolean found=false;
		int i=0;
		while (!found && i<ClusteringUtils.peres.size()){
			Node currentNode= ClusteringUtils.peres.get(i);
			if(currentNode instanceof BinaryTree){
				Set<MyClass> classesNode1= ((BinaryTree)currentNode).getNode1().getClasses();
				Set<MyClass> classesNode2= ((BinaryTree)currentNode).getNode2().getClasses();
				if (containsTheSameClasses(currentCluster,classesNode1) && isACluster(clusters, classesNode2)){
					pere= currentNode;
					found= true;
				}
				else{
					if (containsTheSameClasses(currentCluster,classesNode2) && isACluster(clusters, classesNode1)){
						pere= currentNode;
						found= true;
					}

				}
			}
			i++;
		}
		return pere;
	}

	private static boolean isACluster(Set<Set<MyClass>> clusters,
			Set<MyClass> classesNode2) {
		boolean isCluster=false;
		int i=0;
		List<Set<MyClass>> listClusters= new ArrayList<Set<MyClass>>(clusters);
		while (!isCluster && i<listClusters.size()){
			if(containsTheSameClasses(listClusters.get(i), classesNode2)){
				isCluster=true;
			}
			i++;
		}
		return isCluster;
	}

	private static boolean containsTheSameClasses(Set<MyClass> currentCluster,
			Set<MyClass> classesNode1) {
		if (currentCluster.size()== classesNode1.size()){
			List<MyClass> classList=new ArrayList<MyClass>(currentCluster);
			boolean isTheSame=true;
			int i=0;
			while(i<classList.size() && isTheSame){
				if(!classesNode1.contains(classList.get(i)))
					isTheSame=false;
				i++;
			}
			return isTheSame;
		}else{
			return false;
		}
		
	}

	private static List<Set<MyClass>> replaceSons(Node node, List<Set<MyClass>> templistCluters, Set<MyClass> currentCluster) {
		Set<MyClass> fils1Cluster = (((BinaryTree) node).getNode1().getClasses());
		Set<MyClass> fils2Cluster = (((BinaryTree) node).getNode2().getClasses());
		templistCluters.remove(fils1Cluster);
		templistCluters.remove(fils2Cluster);
		templistCluters.add(node.getClasses());
		return templistCluters;
	}

	// Decompose a set of cluster to produce the demanded number of clusters
	public static Set<Set<MyClass>> decomposeClusters(Set<Set<MyClass>> clusters, int clusterNumber) {
		
		int nbIdentifiedClusters= clusters.size();
		
		int cannotbedeviced=0;
		while(nbIdentifiedClusters<clusterNumber){
			
			int i=0;
			
			List<Node> binarypere= new ArrayList <Node>();
			List<Double> qualityValues= new ArrayList <Double>();
			List<Set<MyClass>> listCluters= new ArrayList(clusters);
			
			while(i<listCluters.size()){
				Set<MyClass> currentCluster= listCluters.get(i);
				Node node=findCorrepondingNodeInDendrogram(currentCluster);
				List<Set<MyClass>> templistCluters= new ArrayList(clusters);
				if(node instanceof BinaryTree){
					templistCluters= replacePere(node, templistCluters, currentCluster);
					Double quality= getSumQualityClusters(templistCluters);
					
					binarypere.add(node);
					qualityValues.add(quality);
				}
				i++;
			}
			// Finding the node that produce the best results when partitioned
			if(!qualityValues.isEmpty()){
				Node bestNode= getBestPere(binarypere, qualityValues);
			
			// Replacing a father cluster in the list of clusters by its sons
				clusters.remove(bestNode.getClasses());
				clusters.add(((BinaryTree) bestNode).getNode1().getClasses());
				clusters.add(((BinaryTree) bestNode).getNode2().getClasses());
			
				nbIdentifiedClusters++;
			}else{
				cannotbedeviced++;
			}
		}
	    return clusters;	
	}

	// Find the node which produce the best results when partitioned 
	private static Node getBestPere(List<Node> binarypere,List<Double> qualityValues) {
		
		double max=  qualityValues.get(0);
		Node bestPere= binarypere.get(0);
		int i=0;
		while(i<qualityValues.size()){
			if (qualityValues.get(i)>max){
				max=  qualityValues.get(i);
				bestPere= binarypere.get(i);
			}
			i++;
		}
		return bestPere;
	}

	// Compute the sum of the quality values of a list of clusters
	private static Double getSumQualityClusters(List<Set<MyClass>> listCluters) {
		int i=0;
		double quality=0.0;
		Set<Set<MyClass>> setClasses= new HashSet<Set<MyClass>>(listCluters);
	//	MaxMinInitiliser.initialiseMaxMin(setClasses);
		while (i<listCluters.size()){
			quality+= MeusuringMetricsNewFdata.FFMicro(new ArrayList(listCluters.get(i)));
			i++;
		}
		return quality;
	}

	// Replace a father node by its sons
	private static List<Set<MyClass>> replacePere(Node pere, List<Set<MyClass>> listCluters, Set<MyClass> currentCluster) {
		Set<MyClass> fils1Cluster = (((BinaryTree) pere).getNode1().getClasses());
		Set<MyClass> fils2Cluster = (((BinaryTree) pere).getNode2().getClasses());
		listCluters.remove(currentCluster);
		listCluters.add(fils1Cluster);
		listCluters.add(fils2Cluster);
		return listCluters;
	}

	
	// Find the corresponding node in the dendogram to a given cluster
	private static Node findCorrepondingNodeInDendrogram(Set<MyClass> currentCluster) {
		
		boolean found=false;
		int i=0;
		int clustersize= currentCluster.size();
		Node correpondingNode= null;
		List<Node> list= new ArrayList(ClusteringUtils.allnoeuds);
		while(i<list.size() && !found){
			List<MyClass> pereClasses= new ArrayList(list.get(i).getClasses());
			if(pereClasses.size()== clustersize){
				
				boolean matches= true;
				int j=0;
				while(j<pereClasses.size() && matches){
					if(!currentCluster.contains(pereClasses.get(j)))
						matches=false;
					j++;
				}
				if(matches){
					found=true;
					correpondingNode= list.get(i);
				}
			}
			i++;
		}
		
		return correpondingNode;
	}

	
	public static void printPeres() {
		for (Node node: ClusteringUtils.allnoeuds){
			System.out.println("Classes: ");
			for (MyClass cl: node.getClasses())
				System.out.println("            - Class: "+cl);
			System.out.println();
			if (node instanceof BinaryTree){
				Set<MyClass> fils1Cluster = (((BinaryTree) node).getNode1()
						.getClasses());
				Set<MyClass> fils2Cluster = (((BinaryTree) node).getNode2()
						.getClasses());
				
				System.out.println("		Son 1: ");
				for (MyClass cl: fils1Cluster)
					System.out.println("       		     - Class: "+cl);
			
				System.out.println();
				System.out.println("		Son 2: ");
				for (MyClass cl: fils2Cluster)
					System.out.println("       		     - Class: "+cl);
				
			}
		}		
	}
	
	// Save the identified clusters in a file
	private static void saveResults(List<Set<MyClass>> clusters) {
		int i=1;
		
		try {
			PrintStream out=  new PrintStream(new FileOutputStream("/home/anfel/Documents/Results/ExactNumber/clusters.txt"));
			out.println("Nombre of clusters: "+clusters.size());
			out.println();
			for(Set<MyClass> cluster: clusters){
				 out.println("====================================================================================");
				 out.println("Cluster "+i+": ");
				 for (MyClass cl: cluster)
					 out.println("       - "+cl);
				     out.println("       FSem: "+MeasuringMetrics.measureFFSem(new ArrayList<MyClass>(cluster)));
				     out.println("       FMicro: "+MeusuringMetricsNewFdata.FFMicro(new ArrayList<MyClass>(cluster)));
				 out.println("====================================================================================");
				 out.println();
				 i++;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		
	}

}
