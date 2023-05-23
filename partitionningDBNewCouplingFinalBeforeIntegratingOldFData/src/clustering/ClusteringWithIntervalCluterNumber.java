/**
 * 
 */
package clustering;

import java.util.List;
import java.util.Random;
import java.util.Set;

import models.MyClass;

/**
 * @author anfel
 *
 */
public class ClusteringWithIntervalCluterNumber {
	
	public static Set<Set<MyClass>> cluster(int minClusterNumber, int maxClusterNumber,List<MyClass> classes,String saveInFile){
		
		int clusterNumber= getRandomWhithinInterval(minClusterNumber,maxClusterNumber);
		Set<Set<MyClass>> clusters = ClusteringWithCluterNumber.cluster(clusterNumber, classes,saveInFile);
		
		return clusters;
	}
	
	private static int getRandomWhithinInterval(int minClusterNumber,
			int maxClusterNumber) {
		Random rand = new Random();
		int number= rand.nextInt((maxClusterNumber+1) - minClusterNumber) + minClusterNumber;
		System.out.println(number);
		return number;
	}


}
