package Exercise1;

import Exercise1.Genetics.UI.SimulationGui;

public class Run {
    public static SimulationGui window;
    public static void main(String[] args) {
        //Opens the UI to choose Parameters
        window = new SimulationGui("Simulation");
        window.setVisible(true);
    }
}
