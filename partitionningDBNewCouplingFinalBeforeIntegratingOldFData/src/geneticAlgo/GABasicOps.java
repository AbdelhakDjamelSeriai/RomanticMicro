package geneticAlgo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import clustering.BinaryTree;
import clustering.ClusteringUtils;
import clustering.ClusteringWithCluterNumber;
import clustering.ClusteringWithGravityCenters;
import clustering.ClusteringWithIntervalCluterNumber;
import clustering.ClusteringWithNbCustersAndSomeCenters;
import clustering.ClusteringWithSomeGravityCenters;
import clustering.ClusteringUtils;
import models.MyClass;
import identificationProcess.FDataComputer;
import identificationProcess.MeusuringMetricsNewFdata;
import identificationProcess.MeasuringMetrics;

public class GABasicOps {
	
	public static Set<Individu> paretoOptimal = new HashSet<Individu>();//contains non-dominated solutions
	public static Set<Individu> elitesStat = new HashSet<Individu>();
	private static int sizeOfPopu = 30;
	public static int sizeOfPopuInit = 80;
	public static int nbPairsSelect = 30;
	public static int cptbesti = 1;
	// 0 : coupling first; 1 : cohesion first; other : neutral
	public static int mode = -1;
	public static int maxGeneration = 10;
	private static double limitAgeProportion = 0.75;
	private static double limitCrossingProportion = 0.5;
	private static double limitSelectProportion = 0.01;
	public static double nbEliteRate = 0.05;
	public static double percentFromParent = 0.2;
	
	//initial population
	public static  ArrayList<Individu> initPopulation(BinaryTree binaryTree, int popuSize) throws IOException{
		Individu e; int i = 0; 
		ArrayList<Individu> population = new ArrayList<Individu>();
		
		System.out.println("Generating random dephts");
		Set<Double> set = dephts(popuSize);
		//for(double t : set){
			e = new Individu();
			//System.out.println("double t.......... "+t);
			Set<Set<MyClass>> clusters = ClusteringUtils.parcoursDendrogramme(binaryTree,0.6);
	//		ClusteringUtils.printClusters(clusters, new PrintStream(new FileOutputStream(GeneticIdentification.resultsPath+"/clusters"+(i+1)+".txt")));
			System.err.println("Clusters are extracted and printed in cluster"+ i +".txt");
			System.err.println("Number of clusters : " + clusters.size());
			e.setChromosomes(clusters);
			population.add(e); i++;
		//}
		return population;
		
	}
	//Random individuals From Romantic

    public static ArrayList<Individu> initPopulation(Set<MyClass> clazzes, int popuSize) throws IOException {
        ArrayList<Individu> population = new ArrayList<Individu>();
        
        Random rand= new Random();
        

        // Random
        MeasuringMetrics.lamda=1;
        MeasuringMetrics.beta=3;
        FDataComputer.lambda=3;
        FDataComputer.beta=1;
        MeusuringMetricsNewFdata.lambda=1;
        MeusuringMetricsNewFdata.beta=3;
        Individu e;
        for (int i = 0; i < 15 ;i++) {
            Random random = new Random();
            List<Set<MyClass>> chromosomeAsList = new ArrayList<Set<MyClass>>();
            
            int nombreDeGenes = random.nextInt(clazzes.size()) + 1;///2 + 1;
        //    nombreDeGenes = 3;
            for (int j = 0; j < nombreDeGenes; j++) {
                Set<MyClass> cluster = new HashSet<MyClass>();
                chromosomeAsList.add(cluster);
            }
            for (MyClass clazz : clazzes) {
                int k = random.nextInt(nombreDeGenes);
                chromosomeAsList.get(k).add(clazz);
            }
        
            Set<Set<MyClass>> genesToRemove = new HashSet<Set<MyClass>>();
            for (Set<MyClass> cluster : chromosomeAsList) {
                if (cluster.size()==0) {
                    genesToRemove.add(cluster);
                }
            }
            chromosomeAsList.removeAll(genesToRemove);
            Set<Set<MyClass>> chromosome = new HashSet<Set<MyClass>>(chromosomeAsList);
            e = new Individu();
             ClusteringUtils.printClusters(chromosome, new PrintStream(new FileOutputStream(GeneticIdentification.resultsPath+"/Initial/random"+(i+1)+".txt")));
            System.err.println("Clusters are extracted and printed in random"+ i +".txt");
            System.err.println("Number of clusters : " + chromosome.size());
            e.setChromosomes(chromosome);
            population.add(e);
        }
        
        MeasuringMetrics.lamda=1;
        MeasuringMetrics.beta=3;
        FDataComputer.lambda=1;
        FDataComputer.beta=1;
        MeusuringMetricsNewFdata.lambda=1;
        MeusuringMetricsNewFdata.beta=1;
        

        MeasuringMetrics.lamda=1;
        MeasuringMetrics.beta=3;
        FDataComputer.lambda=3;
        FDataComputer.beta=1;
        MeusuringMetricsNewFdata.lambda=1;
        MeusuringMetricsNewFdata.beta=3;
        return population;
    }



