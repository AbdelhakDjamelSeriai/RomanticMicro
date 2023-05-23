package clustering;

import identificationProcess.MeusuringMetricsNewFdata;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import models.MyClass;

public class ClusteringWithGravityCenters {
	
	public static List<Set<MyClass>> clustering(Set<MyClass> gravityCenters, List<MyClass> classes, String saveInFile){
	
		 List<Set<MyClass>> clusters= new ArrayList<Set<MyClass>>();
		 
		 clusters=initializeClusters(gravityCenters);
		 List<MyClass> restClasses= getRestingClasses(gravityCenters,classes);
		 Set<MyClass> restClassesSet = new TreeSet<MyClass>(restClasses);
		
		 for(MyClass cl: restClassesSet ){
			
			 List<Cluster> qulityValuesNewClusters= new ArrayList <Cluster>();
			// List<Set<MyClass>> tempClusters=createTempClusters(cl,clusters);
			// MaxMinInitiliser.initialiseMaxMin(new HashSet<Set<MyClass>>(tempClusters));
			 for(Set<MyClass> cluster: clusters){
				 
				 Set<MyClass> tempcluster= new TreeSet<MyClass>(cluster);
				 tempcluster.add(cl);
				 double quality = MeusuringMetricsNewFdata.FFMicro(new ArrayList<MyClass>(tempcluster));
				 qulityValuesNewClusters.add(new Cluster(cluster,quality));
			 }
			 
			 Set<MyClass> bestCluster= findBestCluster(qulityValuesNewClusters);
			 clusters.get(clusters.indexOf(bestCluster)).add(cl);
		 }
		 
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

	public static Set<MyClass> findBestCluster(List<Cluster> qulityValuesperNewCluster) {
		
		Set<MyClass> bestCluster= new TreeSet<MyClass>();
		if (!qulityValuesperNewCluster.isEmpty()){
			
			double max= qulityValuesperNewCluster.get(0).getQuality();
			bestCluster=qulityValuesperNewCluster.get(0).getClasses();
			for(Cluster cluster: qulityValuesperNewCluster){
				if(cluster.getQuality()>max){
					max= cluster.getQuality();
					bestCluster= cluster.getClasses();
				}		
			}	
		}
		
		return bestCluster;
	}

	public static List<MyClass> getRestingClasses(Set<MyClass> gravityCanters, List<MyClass> classes) {
		
		List<MyClass> restClasses= new ArrayList<MyClass>();
		
		for (MyClass cl: classes){
		     restClasses.add(cl);
		}
		for (MyClass cl: gravityCanters){
			restClasses.remove(cl);
		}
		return restClasses;
	}

	public static List<Set<MyClass>> initializeClusters(Set<MyClass> gravityCenters) {
		
		List<Set<MyClass>> clusters= new ArrayList<Set<MyClass>>();
		for (MyClass cl: gravityCenters){
			Set<MyClass> cluster= new TreeSet<MyClass>();
			cluster.add(cl);
			clusters.add(cluster);
		}
		return clusters;
	}

}
