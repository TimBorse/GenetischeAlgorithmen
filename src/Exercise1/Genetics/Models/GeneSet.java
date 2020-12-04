package Exercise1.Genetics.Models;

import Exercise1.Genetics.Enums.RecombinationType;
import Exercise1.Genetics.Enums.Protection;
import Exercise1.Genetics.Enums.ReplicationScheme;
import Exercise1.Genetics.Threads.RunGenerationsThread;
import Exercise1.Run;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Scanner;

public class GeneSet {

    private ArrayList<ParameterValue> parameterValues;
    private final int genecnt;
    private final int genelen;
    private final int maxgenerations;
    private double pc;
    private double pm;
    private final double initrate;
    private final double acceptRate;
    private final int numberOfRuns;
    private final ReplicationScheme replicationScheme;
    private final RecombinationType crossingOverMethod;
    private final Protection protection;
    private int rankBasedSelectionParameter_s;
    public static int progress = 0;
    private int[] result;

    public GeneSet(int genecnt, int genelen, int maxgenerations, double initrate, double acceptRate, int numberOfRuns, ReplicationScheme replicationScheme, RecombinationType crossingOverMethod, Protection protection) {
        this.genecnt = genecnt;
        this.genelen = genelen;
        this.maxgenerations = maxgenerations;
        this.initrate = initrate;
        this.acceptRate = acceptRate;
        this.numberOfRuns = numberOfRuns;
        this.replicationScheme = replicationScheme;
        this.crossingOverMethod = crossingOverMethod;
        this.protection = protection;
    }

    public int[] getResult(){
        return this.result;
    }

    private int[] runGeneration() throws InterruptedException {
        RunGenerationsThread[] threads = new RunGenerationsThread[numberOfRuns];
        int sum = 0;
        int maxValue = 0;
        //Starts one thread for each number of runs
        //Example: You want the average of 10 runs/Parameter -> 10 Threads
        for (int i = 0; i < numberOfRuns; i++) {
            threads[i] = new RunGenerationsThread(genecnt, genelen, maxgenerations, initrate, acceptRate, pc, pm, crossingOverMethod, replicationScheme, protection);
            threads[i].setRankBasedSelectionParameter_s(rankBasedSelectionParameter_s);
            threads[i].start();
        }

        //Checks if the threads are finished and adds the given values to the sum/maxValue
        for (RunGenerationsThread thread : threads) {
            if (thread != null) {
                thread.join();
                sum += thread.getGenerationCount();
                if (maxValue < thread.getMaxValue())
                    maxValue = thread.getMaxValue();
            }

        }

        return new int[]{(sum / this.numberOfRuns), maxValue};
    }

    //Tests all Parameters of the given range and writes it to a results file
    public void findIdealParameters(double pcStart, double pcEnd, double pcStep, double pmStart, double pmEnd, double pmStep) throws IOException, InterruptedException {
        FileWriter fileWriter = new FileWriter("results.txt");
        parameterValues = new ArrayList<>();
        //Amount of runs required to check each parameter combination
        int requiredRuns = (int) (((pcEnd-pcStart)/pcStep+1)*((pmEnd-pmStart)/pmStep+1));
        for (double a = pcStart; BigDecimal.valueOf(a).setScale(5, RoundingMode.HALF_UP).doubleValue() <= BigDecimal.valueOf(pcEnd).setScale(5, RoundingMode.HALF_UP).doubleValue(); a += pcStep) {
            pc = BigDecimal.valueOf(a).setScale(5, RoundingMode.HALF_UP).doubleValue();
            for (double j = pmStart; BigDecimal.valueOf(j).setScale(5, RoundingMode.HALF_UP).doubleValue() <= BigDecimal.valueOf(pmEnd).setScale(5, RoundingMode.HALF_UP).doubleValue(); j += pmStep) {
                pm = BigDecimal.valueOf(j).setScale(5, RoundingMode.HALF_UP).doubleValue();
                writeFile(fileWriter, pc, pm);
                //Updates progress for the UI
                progress++;
                Run.window.setProgressValue((int) ((double)progress/(double)requiredRuns*100d));
                Run.window.setProgressLabel(progress, requiredRuns);
            }
            try {
                fileWriter.write("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        fileWriter.close();
    }

    //Runs simulation for given pc and pm
    public void runSimulation(double pc, double pm) throws InterruptedException {
        this.pc = pc;
        this.pm = pm;
        result = runGeneration();
    }

    //Gets the best found parameter combination
    public ParameterValue getBestParameters(){
        ParameterValue bestValue = null;
        for(ParameterValue value : parameterValues){
            if(bestValue==null)
                bestValue = value;
            else if(value.getAverageGens()<bestValue.getAverageGens()){
                bestValue = value;
            }
        }
        return bestValue;
    }

    //Runs Simulation for given pc and pm and writes it to result file
    private void writeFile(FileWriter fileWriter, double pc, double pm) throws InterruptedException {
        int[] result = runGeneration();
        int avgRuns = result[0];
        parameterValues.add(new ParameterValue(pc, pm, avgRuns));
        try {
            fileWriter.write(pm + "\t" + pc + "\t" + avgRuns + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setRankBasedSelectionParameter_s(int rankBasedSelectionParameter_s) {
        this.rankBasedSelectionParameter_s = rankBasedSelectionParameter_s;
    }
}