    private static Set<MyClass> enterListGravityCenters(List<MyClass> classes, String fileName) {
    
        
        Set<MyClass> centers= new TreeSet<MyClass>();
        try {
            File file = new File(fileName);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String centerName;
            while ((centerName = br.readLine()) != null){
                
                MyClass cl= MeasuringMetrics.findCorrespondingClass(classes, centerName.trim());
                if(cl!= null){
                    
                    centers.add(cl);
                }
                else
                    System.out.println("null "+centerName);
                    
            }    
            br.close();
            for(MyClass center: centers)
                System.out.println("Center: "+center.getName());
            
            System.out.println();
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }  
        return centers;
    }

	//Selection  Roulette wheel normal
		public static Individu selectWheelRemain(ArrayList<Individu> population){
			double quota = Math.random();
			double sumQualities = population.stream().mapToDouble(s -> s.getQuality()).sum();
			double cumulProb = 0; 
			for(Individu e : population){
				cumulProb += e.getQuality() / sumQualities; //normalized probabilities quality/sumQualities
				if(quota <= cumulProb){
					return e;
				}
			}
			return null;
			
		}
	//Selection  Roulette wheel - Murata
	public static Individu selectWheel(ArrayList<Individu> population){
		double quota = Math.random(); 
		double cumulProb = 0;
		for(Individu e : population){
			cumulProb += e.getProbability();
			if(quota <= cumulProb){
				//popuTemp.add(e);
				return e;
			}
		}
		
		/*double rand = randomDouble(0, sumQualitiesStream);		
		double cumulProb = 0;
		 for(int i = population.size() -1; i >= 0 ; i--){
			cumulProb += population.get(i).getQuality() - minFitness; 
			if(cumulProb >= rand){
				popuTemp.add(population.get(i));
				return i;
			}
		 }*/
		//do{
		/*Iterator<Individu> itr = population.iterator(); 
		double cumulProb = 0;
		while(itr.hasNext()){
			Individu e = itr.next();
			//for(Individu e : population){
				cumulProb += (e.getQuality() - minFitness) / sumQualitiesStream; //normalized probabilities quality - min /sumQualities
				if(quota <= cumulProb){
					e1 = e;
					popuTemp.add(e);// break;
					//itr.remove();
					return e;
				}
			//}
		}*/
			
		//}while(e1 == null);
		//System.err.println("DEFAULT - NO INDIVIDUAL SELECTED");
		//System.err.println("popu size "+population.size());
		Random r = new Random();
		//popuTemp.add(e1);
		return population.get(r.nextInt(population.size()));
		
	}
	public static ArrayList<Individu> adaptationBasedSelectionOld(ArrayList<Individu> population, int size){
		ArrayList<Individu> nextGeneration = new ArrayList<Individu>();
		 //int stop = (int) (population.size()*limitSelectProportion);
		// System.err.println("stop "+stop+" popu size "+population.size());
		do{
			Individu e = selectWheel(population);
			nextGeneration.add(e);
			population.remove(e);	
		}while(nextGeneration.size() < size);
		//System.err.println("after while ");
		/*for(Individu e : nextGeneration){
			System.out.println("quality ..... "+e.getQuality());
		}*/
		return nextGeneration;
		
	}
	public static ArrayList<Individu> adaptationBasedSelection(ArrayList<Individu> population, int size){
		List<Individu> nextGeneration = new ArrayList<Individu>();
		 //int stop = (int) (population.size()*limitSelectProportion);
		//System.err.println(" popu before size "+population.size());
		//nextGeneration = GABasicOps.sortByMode(population); int i = 0;
		//System.err.println(" popu size "+population.size());
		//int siz = nextGeneration.size() >= size ? size : nextGeneration.size();
		//nextGeneration = nextGeneration.subList(0, siz);
		/*do{
			Individu e = population.get(i);
			nextGeneration.add(e);
			population.remove(e);	i++;
		}while(nextGeneration.size() < size);*/
		//System.err.println("after while ");
		/*for(Individu e : nextGeneration){
			System.out.println("quality ..... "+e.getQuality());
		}*/
		for(Individu e1 : population){
			boolean dominance = false;
			
			for(Individu e2 : population){
				if(e1.dominance(e2)){
					dominance = true; break;
				}
			}
			
			if(!dominance){
				nextGeneration.add(e1);
			}
		}
		return new ArrayList<Individu>(nextGeneration);
		
	}
	// Individual Crossing operation
	public static Individu crossing(Individu one, Individu two){
		//child
		Individu child = new Individu();
		//inherit all chromosomes from one
		Set<Set<MyClass>> chromo= new HashSet<Set<MyClass>>();
		for (Set<MyClass> gene : one.getChromosomes()) {
			Set<MyClass> geneEnfant = new HashSet<MyClass>();
			geneEnfant.addAll(gene);
			chromo.add(geneEnfant);
		}
		child.setChromosomes(chromo);
		//part of chromosomes from Individu two --> percentF...
		//Random random = new Random();
		List<Set<MyClass>> twoAsArrayList = new ArrayList<Set<MyClass>>(two.sortByMode());
		/*for(Set<MyClass> gen : twoAsArrayList)
			System.out.print(" * "+QualityFunction.QualityFun(gen));
		System.out.println("");*/
		//System.out.println("size chromo two "+two.getChromosomes().size());
		//Set<Integer> numbers = distinctsRandInt((int)(twoAsArrayList.size() * GABasicOps.percentFromParent));
		//System.out.println("size "+twoAsArrayList.size()+" numbers "+numbers.toString());
		//ArrayList<Set<MyClass>> chromosomesFromTwo = chromosFromParent(numbers, twoAsArrayList);
		ArrayList<Set<MyClass>> chromosomesFromTwo = new ArrayList<Set<MyClass>>();
		int number = (int)(twoAsArrayList.size() * GABasicOps.percentFromParent);
		number = number < 1? 1 : number;
		chromosomesFromTwo.addAll(twoAsArrayList.subList(0, number));
		//System.err.println("chromos from one "+one.getChromosomes().size()+" chromos from two "+chromosomesFromTwo.size());
		for(Set<MyClass> chromoz : chromosomesFromTwo){
			for (MyClass clazz : chromoz) {
				for (Set<MyClass> geneEnfant : child.getChromosomes()) {
					Iterator<MyClass> it = geneEnfant.iterator();
					while(it.hasNext()){
						MyClass cl = it.next();
						if(cl.equals(clazz)){
							it.remove();
						}
					}
					
				}
			}
			//add the chromosome
			child.addChromosome(chromoz);
		}
		/*Set<MyClass> chromosomeFromTwo = twoAsArrayList.get(random.nextInt(two.getChromosomes().size()));
		for (MyClass clazz : chromosomeFromTwo) {
			for (Set<MyClass> geneEnfant : child.getChromosomes()) {
				if (geneEnfant.contains(clazz)) {
					geneEnfant.remove(clazz);
				}
			}
		}*/
		
		//add the chromosome
		//child.addChromosome(chromosomeFromTwo);
		
		//final child
		Individu fChild = new Individu();
		//System.err.println("size child init "+child.getChromosomes().size());
		//From Romantic - remove empty chromosomes
		for (Set<MyClass> chromosome : child.getChromosomes()) {
			if (chromosome.size() > 0) {
				fChild.addChromosome(chromosome);
			}
		}
		
		//System.err.println("size fchild "+ fChild.getChromosomes().size());
		return fChild;
			
	}
	// Returns a list of chromosomes selected from an individual
	public static ArrayList<Set<MyClass>> chromosFromParent(Set<Integer> numbers, List<Set<MyClass>> parent){
		ArrayList<Set<MyClass>> chromoz = new ArrayList<Set<MyClass>>();
		for(int n : numbers){
			chromoz.add(parent.get(n));
		}
		return chromoz;
		
	}
	//Individu Mutation operation
	public static void mutation(Individu individu, double probability){
		Random rand = new Random();
		List<Set<MyClass>> chromosomes = new ArrayList<Set<MyClass>>(individu.getChromosomes());
		double r = rand.nextDouble();
		if(r<probability){
			double rfusion = rand.nextDouble();
			//Fusion case
			if(individu.getChromosomes().size() > 1)
			if(rfusion < probability/3){
				System.out.println("FUSION   "+rfusion);
				//select two set of MyClasses from chromosomes
				int indexCh1 = rand.nextInt(chromosomes.size());
				int indexCh2 = indexCh1;
				//Avoid identical set
				while(indexCh2 == indexCh1){
					indexCh2 = rand.nextInt(chromosomes.size());
				}
				Set<MyClass> chromosome1 = chromosomes.get(indexCh1);
				Set<MyClass> chromosome2 = chromosomes.get(indexCh2);
				Set<MyClass> newChromosome = fusion(chromosome1, chromosome2);
				//Remove old chromosomes, add the new one
				chromosomes.remove(chromosome1); chromosomes.remove(chromosome2); 
				chromosomes.add(newChromosome);
			}
			
			//Separation case
			double rSep = rand.nextDouble();
			if (rSep<probability/3) {
				System.out.println("SEPARATION   "+rSep);
				//Select a set of MyClasses
				int index = rand.nextInt(chromosomes.size());
				Set<MyClass> chromosome = chromosomes.get(index);
				List<Set<MyClass>> result = separation(chromosome, rand);
				chromosomes.remove(chromosome);
				if(result.size() > 0)
					for(Set<MyClass> chr : result){
						chromosomes.add(chr);
					}
						
			}
			Set<Set<MyClass>> neoChromosomes = new HashSet<Set<MyClass>>(chromosomes);
			individu.getChromosomes().clear();
			individu.setChromosomes(neoChromosomes);		
		}
				
	}
	//Mutation - fusion
	public static Set<MyClass> fusion(Set<MyClass> chromosome1, Set<MyClass> chromosome2 ){
		Set<MyClass> fusion = new HashSet<MyClass>(chromosome1);
		fusion.addAll(chromosome2);
	
		return fusion;			
	}
	//Mutation - separation
	public static List<Set<MyClass>> separation(Set<MyClass> chromosome, Random rand){
		List<MyClass> chromosomeAsList = new ArrayList<MyClass>(chromosome);
		//partition size
		int index = rand.nextInt(chromosomeAsList.size());
		List<MyClass> list1 = chromosomeAsList.subList(0, index);
		List<MyClass> list2 = chromosomeAsList.subList(index, chromosomeAsList.size());
		
		//From Romantic
		Set<MyClass> chromosome1= new HashSet<MyClass>(list1);
		Set<MyClass> chromosome2 = new HashSet<MyClass>(list2);
		List<Set<MyClass>> newChromosomes = new ArrayList<Set<MyClass>>();
		if (chromosome1.size()>0){
			newChromosomes.add(chromosome1);
		}
		if (chromosome2.size()>0){
			newChromosomes.add(chromosome2);
		}
		return newChromosomes;
			
	}
	
