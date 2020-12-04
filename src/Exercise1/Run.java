package Exercise1;

import Exercise1.Genetics.UI.SimulationGui;

public class Run {
    public static SimulationGui window;
    public static void main(String[] args) {

        window = new SimulationGui("Test");
        window.setVisible(true);
       /* GeneSet gs;
        long startTime = System.currentTimeMillis();
        try {
            gs = new GeneSet(200, 200, 3000, 0.05, 1, 15, ReplicationScheme.DOUBLE_BEST_HALF, CrossOverMethodType.RECOMBINATION, Protection.NONE);
            //gs.printGenerationResult(0.9, 0.006);
            gs.findIdealParameters(0.5, 0.9, 0.02, 0.0, 0.03,0.002);
        }catch (Exception e){
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        long timeInSecs = (endTime-startTime)/1000;
        System.out.println("The process finished after: "+ (int)(timeInSecs/60) + "Minutes and " + (int)(timeInSecs%60)+"Seconds");*/
    }
}
