package geneticAlgo;

import identificationProcess.FunctionalIndependanceMeasurer;
import identificationProcess.InterfaceRecoverer;

import java.awt.geom.Point2D;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import partitionningdb.handlers.PartitionningDB;
import clustering.ClusteringUtils;
import models.Microservice;
import models.MyClass;

public class GeneticIdentification {
	
	public static final int iteration = 10; 
	public static int nbObjectives = 2;
	public static ArrayList<Point2D> paretoXY = new ArrayList<Point2D>();
    public static Double [][] avgQualityTopTen = new Double [iteration][iteration];
	public static Double [][] qualitybesti = new Double [iteration][iteration];
	public static int [][] popu = new int [iteration][iteration];
	public static Double [][] avgPareto = new Double [iteration][iteration];
	public static Double [][] bestQualityPareto = new Double [iteration][iteration];
	public static String resultsPath;
	
	public static Set<Set<MyClass>> identify(Set<MyClass> classes, List<String> dbClassesNames) {
				ArrayList<Individu> population = new ArrayList<Individu>();
				System.err.println("GENETIC ALGORITHME START");
				System.err.println("\n Step 0 Population initialization");
				//population = GABasicOps.initPopulation(binaryTree, 5);
				try {
						population.addAll(GABasicOps.initPopulation(classes, 50)) ;
						int maxGeneration = 0;
						//Deal with inconstant weight when comparing chromosomes
		//		QualityFunction.initWeights();
						
						for (int it = 0; it < iteration; it++) {
							
							System.err.println("\n========ITERATION "+it+"============");
							//observer++;
						
							/*if(maxGeneration == GABasicOps.maxGeneration){
								//Remove old Individuals age > GABasicOps.maxGeneration
								//removeOldIndiv(population);
								//Adaptation based selection
								population = GABasicOps.adaptationBasedSelection(population);
								maxGeneration = 0;
							}*/
							System.err.println("Population size "+population.size()); //maxGeneration++;
							population = GABasicOps.geneticAlgorithm(population, nbObjectives, Individu.mutRate, it);
							if (population.isEmpty())
								break;
							GABasicOps.cptbesti++;
						}
						Iterator<Individu> itr;
						System.err.println("\nWriting non dominated solutions found during the search .....");
						//GABasicOps.kungPareto(new ArrayList<Individu>(GABasicOps.paretoOptimal))
						if(GABasicOps.kungPareto(new ArrayList<Individu>(GABasicOps.paretoOptimal)).size() > 0)
							itr = GABasicOps.sortByMode(new ArrayList<Individu>(GABasicOps.kungPareto(new ArrayList<Individu>(GABasicOps.paretoOptimal)))).iterator();
						else itr = GABasicOps.sortByMode(new ArrayList<Individu>(GABasicOps.paretoOptimal)).iterator(); int p = 0;
						//ranking by quality
						Individu prem = itr.next();
						paretoXY.add(new Point2D.Double(prem.getFFonc(),prem.getFData()));
						System.out.println("besti "+prem.getId()+" : number of clusters "+prem.getChromosomes().size()+" cpl "+prem.getCoupling()+" lcc "+prem.getLcc()+" extCpl "+prem.getExternalCoupling());
						ClusteringUtils.printClusters(prem.getChromosomes(), new PrintStream(new FileOutputStream(resultsPath+"/paretoBestQuality_chr_"+prem.getChromosomes().size()+"_fonc_"+prem.getFFonc()+"_data_"+prem.getFData()+".txt")));
						String saveInFileWithDBClasses=  "/home/anfel/Documents/InchallehFinalResults/GeneticAlgo/DB/clutsers100.txt";
						PartitionningDB part= new PartitionningDB();
				//		part.addDBClasses(prem.getChromosomes(),DBClasses,saveInFileWithDBClasses);
						int i=0;
						while(itr.hasNext()){
							Individu e = itr.next();
							paretoXY.add(new Point2D.Double(e.getFFonc(),e.getFData()));
							System.out.println(e.getId()+" : number of clusters "+e.getChromosomes().size()+" fonc "+e.getFFonc()+" data "+e.getFData());
							ClusteringUtils.printClusters(e.getChromosomes(), new PrintStream(new FileOutputStream(resultsPath+"/pareto"+p+"_chr_"+e.getChromosomes().size()+"_fonc_"+e.getFFonc()+"_data_"+e.getFData()+".txt"))); p++;
					//		saveInFileWithDBClasses=  "/home/selmadji/Documents/InchallehFinalResults/GeneticAlgo/DB/clutsers"+i+".txt";
					//		part.addDBClasses(e.getChromosomes(),DBClasses,saveInFileWithDBClasses);
							i++;
							//System.out.println(e.getId()+" : number of clusters "+e.getChromosomes().size()+" cpl "+e.getCoupling()+" lcc "+e.getLcc()+" extCpl "+e.getExternalCoupling());
							//ClusteringUtils.printClusters(e.getChromosomes(), new PrintStream(new FileOutputStream(resultsPath+"/pareto"+p+"_chr_"+e.getChromosomes().size()+"_cpl_"+e.getCoupling()+"_lcc_"+e.getLcc()+"_extcpl_"+e.getExternalCoupling()+".txt"))); p++;
				
							}
						
						
						//GABasicOps.kungPareto(new ArrayList<Individu>(GABasicOps.paretoOptimal))
						if(GABasicOps.kungPareto(new ArrayList<Individu>(GABasicOps.paretoOptimal)).size() > 0)
							itr = GABasicOps.sortByMode(new ArrayList<Individu>(GABasicOps.kungPareto(new ArrayList<Individu>(GABasicOps.paretoOptimal)))).iterator();
						else itr = GABasicOps.sortByMode(new ArrayList<Individu>(GABasicOps.paretoOptimal)).iterator();  p = 0;
						//ranking by quality
						prem = itr.next();
						paretoXY.add(new Point2D.Double(prem.getFFonc(),prem.getFData()));
						//ClusteringUtils.printClusters(prem.getChromosomes(), new PrintStream(new FileOutputStream(resultsPath+"/paretoBestQuality_chr_"+prem.getChromosomes().size()+"_fonc_"+prem.getFFonc()+"_data_"+prem.getFData()+".txt")));
						//String saveInFileWithDBClasses=  "/home/selmadji/Documents/InchallehFinalResults/GeneticAlgo/DB/clutsers.txt";
						part= new PartitionningDB();
						part.addDBClasses(prem.getChromosomes(),dbClassesNames,saveInFileWithDBClasses);
						 InterfaceRecoverer interfaceRecoverer= new InterfaceRecoverer();
							List<Microservice> microservices= interfaceRecoverer.getMicroservices(prem.getChromosomes());
							String saveInFileMicroWithInterface= "/home/anfel/Documents/InchallehFinalResults/GeneticAlgo/Interface/Interface100.txt";
							for (Microservice micro: microservices){
							//	System.out.println("****************************************************");
								//	micro.print();
								micro.printInFile(saveInFileMicroWithInterface);
								//	System.out.println("****************************************************");
								
							}
							
							FunctionalIndependanceMeasurer fim= new FunctionalIndependanceMeasurer();
							fim.measure(microservices,saveInFileMicroWithInterface);
						String manualIdentificationFile = "/home/anfel/Documents/ManualIdentification/SpringBlog.txt";
						if(manualIdentificationFile!=null)
							part.classifiyClusters(saveInFileWithDBClasses,manualIdentificationFile);
						i=0;
						while(itr.hasNext()){
							Individu e = itr.next();
							paretoXY.add(new Point2D.Double(e.getFFonc(),e.getFData()));
						//	ClusteringUtils.printClusters(e.getChromosomes(), new PrintStream(new FileOutputStream(resultsPath+"/pareto"+p+"_chr_"+e.getChromosomes().size()+"_fonc_"+e.getFFonc()+"_data_"+e.getFData()+".txt"))); p++;
							saveInFileWithDBClasses=  "/home/anfel/Documents/InchallehFinalResults/GeneticAlgo/DB/clutsers"+i+".txt";
							part.addDBClasses(e.getChromosomes(),dbClassesNames,saveInFileWithDBClasses);
							interfaceRecoverer= new InterfaceRecoverer();
							microservices= interfaceRecoverer.getMicroservices(prem.getChromosomes());
							saveInFileMicroWithInterface= "/home/anfel/Documents/InchallehFinalResults/GeneticAlgo/Interface/Interface"+i+".txt";
							for (Microservice micro: microservices){
							//	System.out.println("****************************************************");
								//	micro.print();
								micro.printInFile(saveInFileMicroWithInterface);
								//	System.out.println("****************************************************");
								
							}
							
							fim= new FunctionalIndependanceMeasurer();
							fim.measure(microservices,saveInFileMicroWithInterface);
							if(manualIdentificationFile!=null)
								part.classifiyClusters(saveInFileWithDBClasses,manualIdentificationFile);
							i++;
							//System.out.println(e.getId()+" : number of clusters "+e.getChromosomes().size()+" cpl "+e.getCoupling()+" lcc "+e.getLcc()+" extCpl "+e.getExternalCoupling());
							//ClusteringUtils.printClusters(e.getChromosomes(), new PrintStream(new FileOutputStream(resultsPath+"/pareto"+p+"_chr_"+e.getChromosomes().size()+"_cpl_"+e.getCoupling()+"_lcc_"+e.getLcc()+"_extcpl_"+e.getExternalCoupling()+".txt"))); p++;
				
							}
						return prem.getChromosomes();	
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				// output
				System.err.println("finish");
				return null;
		}

}
