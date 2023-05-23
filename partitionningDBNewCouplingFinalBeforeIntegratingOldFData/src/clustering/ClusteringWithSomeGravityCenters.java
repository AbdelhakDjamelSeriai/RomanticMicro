package clustering;

import identificationProcess.MeusuringMetricsNewFdata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import models.MyClass;

public class ClusteringWithSomeGravityCenters {
	
	static Set<Node> allnoeuds = new TreeSet<Node>();
	static Node oldPereNode;
	
	public static List<Set<MyClass>> cluster(Set<MyClass> gravityCenters, List<MyClass> classes, String saveInFile){
		

	 	List<Set<MyClass>> clusters=ClusteringWithGravityCenters.initializeClusters(gravityCenters);	
	 	
	 	List<MyClass> restClasses= ClusteringWithGravityCenters.getRestingClasses(gravityCenters,classes);
	 	Set<MyClass> restClassesSet = new TreeSet<MyClass>(restClasses);
	 	
	 	clusters.add(ClusteringWithNbCustersAndSomeCenters.addClusterToBePartitioned( restClassesSet));
	 	
	 	for(MyClass cl: restClassesSet){
	 		
	 		 List<Cluster> qulityValuesNewClusters= new ArrayList <Cluster>();
	 	//	 List<Set<MyClass>> tempClusters=createTempClusters(cl,clusters);
		//	 MaxMinInitiliser.initialiseMaxMin(new HashSet<Set<MyClass>>(tempClusters));
			 for(Set<MyClass> cluster: clusters){
				 Set<MyClass> tempcluster= new TreeSet<MyClass>(cluster);
				 tempcluster.add(cl);
				 double quality = MeusuringMetricsNewFdata.FFMicro(new ArrayList<MyClass>(tempcluster));
				 qulityValuesNewClusters.add(new Cluster(cluster,quality));
			 }
			 
			 Set<MyClass> bestCluster= ClusteringWithGravityCenters.findBestCluster(qulityValuesNewClusters);
			 
			 int indexBestCluster= clusters.indexOf(bestCluster);
			 
			 if (indexBestCluster== clusters.size()-1)
				 ClusteringWithNbCustersAndSomeCenters.addNoudsDedogram(cl);
			 clusters.get(indexBestCluster).add(cl);
	 	}
	 	
		BinaryTree binaryTree = ClusteringUtils.clustering(clusters.get(clusters.size()-1));
		clusters.remove(clusters.size()-1);
		Set<Set<MyClass>> subClusters = ClusteringUtils.parcoursDendrogramme(binaryTree,0.5);
		
	 	clusters.addAll(subClusters);
	 	
	 	ClusteringUtils.printClusters(new HashSet<Set<MyClass>>(clusters), saveInFile);
		System.out.println("***** The clustering is done.");
		
		return clusters;
	}
	
/*	private static List<Set<MyClass>> createTempClusters(MyClass cl,
			List<Set<MyClass>> clusters) {
		List<Set<MyClass>> tempClusters= new ArrayList<Set<MyClass>>();
		for(Set<MyClass> cluster: clusters){
			 
			 Set<MyClass> tempcluster= new TreeSet<MyClass>();
			 tempcluster.addAll(cluster);
			 tempcluster.add(cl);
			 tempClusters.add(tempcluster);
		 }
		 
		return tempClusters;
	}*/

}
