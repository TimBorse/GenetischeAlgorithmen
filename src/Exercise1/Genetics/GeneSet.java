package Exercise1.Genetics;

import Exercise1.Genetics.Enums.CrossOverMethodType;
import Exercise1.Genetics.Enums.ReplicationScheme;
import Exercise1.Genetics.Threads.RunGenerationsThread;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class GeneSet {

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

    public GeneSet(int genecnt, int genelen, int maxgenerations, double initrate, double acceptRate, int numberOfRuns, ReplicationScheme replicationScheme, CrossOverMethodType crossingOverMethod) {
        this.genecnt = genecnt;
        this.genelen = genelen;
        this.maxgenerations = maxgenerations;
        this.initrate = initrate;
        this.acceptRate = acceptRate;
        this.numberOfRuns = numberOfRuns;
        this.replicationScheme = replicationScheme;
        this.crossingOverMethod = crossingOverMethod;
    }

    private int[] runGeneration() throws InterruptedException {
        RunGenerationsThread[] threads = new RunGenerationsThread[numberOfRuns];
        int sum = 0;
        int maxValue = 0;
        for (int i = 0; i < numberOfRuns; i++) {
            threads[i] = new RunGenerationsThread(genecnt, genelen, maxgenerations, initrate, acceptRate, pc, pm, crossingOverMethod, replicationScheme);
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
        FileWriter fileWriter = new FileWriter("resultData.txt");
        for (double a = 0.0; a <= pcEnd - pcStart; a += pcStep) {
            pc = BigDecimal.valueOf(pcStart + a).setScale(5, RoundingMode.HALF_UP).doubleValue();
            for (double j = 0.0; j <= pmEnd - pmStart; j += pmStep) {
                pm = BigDecimal.valueOf(pmStart + j).setScale(5, RoundingMode.HALF_UP).doubleValue();
                writeFile(fileWriter, pc, pm);
            }
            try {
                fileWriter.write("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        fileWriter.close();
    }

    public void printGenerationResult(double pc, double pm) throws InterruptedException {
        this.pc = pc;
        this.pm = pm;
        int[] result = runGeneration();
        System.out.println("Mittel der Generationen: " + result[0]);
        System.out.println("Mutationsrate: " + pm);
        System.out.println("Rekombinationsrate: " + pc);
        System.out.println("Anzahl der Gene: " + genecnt);
        System.out.println("Länge der Gene: " + genelen);
        System.out.println("Initiationsrate: " + initrate);
        System.out.println("Crossovermethod: " + crossingOverMethod);
        System.out.println("Replicationscheme: " + replicationScheme);
        System.out.println("Accepted upper boder: " + (int) (genecnt * acceptRate));
        System.out.println("Highest value: " + result[1]);


    }

    private void writeFile(FileWriter fileWriter, double pc, double pm) throws InterruptedException {
        int[] result = runGeneration();
        int avgRuns = result[0];
        int maxValue = result[1];
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