	//Determining algo orientation - couplage first or cohesion or neutral
	public static ArrayList<Individu> sortByMode(ArrayList<Individu> population){
		Set<Individu> temp = new HashSet<Individu>();
		ArrayList<Individu> list = new ArrayList<Individu>();
		//System.err.println("*size popu enter method "+population.size());
		for(Individu e : population)
			temp.add(e);
		//System.err.println("size popu after affectation set "+population.size());
		//if(temp.size() < 10)
			//for(Individu e : population) 
				//System.out.println("id "+e.getId()+" nbchr "+e.getChromosomes().size()+" quality "+e.getQuality()+" coupling  "+e.getCoupling()+" cohesion "+e.getLcc());

		//temp.addAll(population);
		//System.err.println("*size temp set "+temp.size());
		for(Individu e : temp)
			list.add(e);
		//list.addAll(temp);
		//System.err.println("*size list before sort "+list.size());
		switch (GABasicOps.mode) {
		case 0: list.sort(Comparator.comparingDouble(Individu::getFFonc).thenComparingDouble(Individu::getFData).reversed());	
			break;
		case 1 : list.sort(Comparator.comparingDouble(Individu::getFData).thenComparingDouble(Individu::getFFonc).reversed());
			break;
		default: list.sort(Comparator.comparingDouble(Individu::getQuality).reversed());
			break;
		}
		//System.err.println("*size list after sort "+list.size());
		return list;
		
	}
	public static Set<Double> dephts(int popuSize){
		Set<Double> set = new HashSet<Double>();
		 Random random = new Random();
		double rand;
		while(set.size() < popuSize){
			rand = 0.0 + (random.nextDouble() * (1.00 - 0.0));
			set.add(Math.round(rand*100.0)/100.0);
		}
		
		return set;
	}
	public static Set<Integer> distinctsRandInt(int size){
		Set<Integer> set = new HashSet<Integer>();
		 Random random = new Random();
		while(set.size() < size){
			set.add(random.nextInt(size + 1));
		}
		
		return set;
	}
	//Evaluate a population and return elites
	public static HashSet<Individu> evaluate(ArrayList<Individu> population){
		HashSet<Individu> elites = new HashSet<Individu>();
		for(Individu e : population){
			e.evaluate();
			//increase the age
			e.setAge(e.getAge() + 1);
		}
		
		//TODO find a solution instead of removing them - As the metrics are normalized, there are some having 1.0 in each objective
		//and they often contain only one cluster
		//System.err.println("\nPopu before remove "+population.size());
		Iterator<Individu> it = population.iterator();
	/*	while(it.hasNext()){
			Individu e = it.next();
			if(e.getCoupling() >= 1 && (e.getLcc() >= 1))
				it.remove();
		}*/
		elites.add(Collections.max(population, Comparator.comparing(s -> s.getFFonc())));
		elites.add(Collections.max(population, Comparator.comparing(s -> s.getFData())));
		//elites.add(Collections.max(population, Comparator.comparing(s -> s.getCoupling())));
		//elites.add(Collections.max(population, Comparator.comparing(s -> s.getLcc())));
		//elites.add(Collections.min(population, Comparator.comparing(s -> s.getExternalCoupling())));
		return elites;
	}
	
