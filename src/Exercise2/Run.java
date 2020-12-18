package Exercise2;

import Exercise2.Genetics.Enums.Protection;
import Exercise2.Genetics.Enums.RecombinationType;
import Exercise2.Genetics.Enums.ReplicationScheme;
import Exercise2.Genetics.Models.GeneSet;

public class Run {
    public static void main(String[] args) {
        try {
            GeneSet gs = new GeneSet(100, 1000, 1, 50, ReplicationScheme.DOUBLE_BEST_HALF, RecombinationType.GREEDY_CROSSOVER, Protection.NONE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
