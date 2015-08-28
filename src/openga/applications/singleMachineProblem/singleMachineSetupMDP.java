package openga.applications.singleMachineProblem;
import openga.applications.singleMachine;
import openga.chromosomes.*;
import openga.operator.selection.*;
import openga.operator.crossover.*;
import openga.operator.mutation.*;
import openga.ObjectiveFunctions.*;
import openga.MainProgram.*;
import openga.ObjectiveFunctions.*;
import openga.Fitness.*;
import openga.util.printClass;
import openga.util.fileWrite1;
import openga.operator.clone.*;

/**
 * <p>Title: The OpenGA project which is to build general framework of Genetic algorithm.</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: Yuan-Ze University</p>
 * @author Chen, Shih-Hsin
 * @version 1.0
 */

public class singleMachineSetupMDP extends singleMachineSetupDP {
  public singleMachineSetupMDP() {
  }
  cloneI clone1;
  public ObjectiveFunctionSchedule2I ObjectiveFunction[];
  public double processingTime[][];
  public int numberOfJobs;
  public String fileName;

  double GADPCPUTIME = 0, DPCPUTIME = 0;

  public void setData(int numberOfJobs, double processingTime[][], String fileName){
    this.numberOfJobs = numberOfJobs;
    numberOfJob = numberOfJobs;
    this.processingTime = processingTime;
    this.fileName = fileName;
  }

  public void initiateVars(){
    GaMain     = new singleThreadGAwithInitialPop2();
    Population = new population();
    Selection  = new binaryTournament();
    Crossover  = new twoPointCrossover2();
    Crossover2 = new PMX();
    Mutation   = new swapMutation();
    Mutation2  = new inverseMutation();
    ObjectiveFunction = new ObjectiveFunctionSchedule2I[numberOfObjs];
    ObjectiveFunction[0] = new ObjectiveEiTiSetupCommonDueForSingleMachine();
    //ObjectiveFunction[1] = new ObjectiveTardinessForFlowShop();//the second one.
    Fitness    = new singleObjectiveFitness();
    objectiveMinimization = new boolean[numberOfObjs];
    objectiveMinimization[0] = true;
    //objectiveMinimization[1] = true;
    encodeType = true;

    //set schedule data to the objectives
    ObjectiveFunction[0].setScheduleData(processingTime, numberOfMachines);
    DEFAULT_generations = totalSolnsToExamine/DEFAULT_PopSize;
    clone1  = new solutionVectorCloneWithMutation();//swap mutation
    //DEFAULT_generations = 10000;
    //set the data to the GA main program.
    GaMain.setData(Population, Selection, Crossover, Mutation, ObjectiveFunction, Fitness, DEFAULT_generations, DEFAULT_initPopSize,DEFAULT_PopSize,
                   numberOfJob, DEFAULT_crossoverRate, DEFAULT_mutationRate, objectiveMinimization, numberOfObjs, encodeType, elitism);
    //GaMain.setSecondaryCrossoverOperator(Crossover2, true);
    //GaMain.setSecondaryMutationOperator(Mutation2, true);
  }


  public void startMain(){
    openga.util.timeClock timeClock1 = new openga.util.timeClock();
    openga.util.timeClock timeClock2 = new openga.util.timeClock();//for Dominance Properties
    timeClock1.start();
    GaMain.initialStage();
    timeClock2.start();
    constructInitialSolutions(GaMain.getPopulation());
    //To collect DP information
    timeClock2.end();
    DPCPUTIME = timeClock2.getExecutionTime()/1000.0;
    GaMain.ProcessObjectiveAndFitness();
    double DP_BestResult = GaMain.getPopulationBestObjValue();
    //GA starts
    for(int i = 0 ; i < DEFAULT_generations ; i ++ ){
      if(i % 50 != 0){//i % (5*numberOfJob) != 0
        GaMain.startGA();
      }
      else{
        constructInitialSolutions2(GaMain.getPopulation());
      }
    }

    timeClock1.end();
    GADPCPUTIME = timeClock1.getExecutionTime()/1000.0;
    //System.out.println("\nThe final result");
    int bestInd = getBestSolnIndex(GaMain.getArchieve());
    String implementationResult = "";
    implementationResult += fileName+"\t"+numberOfJob+"\t" +DP_BestResult+"\t"+ DPCPUTIME+"\t"+ GaMain.getArchieve().getSingleChromosome(bestInd).getObjValue()[0]+"\t"+GADPCPUTIME+"\n";
    writeFile("singleMachineSetupMDP_20070620", implementationResult);
    System.out.println(implementationResult);
  }