	//setting the probability according to murata
	public static void evaluateProba(ArrayList<Individu> population){
		double sumQualitiesStream = 0;
		Individu min = Collections.min(population, Comparator.comparing(s -> s.getQuality()));
		double minFitness = min.getQuality();
		for(Individu e : population){
			sumQualitiesStream += (e.getQuality() - minFitness);
		}
		//double sumQualitiesStream = population.stream().mapToDouble(s -> s.getQuality() - minFitness).sum();
		//System.out.println("max sum "+sumQualitiesStream);
		for(Individu e : population){
			e.setProbability(sumQualitiesStream == 0? 0 : (e.getQuality() - minFitness) / sumQualitiesStream);
			//System.err.println("id "+e.getId()+" nb chrs "+e.getChromosomes().size()+" proba "+e.getProbability());
		}
	}
	
	// update  paretoOptimal - Is it really working?
	public static void updatePareto1(ArrayList<Individu> population){
		for(Individu e1 : population){
			boolean dominance = false;
			
			for(Individu e2 : population){
				if(e1.dominance(e2)){
					dominance = true; break;
				}
			}
			
			if(!dominance){
				 GABasicOps.paretoOptimal.add(e1);
			}
		}
	}
	public static void updatePareto(ArrayList<Individu> population, int iterCpt){
		ArrayList<Individu> list = kungPareto(population);
		System.out.println("list size "+list.size());
		//fill statistics Avg pareto
		GeneticIdentification.avgPareto [iterCpt][iterCpt] = list.stream().mapToDouble(Individu::getQuality).average().getAsDouble();
		//best quality pareto
		GeneticIdentification.bestQualityPareto[iterCpt][iterCpt] = GABasicOps.sortByMode(list).stream().findFirst().get().getQuality();
		if(list.size() > 0)
			GABasicOps.paretoOptimal.addAll(list);
			/*for(Individu e : list)
				if(!GABasicOps.paretoOptimal.contains(e))
				GABasicOps.paretoOptimal.add(e);*/	
	}
	//Genetic Algorithm implementation
	public static ArrayList<Individu> geneticAlgorithm(ArrayList<Individu> population, int nbObjectives, double mutationRate, int iterCpt) throws IOException{
		System.err.println("\n Step 1 : Evaluation ");
		ArrayList<Individu> populationTemp = new ArrayList<Individu>();
		ArrayList<Individu> offspring = new ArrayList<Individu>();
		List<Individu> best10 = new ArrayList<Individu>();
		HashSet<Individu> elites = new HashSet<Individu>();
		elites = GABasicOps.evaluate(population);
		//used later in main
		elitesStat.addAll(elites);
		Individu e1, e2;
		int nextGenerationSize = population.size() + (int) (population.size()*limitSelectProportion);;
		// TODO improve population size management
		/*if(population.size() < 2*nbPairsSelect) 
			nbPairsSelect = population.size() / 2;
		else nbPairsSelect = 20;*/
		
		System.err.println("Number of elites "+elites.size());
		
		System.err.println("\n Selection + Crossover + Mutation Per Pair");
		
		 nbPairsSelect = (int) ((GABasicOps.limitCrossingProportion * population.size())/2);
		System.err.println("\nPopu start "+population.size());
		System.err.println("Pairs "+nbPairsSelect);
		//fill statistics popu
		GeneticIdentification.popu[iterCpt][iterCpt] = population.size();
		
		//System.err.println("\nnbPair "+nbPairsSelect);
		int l = 1;
		
		
		do{
			//System.err.println("\nPair "+ (nbPairs -(nbPairs -l))); l++;
			System.err.println("\nPair "+l); l++;
			System.err.println("\nStep 2 : Selection");
			System.err.println("Calculating fitness values");
			GABasicOps.fitness(population, nbObjectives );
			GABasicOps.evaluateProba(population);
			//fitness(population);
			//e1 = new Individu(); e2 = new Individu();
			e1 = selectWheel(population);
			// Avoid selecting the same individual
			population.remove(e1); 
			//do{
			e2 = selectWheel(population);
			population.remove(e2);
			//}while(e1.getId() == e2.getId());
			
			
			System.err.println("\nStep 3 : Crossover");
			Individu offspring1 = crossing(e1, e2); 
			Individu offspring2 = crossing(e2, e1);
			/*System.err.println(" parent 1 id "+e1.getId()+" nbchr "+e1.getChromosomes().size()+" proba "+e1.getProbability());
			System.err.println(" parent 2 id "+e2.getId()+" nbchr "+e2.getChromosomes().size()+" proba "+e2.getProbability());
			System.err.println(" offspring1 cross id "+offspring1.getId()+" nbchr "+offspring1.getChromosomes().size());
			System.err.println(" offspring2 cross id "+offspring2.getId()+" nbchr "+offspring2.getChromosomes().size());*/
			
			System.err.println("\nStep 4 : Mutation");
			mutation(offspring1, mutationRate);
			mutation(offspring2, mutationRate);
			System.err.println("Saving offspring");
			offspring.add(offspring1); offspring.add(offspring2);
			
			//keep track for statistics
			populationTemp.add(e1); populationTemp.add(e2);
		}while(l <= nbPairsSelect );
		//best 10
		//System.out.println("best 10 ................. ");
		System.err.println("popu temp "+populationTemp.size());
		population.addAll(populationTemp);
		System.err.println("\nPopu  "+population.size());
		best10 = GABasicOps.sortByMode(population);
		int siz = best10.size() >= 10 ? 10 : best10.size();
		//best10 = best10.subList( best10.size() - siz, best10.size());
		best10 = best10.subList(0, siz);
				
		System.err.println("\nStep 5 : Elitist strategy");
		int nbElites = (int) (nbEliteRate * offspring.size());
		elitism(offspring, elites, nbElites);
		offspring.addAll(elites);
		System.err.println("Offspring elitism size "+offspring.size());
		
		//Print top ten
		printTopTen(best10);
									
		//fill statistics Avg top ten
		GeneticIdentification.avgQualityTopTen [iterCpt][iterCpt] = best10.stream().mapToDouble(Individu::getQuality).average().getAsDouble();
		//besti
		GeneticIdentification.qualitybesti[iterCpt][iterCpt] = best10.stream().findFirst().get().getQuality();	
		
		System.err.println("Updating Pareto Optimal");
		GABasicOps.updatePareto(population, iterCpt);
		System.err.println("ParetoOptimal size "+GABasicOps.paretoOptimal.size());
		
		//Strategy keep all individuals until... (fixed in MainAg) --- not anymore
		//keep limitcrossPopu individuals based on adaptation
		System.err.println("adaptation based selection");
		//populationTemp = GABasicOps.adaptationBasedSelection(population);
		//Controlling age
		//if(MainAG.observer == maxGeneration){
		//MainAG.removeOldIndiv(population);
		//}
		//offspring.addAll(population); 
		System.err.println("Next Genration size "+nextGenerationSize);
		System.err.println("popu end size "+population.size());
		/*for(Individu e : offspring){
			System.err.println("id "+e.getId()+" nb chrs "+e.getChromosomes().size());
		}*/
		//GABasicOps.adaptationBasedSelectionOld(offspring, nextGenerationSize);
		offspring.addAll(GABasicOps.adaptationBasedSelectionOld(population, nextGenerationSize - offspring.size()));
		System.err.println("Offspring next size "+offspring.size());
		return offspring;
	}
	
