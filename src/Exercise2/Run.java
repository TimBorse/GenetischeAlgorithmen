package Exercise2;

import Exercise2.Genetics.Enums.Protection;
import Exercise2.Genetics.Enums.RecombinationType;
import Exercise2.Genetics.Enums.ReplicationScheme;
import Exercise2.Genetics.Models.GeneSet;
import Exercise2.Genetics.UI.SimulationGui;

public class Run {
    public static SimulationGui window;
    public static void main(String[] args) {
        try {
            GeneSet gs = new GeneSet("05-map-10x10-36border.txt" ,10, 200, 1000, 36, 50, ReplicationScheme.NONE, RecombinationType.GREEDY_CROSSOVER, Protection.NONE);
            //gs.runSimulation(0.9,0.05);
            long start = System.currentTimeMillis();
            window = new SimulationGui("Simulation");
            window.setVisible(true);
            //gs.findIdealParameters(0.85, 0.9, 0.05, 0, 0.01, 0.005);
            //System.out.println(gs.getBestParameters().getPm()+";"+gs.getBestParameters().getPc()+";"+gs.getBestParameters().getAverageGens());
            long end = System.currentTimeMillis();
            System.out.println("The process finished after ");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
