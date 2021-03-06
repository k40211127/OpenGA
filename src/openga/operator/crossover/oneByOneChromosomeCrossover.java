package openga.operator.crossover;

/**
 * <p>Title: The OpenGA project which is to build general framework of Genetic algorithm.</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: Yuan-Ze University</p>
 * @author Chen, Shih-Hsin
 * @version 1.0
 */

public class oneByOneChromosomeCrossover extends twoPointCrossover2 {
  public oneByOneChromosomeCrossover() {
  }
  //start to crossover
  public void startCrossover(){
    for(int i = 0 ; i < popSize ; i ++ ){
       //test the probability is larger than crossoverRate.
       if(Math.random() <= crossoverRate){
         //to get the other chromosome to crossover
         int index2;
         if(i % 2 == 0){
           index2 = i+1;
         }
         else if(i % 2 == 1 && i > 1){
           index2 = i-1;
         }
         else{
           index2 = 0;
         }

         setCutpoint();
         copyElements(i, index2);
         copyElements(index2, i);
       }
    }
  }

  /**
   * The two chromosomes produce a new offspring
   * @param index1 The first chromosome to crossover
   * @param index2 The second chromosome to crossover
   */
  private void copyElements(int index1, int index2){
    //to modify the first chromosome between the index1 to index2, which genes
    //is from chromosome 2.
    int counter = 0;
    for(int i = cutPoint1 ; i <= cutPoint2; i ++ ){
      while(checkConflict(newPop.getSingleChromosome(index2).genes[counter], newPop.getSingleChromosome(index1).genes) == true){
        counter ++;
      }
      newPop.setGene(index1, i, newPop.getSingleChromosome(index2).genes[counter]);
      counter ++;
    }
  }

  /**
   * if there is the same gene, it return the index of the gene.
   * Else, default value is -1, which is also mean don't have the same gene
   * during cutpoint1 and cutpoint2.
   * @param newGene
   * @param _chromosome
   * @return
   */
  private boolean checkConflict(int newGene, int _chromosome[]){
    boolean hasConflict = false;
    for(int i = 0 ; i < cutPoint1 ; i ++ ){
      if(newGene == _chromosome[i]){
        return true;
      }
    }

    for(int i = cutPoint2 + 1 ; i < chromosomeLength ; i ++ ){
      if(newGene == _chromosome[i]){
        return true;
      }
    }

    return hasConflict;
  }

  public static void main(String[] args) {
    oneByOneChromosomeCrossover oneByOneChromosomeCrossover1 = new oneByOneChromosomeCrossover();
  }

}