	//Elitist strategy - Murata
	//Remove nbElites individuals from offspring - Add nbElites individuals selected from paretoOptimal
	public static void elitism(ArrayList<Individu> offspring, HashSet<Individu> elites, int nbElites){
		Random rand = new Random();
		
		if(paretoOptimal.size() < nbElites){
			for(int i = 0; i < paretoOptimal.size(); i++){
				offspring.remove(rand.nextInt(offspring.size()));
			}
			offspring.addAll(paretoOptimal);
		}else{
			ArrayList<Individu> pareto = new ArrayList<Individu>();
			pareto.addAll(paretoOptimal);
			for(int i = 0; i < nbElites; i++){
				offspring.remove(rand.nextInt(offspring.size()));
				Individu e = pareto.get(rand.nextInt(pareto.size()));
				offspring.add(e); pareto.remove(e);
			}
		}
		
	}
	
	//Calculate the Fitness value of each individual
	public static void fitness(ArrayList<Individu> population, int nbObjectives){
		double [] weights = weight(nbObjectives);
		double [] weights1 = GABasicOps.weight(2);
		double [] weights2 = GABasicOps.weight(2);
		//case two objectives - coupling lcc
		for(Individu e : population){
			//e.setFFonc( (3* ((e.getCoupling() + e.getLcc()) /2) + 2 * e.getCoupling() -  e.getExternalCoupling()) / 5);
			///e.setFFonc((3*e.getCoupling() + 3*e.getLcc() - e.getExternalCoupling()) / 7);
			///e.setFData((3*e.getInternalData() - e.getExternalData()) / 4);
			//e.setQuality(Math.round((weights[0] * e.getCoupling() + weights[1] * e.getLcc())*10000.0)/10000.0);
			//e.setQuality(weights[0] * e.getCoupling() + weights[1] * e.getLcc() - weights[2] * e.getExternalCoupling());
			e.setFData((FDataComputer.lambda*e.getInternalData() - FDataComputer.beta*e.getExternalData())/
					(FDataComputer.lambda+FDataComputer.beta));
			double FOne=(e.getCoupling()+e.getLcc())/2;
			e.setFFonc((MeasuringMetrics.lamda*FOne - MeasuringMetrics.beta*e.getExternalCoupling())/
					(MeasuringMetrics.lamda+MeasuringMetrics.beta));
			double quality= (MeusuringMetricsNewFdata.lambda* e.getFFonc()+MeusuringMetricsNewFdata.beta*e.getFData())/
					(MeusuringMetricsNewFdata.lambda+MeusuringMetricsNewFdata.beta);
			e.setQuality(quality);
			//From Romantic
			//double result = weights[0] * ((e.getCoupling() + e.getLcc()) / 2) + weights[1] * e.getCoupling();
			//e.setQuality(Math.round((weights[0] * ((e.getCoupling() + e.getLcc()) / 2) + weights[1] * e.getCoupling())*10000.0)/10000.0);
			//if(result > 0.5) e.setQuality(result);
			//else e.setQuality(0.0);
			//System.out.println(" fitness "+ e.getQuality());
		}
	}
	
