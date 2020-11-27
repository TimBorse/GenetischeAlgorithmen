package Exercise1;

import Exercise1.Genetics.Enums.Protection;
import Exercise1.Genetics.GeneSet;
import Exercise1.Genetics.Enums.CrossOverMethodType;
import Exercise1.Genetics.Enums.ReplicationScheme;
import Exercise1.Genetics.Threads.RunGenerationsThread;

public class Run {
    public static void main(String[] args) {
        GeneSet gs;
        long startTime = System.currentTimeMillis();
        try {
            gs = new GeneSet(200, 200, 3000, 0.05, 1, 10, ReplicationScheme.RANK_BASED_SELECTION, CrossOverMethodType.FRONTREAR, Protection.NONE);
            //gs.printGenerationResult(0.9, 0.006);
            gs.findIdealParameters(0.5, 0.9, 0.02, 0.0, 0.03,0.002);
        }catch (Exception e){
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        long timeInSecs = (endTime-startTime)/1000;
        System.out.println("The process finished after: "+ (int)(timeInSecs/60) + "Minutes and " + (int)(timeInSecs%60)+"Seconds");
    }
}
