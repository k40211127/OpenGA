package openga.operator.localSearch;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import openga.chromosomes.chromosome;
import openga.chromosomes.population;
import openga.chromosomes.populationI;

public class localSearchByIG extends localSearchBy2Opt implements localSearchMTSPI {

  int numberofSalesmen;

  @Override
  public void startLocalSearch() {
    selectedIndex = getBestIndex(archive);
    populationI _pop = new population();//to store the temp chromosome
    _pop.setGenotypeSizeAndLength(pop.getEncodedType(), 1, pop.getLengthOfChromosome(),
            pop.getNumberOfObjectives());
    _pop.initNewPop();
    _pop.setChromosome(0, archive.getSingleChromosome(selectedIndex));//Set the original solution to the 1st chromosome (best).
    evaluateNewSoln(_pop.getSingleChromosome(0));
    
    double ObjValue = _pop.getSingleChromosome(0).getObjValue()[0];
    double lsObjValue = IG_ls(_pop);
//     System.out.println("lsObjValue�G"+lsObjValue);
    if (lsObjValue > ObjValue) {
//      _pop.setChromosome(0, _pop.getSingleChromosome(0));
//      System.out.println("improve _pop : "+_pop.getSingleChromosome(0).toString1()+"\t\t"+ObjValue +"\t"+lsObjValue);
      _pop.setObjectiveValue(0, _pop.getSingleChromosome(0).getObjValue());
      updateArchive(_pop.getSingleChromosome(0)); //update the solution in the elite set.
    }
  }

  
  public final double IG_ls(populationI _sp) {
    int UsedSolution = 0;
    chromosomeLength = _sp.getSingleChromosome(0).genes.length;    
    List<Integer> destructedPart = new ArrayList<>();
    List<Integer> reservePart = new ArrayList<>();

    double originalObjValue = _sp.getSingleChromosome(0).getObjValue()[0];

    for (int i = 0; i < chromosomeLength; i++) {
      reservePart.add(_sp.getSingleChromosome(0).genes[i]);
    }

    setdestructedPart(reservePart, destructedPart);
    //  insertPoint : number of insert position
    int insertPoint = reservePart.size() + 1;

    List<Integer> tmpPart = new ArrayList<>();
    tmpPart.addAll(reservePart);
    double tmpObjValue;
    List<Integer> lsPart = new ArrayList<>();
    lsPart.addAll(reservePart);
    double lsObjValue = 0;
    for (int i = 0; i < maxNeighborhood; i++) {
      //    add destructedPart gene and initialize Chromosome then calculate objectivefunction    
      lsPart.add(0, destructedPart.get(i));
      chromosome lsChromosome = new chromosome();
      lsChromosome.setGenotypeAndLength(true, lsPart.size(), 1);
      lsChromosome.setSolution(lsPart);
      evaluateNewSoln(lsChromosome);
      lsObjValue = lsChromosome.getObjValue()[0];
//      System.out.println(insertPoint);
      for (int j = 0; j < insertPoint; j++) {
        tmpPart.add(j, destructedPart.get(i));
        chromosome tmpChromosome = new chromosome();
        tmpChromosome.setGenotypeAndLength(true, tmpPart.size(), 1);
        tmpChromosome.setSolution(tmpPart);
        evaluateNewSoln(tmpChromosome);
        tmpObjValue = tmpChromosome.getObjValue()[0];
//        System.out.println(tmpObjValue);
        if (tmpObjValue > lsObjValue) {
          lsObjValue = tmpObjValue;
          lsPart.clear();
          lsPart.addAll(tmpPart);
          tmpPart.remove(j);

        } else {
          tmpPart.remove(j);
        }
      }
      if (i == (maxNeighborhood - 1)) {
        UsedSolution += (tmpPart.size() - tmpPart.get(tmpPart.size() - 1) + 1);
      }
      tmpPart.clear();
      tmpPart.addAll(lsPart);
      insertPoint++;
    }
    UsedSolution++;
    double localsearchObj;
    if (lsObjValue > originalObjValue) {
      _sp.getSingleChromosome(0).setSolution(lsPart);
      evaluateNewSoln(_sp.getSingleChromosome(0));
      localsearchObj = _sp.getSingleChromosome(0).getObjValue()[0];
      currentUsedSolution += UsedSolution;
    } else {
      localsearchObj = originalObjValue;
    }
    return localsearchObj;
  }

  public final void setdestructedPart(List<Integer> reservePart, List<Integer> destructedPart) {
    int cities = chromosomeLength;
    int[] Destructgenes = new int[numberofSalesmen-1];
    int numberofDestructgenes = maxNeighborhood;

    for (int i = 0; i < numberofSalesmen-1; i++) {
      if (numberofDestructgenes == 0) {
        break;
      }
      int raitoNumber = (int) maxNeighborhood;
      for (int j = 0; j < raitoNumber; j++) {
        if (numberofDestructgenes == 0) {
          break;
        }
        Destructgenes[i] += 1;
        numberofDestructgenes -= 1;
      }
    }

      int frequency = Destructgenes[0],count=0;   
      for (int j = 0; j < frequency; j++) {
        int tmp;
        tmp = new Random().nextInt(cities-count);
        destructedPart.add(reservePart.get(tmp));
        reservePart.remove(reservePart.get(tmp));
        count++;
        Destructgenes[0]--;
//        System.out.println("tmp:"+tmp);
      }
  }

  public int getBestIndex(populationI arch1) {
    int index = 0;
    double bestobj = 0;
    for (int k = 0; k < arch1.getPopulationSize(); k++) {
      if (arch1.getObjectiveValues(k)[0] > bestobj) {
        bestobj = arch1.getObjectiveValues(k)[0];
        index = k;
      }
    }
    return index;
  }
  @Override
  public chromosome evaluateNewSoln(chromosome chromosome1) {
    for (int k = 0; k < ObjectiveFunction.length; k++) {
      ObjectiveFunction[k].setData(chromosome1, numberofSalesmen);
      chromosome1.getObjValue();
      chromosome1.setObjValue(ObjectiveFunction[k].getObjectiveValues(k));
    }
    return chromosome1;
  }
  @Override
  public void setNumberofSalesmen(int numberofSalesmen) {
    this.numberofSalesmen = numberofSalesmen;
  }
}