	public static void fitness(ArrayList<Individu> population){
		for(Individu e : population){
			e.fitness();
			//System.out.println(" fitness "+ e.getQuality());
		}
	}
	//Random weight generation - Murata
	public static double [] weight(int nbObjectives){
		double [] weights = new double [nbObjectives];
		double [] rand = new double [nbObjectives];
		double sum = 0.0;
		for(int i = 0; i < nbObjectives; i++){
			rand[i] = Math.random();
			//System.err.println("rand fitness "+rand[i]);
		}
		sum = Arrays.stream(rand).sum();
		for(int i = 0; i < nbObjectives; i++){
			if(sum == 0.0) weights[i] = 0.0;
			else weights[i] = rand[i] / sum;
			//System.err.println("weights fitness "+weights[i]);
		}
		return weights;
		
	}
	public static void printTopTen(List<Individu> best10) throws IOException{
		Iterator<Individu> itr = best10.iterator();  int it = 1;
		while(itr.hasNext()){
			Individu e = itr.next();
			System.out.println("id "+e.getId()+" nbchr "+e.getChromosomes().size()+" quality "+e.getQuality()+
					" Fonc  "+e.getFFonc()+" Data "+e.getFData());
		//	ClusteringUtils.printClusters(e.getChromosomes(), 
				//	new PrintStream(new FileOutputStream(GeneticIdentification.resultsPath+"/best_"+cptbesti+"_"+it+"fonc_"+e.getFFonc()+"_data_"+e.getFData()+".txt")));
			it++;
		}
	}
	public static void printTopTen1(List<Individu> best10) throws IOException{
		Iterator<Individu> itr = best10.iterator();  int it = 1;
		while(itr.hasNext()){
			Individu e = itr.next();
			System.out.println("id "+e.getId()+" nbchr "+e.getChromosomes().size()+" quality "+e.getQuality()+
					" coupling  "+e.getCoupling()+" cohesion "+e.getLcc()+" externCpl "+e.getExternalCoupling());
	//		ClusteringUtils.printClusters(e.getChromosomes(), 
			//		new PrintStream(new FileOutputStream(GeneticIdentification.resultsPath+"/best_"+cptbesti+"_"+it+"cpl_"+e.getCoupling()+"_lcc_"+e.getLcc()+"_extcpl_"+e.getExternalCoupling()+".txt")));
			it++;
		}
	}
	static double randomDouble(double min, double max) {
	    if (min >= max) {
	        throw new IllegalArgumentException("max must be greater than min");
	    }
	    Random r = new Random();
	    return min + (max - min) * r.nextDouble();
	}
	public static ArrayList<Individu> kungPareto(ArrayList<Individu> population){
		population.sort(Comparator.comparingDouble(Individu::getFFonc).reversed());
		int size = population.size();
		if(population.size() == 1){
			return population;
		}
		List<Individu> top = new ArrayList<Individu>();
		List<Individu> bottom = new ArrayList<Individu>();
		top = kungPareto(new ArrayList<Individu>(population.subList(0, (size + 1) / 2)));
		bottom = kungPareto(new ArrayList<Individu>(population.subList((size + 1) / 2, size)));
		ArrayList<Individu> nondomTop = nonDominated(top);
		ArrayList<Individu> nondomBot = nonDominated(bottom);
		Set<Individu> m = new HashSet<Individu>(); m.addAll(nondomTop);
		for(int i = 0; i < nondomBot.size(); i++)
			if(!nonDominated(nondomBot.get(i), nondomTop))
				m.add(nondomBot.get(i));
	
		return new ArrayList<Individu>(m);
	}
	public static ArrayList<Individu> nonDominated(List<Individu> popu){
		ArrayList<Individu> nondominated = new ArrayList<Individu>();
		for(Individu e1 : popu){
			boolean dominance = false;
			
			for(Individu e2 : popu){
				if(e1.dominance(e2)){
					dominance = true; break;
				}
			}
			
			if(!dominance){
				nondominated.add(e1);
			}
		}
		return nondominated;
		
	}
	public static boolean nonDominated(Individu e,List<Individu> popu){
		boolean dominance = false;
		for(Individu e1 : popu){
				if(e.dominance(e1)){
					dominance = true; break;
				}
		}
		return dominance;
		
	}
	//Not used yet
	public static ArrayList<Individu> topTen( ArrayList<Individu> popu){
		ArrayList<Individu> list = new ArrayList<Individu>();
		int cpt = 0;
		for(Individu e : popu){
			if(e.getLcc() > 0.5 && (cpt < 10)){
				list.add(e); cpt++;
			}
		}
		System.err.println("list size "+popu.size());
		return list;
	}
	
	public void setSizeOfPopu(int n){
		sizeOfPopu = n;
	}
	public int getSizeOfPopu(){
		return sizeOfPopu;
	}
	public void setLimitCrossingProportion(double n){
		limitCrossingProportion = n;
	}
	public double getLimitCrossingProportion(){
		return limitCrossingProportion;
	}
	public void setLimitAgeProportion(double n){
		limitAgeProportion = n;
	}
	public double getLimitAgeProportion(){
		return limitAgeProportion;
	}
	public double getLimitSelectProportion(){
		return limitSelectProportion;
	}
	public void setLimitSelectProportion(double n){
		limitSelectProportion = n;
	}
}
