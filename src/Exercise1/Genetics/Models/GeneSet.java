package Exercise1.Genetics.Models;

import Exercise1.Genetics.Enums.CrossOverMethodType;
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
    private final CrossOverMethodType crossingOverMethod;
    private final Protection protection;
    private int rankBasedSelectionParameter_s;
    public static int progress = 0;
    private int[] result;

    public GeneSet(int genecnt, int genelen, int maxgenerations, double initrate, double acceptRate, int numberOfRuns, ReplicationScheme replicationScheme, CrossOverMethodType crossingOverMethod, Protection protection) {
        this.genecnt = genecnt;
        this.genelen = genelen;
        this.maxgenerations = maxgenerations;
        this.initrate = initrate;
        this.acceptRate = acceptRate;
        this.numberOfRuns = numberOfRuns;
        this.replicationScheme = replicationScheme;
        this.crossingOverMethod = crossingOverMethod;
        this.protection = protection;
        if(replicationScheme == ReplicationScheme.RANK_BASED_SELECTION){
            String s;
            do {
                System.out.println("Wählen sie den Parameter s (Natürliche Zahl): ");
                Scanner sc = new Scanner(System.in);
                s = sc.next();
            }while (!s.matches("[0-9]*"));
            rankBasedSelectionParameter_s = Integer.parseInt(s);
        }
    }

    public int[] getResult(){
        return this.result;
    }

    private int[] runGeneration() throws InterruptedException {
        RunGenerationsThread[] threads = new RunGenerationsThread[numberOfRuns];
        int sum = 0;
        int maxValue = 0;
        for (int i = 0; i < numberOfRuns; i++) {
            threads[i] = new RunGenerationsThread(genecnt, genelen, maxgenerations, initrate, acceptRate, pc, pm, crossingOverMethod, replicationScheme, protection);
            threads[i].setRankBasedSelectionParameter_s(rankBasedSelectionParameter_s);
            threads[i].start();
        }

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

    public void findIdealParameters(double pcStart, double pcEnd, double pcStep, double pmStart, double pmEnd, double pmStep) throws IOException, InterruptedException {
        FileWriter fileWriter = new FileWriter("results.txt");
        parameterValues = new ArrayList<>();
        int requiredRuns = (int) (((pcEnd-pcStart)/pcStep+1)*((pmEnd-pmStart)/pmStep+1));
        for (double a = pcStart; BigDecimal.valueOf(a).setScale(5, RoundingMode.HALF_UP).doubleValue() <= BigDecimal.valueOf(pcEnd).setScale(5, RoundingMode.HALF_UP).doubleValue(); a += pcStep) {
            pc = BigDecimal.valueOf(a).setScale(5, RoundingMode.HALF_UP).doubleValue();
            for (double j = pmStart; BigDecimal.valueOf(j).setScale(5, RoundingMode.HALF_UP).doubleValue() <= BigDecimal.valueOf(pmEnd).setScale(5, RoundingMode.HALF_UP).doubleValue(); j += pmStep) {
                pm = BigDecimal.valueOf(j).setScale(5, RoundingMode.HALF_UP).doubleValue();
                writeFile(fileWriter, pc, pm);
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
        ParameterValue bestParams = getBestParameters();
        System.out.println("The best Parameters are: pc = "+ bestParams.getPc()+ ", pm = " + bestParams.getPm() + " with an average of "+ bestParams.getAverageGens() + " Generations");
        fileWriter.close();
    }

    public void printGenerationResult(double pc, double pm) throws InterruptedException {
        this.pc = pc;
        this.pm = pm;
        result = runGeneration();
        System.out.println("Mittel der Generationen: " + result[0]);
        System.out.println("Mutationsrate: " + pm);
        System.out.println("Rekombinationsrate: " + pc);
        System.out.println("Anzahl der Gene: " + genecnt);
        System.out.println("Länge der Gene: " + genelen);
        System.out.println("Initiationsrate: " + initrate);
        System.out.println("Crossovermethod: " + crossingOverMethod);
        System.out.println("Replicationscheme: " + replicationScheme);
        System.out.println("Accepted upper boder: " + (int) (genelen * acceptRate));
        System.out.println("Highest value: " + result[1]);


    }

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

    private void writeFile(FileWriter fileWriter, double pc, double pm) throws InterruptedException {
        int[] result = runGeneration();
        int avgRuns = result[0];
        int maxValue = result[1];
        parameterValues.add(new ParameterValue(pc, pm, avgRuns));
        System.out.println("Mittel der Generationen: " + avgRuns);
        System.out.println("Mutationsrate: " + pm);
        System.out.println("Rekombinationsrate: " + pc);
        System.out.println("Highest value: " + maxValue);
        System.out.println("---------------------------------------------------------------------------------");
        try {
            fileWriter.write(pm + "\t" + pc + "\t" + avgRuns + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