  public void constructInitialSolutions(populationI _Population){
    for(int i = 0 ; i < DEFAULT_PopSize ; i ++ ){
      int sequence[] = new int[numberOfJob];
      for(int j = 0 ; j < numberOfJob ; j ++ ){
        sequence[j] = j;
      }

      homework.schedule.singleMachineSetupDP singleMachine1 = new homework.schedule.singleMachineSetupDP();
      singleMachine1.setData(numberOfJobs, processingTime, sequence);
      //singleMachine1.setIterations(1);

      singleMachine1.generateInitialSolution(i);
      singleMachine1.startAlgorithm();
      _Population.getSingleChromosome(i).setSolution(singleMachine1.getSolution());
    }
  }

  public void constructInitialSolutions2(populationI _Population){
    for(int i = 0 ; i < DEFAULT_PopSize ; i ++ ){
      homework.schedule.singleMachineSetupDP singleMachine1 = new homework.schedule.singleMachineSetupDP();
      singleMachine1.setData(numberOfJobs, processingTime, _Population.getSingleChromosome(i).getSolution());
      //singleMachine1.setIterations(DP_Iter);

      //singleMachine1.generateInitialSolution(i);
      singleMachine1.startAlgorithm();
      _Population.getSingleChromosome(i).setSolution(singleMachine1.getSolution());

    }
  }

  public static void main(String[] args) {
    System.out.println("singleMachineSetupMDP_20070620");

    int counter = 0;
    int repeatExperiments = 30;
    int jobSets[] = new int[]{15, 20, 25};//10, 15, 20, 25, 50, 100, 150, 200
    String type[] = new String[]{"low", "med", "high"};//"low", "med", "high"
    double crossoverRate[] = new double[]{0.8},//0.8, 0.5 (0.5)
           mutationRate[] = new double[]{0.3};//0.6, 0.3  (0.3)
    int populationSize[] = new int[]{100};//200, 100

    for(int m = 0 ; m < jobSets.length ; m ++ ){//jobSets.length
      for(int j = 0 ; j < type.length ; j ++ ){
        for(int k = 1 ; k <= 15 ; k ++ ){
          openga.applications.data.singleMachineSetupData singleMachineData1 = new openga.applications.data.singleMachineSetupData();
          String fileName = "instances\\SingleMachineSetup\\"+type[j]+"\\"+jobSets[m]+"_"+k+".etp";
          //fileName = "Data\\SMSetupTime8.txt";//for test
          singleMachineData1.setData(fileName);
          singleMachineData1.getDataFromFile();
          int numberOfJobs = singleMachineData1.getSize();
          double processingTime[][] = singleMachineData1.getProcessingTime();

          for(int i = 0 ; i < repeatExperiments ; i ++ ){
            System.out.println("Combinations: "+counter);
            singleMachineSetupMDP singleMachine1 = new singleMachineSetupMDP();
            singleMachine1.setData(numberOfJobs, processingTime, fileName);
            singleMachine1.setParameters(populationSize[0], crossoverRate[0], mutationRate[0], 1);
            singleMachine1.initiateVars();
            singleMachine1.startMain();
            counter ++;
          }
        }
      }
    }
  }
}