package clustering;

import identificationProcess.MeusuringMetricsNewFdata;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import models.MyClass;

public class ClusteringWithNbCustersAndSomeCenters {
	
	static Set<Node> allnoeuds = new TreeSet<Node>();
	static Node oldPereNode;
	
	public static List<Set<MyClass>> cluster(int clusterNumber, Set<MyClass> gravityCenters, List<MyClass> classes,
			                                String saveInFile){
		

	 	List<Set<MyClass>> clusters=ClusteringWithGravityCenters.initializeClusters(gravityCenters);	
	 	
	 	List<MyClass> restClasses= ClusteringWithGravityCenters.getRestingClasses(gravityCenters,classes);
	 	 Set<MyClass> restClassesSet = new TreeSet<MyClass>(restClasses);
	 	
	 	clusters.add(addClusterToBePartitioned(restClassesSet));
	 	
	 //	restClassesSet = new TreeSet<MyClass>(restClasses);
	 	for(MyClass cl: restClassesSet){
	 		
	 		List<Cluster> qulityValuesNewClusters= new ArrayList <Cluster>();
	 		//MaxMinInitiliser.initialiseMaxMin(new HashSet<Set<MyClass>>(clusters));
			 for(Set<MyClass> cluster: clusters){
				 List<MyClass> tempcluster= new ArrayList<MyClass>(cluster);
				 tempcluster.add(cl);
				 double quality = MeusuringMetricsNewFdata.FFMicro(tempcluster);
				
				 qulityValuesNewClusters.add(new Cluster(cluster,quality));
			 }
			 
			 Set<MyClass> bestCluster= ClusteringWithGravityCenters.findBestCluster(qulityValuesNewClusters);
			 
			 int indexBestCluster= clusters.indexOf(bestCluster);
			 
			 if (indexBestCluster== clusters.size()-1)
				 addNoudsDedogram(cl);
			 clusters.get(indexBestCluster).add(cl);
	 	}
	 	
	 	ClusteringUtils.allnoeuds=allnoeuds;
	 	int  restingClusters= clusterNumber-gravityCenters.size();
	 	Set<Set<MyClass>> clusterToBePartitioned= new HashSet<Set<MyClass>>();
	 	clusterToBePartitioned.add(clusters.get(clusters.size()-1));
	 	
		clusters.remove(clusters.size()-1);
		
	 	clusters.addAll(ClusteringWithCluterNumber.decomposeClusters(clusterToBePartitioned, restingClusters));
	 	
	 	ClusteringUtils.printClusters(new HashSet<Set<MyClass>>(clusters), saveInFile);
		System.out.println("***** The clustering is done.");
		
		return clusters;
	}

	private static void addDendogramLeaves(List<MyClass> classList) {
		for (MyClass c : classList) {
			allnoeuds.add(new Leaf(c));
		}	
		
	}

	static void addNoudsDedogram(MyClass cll) {
		
		Leaf l=new Leaf(cll);
		allnoeuds.add(l);
		
		BinaryTree b = new BinaryTree();
		b.setNode1(oldPereNode);
		
		b.setNode2(l);
		allnoeuds.add(b);
		oldPereNode=b;
		
	}

	 static Set<MyClass> addClusterToBePartitioned(Set<MyClass> restClassesSet) {
		
		Set<MyClass> cluster= new HashSet<MyClass>();
		List<MyClass> tempList= new ArrayList<MyClass>();
		List<MyClass> restClasses= new ArrayList<MyClass>(restClassesSet);
		if(restClasses.size()>1){
			tempList.add(restClasses.get(0));
			tempList.add(restClasses.get(1));
		//	initializeMaxMin(restClasses);
			double max= MeusuringMetricsNewFdata.FFMicro(tempList);
			for(int i=0;i<restClasses.size();i++)
				for(int j=i+1; j<restClasses.size();j++){
					tempList= new ArrayList<MyClass>();
					tempList.add(restClasses.get(i));
					tempList.add(restClasses.get(j));
					double quality= MeusuringMetricsNewFdata.FFMicro(tempList);
					if (quality>max){
						cluster= new HashSet<MyClass>();
						cluster.add(restClasses.get(i));
						cluster.add(restClasses.get(j));
					}
			}
			List<MyClass> listcluster= new ArrayList<MyClass>(cluster);
			restClasses.remove(listcluster.get(0));
			restClasses.remove(listcluster.get(1));
			restClassesSet.remove(listcluster.get(0));
			restClassesSet.remove(listcluster.get(1));
			addDendogramInitialNodes(cluster);
		}
		else{
			
			cluster.add(restClasses.get(0));
			restClasses.remove(0);
		}
			
		
		return cluster;
	}

/*	private static void initializeMaxMin(List<MyClass> restClasses) {
		Set<Set<MyClass>> clusters=new HashSet<Set<MyClass>>();
		Set<MyClass> cluster;
		for(int i=0;i<restClasses.size();i++)
			for(int j=i+1; j<restClasses.size();j++){
				cluster= new HashSet<MyClass>();
				cluster.add(restClasses.get(i));
				cluster.add(restClasses.get(j));
				clusters.add(cluster);
		}
		MaxMinInitiliser.initialiseMaxMin(clusters);		
	}*/

	private static void addDendogramInitialNodes(Set<MyClass> cluster) {
		
		addDendogramLeaves(new ArrayList<MyClass>(cluster));
		
		BinaryTree b = new BinaryTree();
		List<Node> listnodes= new ArrayList<Node>(allnoeuds);
		b.setNode1(listnodes.get(0));
		b.setNode2(listnodes.get(1));
		allnoeuds.add(b);		
		oldPereNode=b;
	}
}
