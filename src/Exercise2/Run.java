package Exercise2;

import Exercise2.Genetics.Enums.Protection;
import Exercise2.Genetics.Enums.RecombinationType;
import Exercise2.Genetics.Enums.ReplicationScheme;
import Exercise2.Genetics.Models.GeneSet;

public class Run {
    public static void main(String[] args) {
        try {
            GeneSet gs = new GeneSet("06-map-200x200-500.txt" ,200, 100, 1000, 41.66, 20, ReplicationScheme.DOUBLE_BEST_HALF, RecombinationType.GREEDY_CROSSOVER, Protection.BEST);
            gs.runSimulation(0.9,0.05);
            //System.out.println(gs.getBestParameters().getPm()+";"+gs.getBestParameters().getPc()+";"+gs.getBestParameters().getAverageGens());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
