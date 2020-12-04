package Exercise1.Genetics.Threads;

import Exercise1.Genetics.Enums.CrossOverMethodType;
import Exercise1.Genetics.Enums.Protection;
import Exercise1.Genetics.Enums.ReplicationScheme;
import Exercise1.Genetics.Models.Gene;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class RunGenerationsThread extends Thread {

    private final int genecnt;
    private final int genelen;
    private final int maxgenerations;
    private final double initrate;
    private final double acceptRate;
    private final double pc;
    private final double pm;
    private final CrossOverMethodType crossingOverMethod;
    private final ReplicationScheme replicationScheme;
    private final Protection protection;
    private int rankBasedSelectionParameter_s;

    public int generationCount;
    public int maxValue;


    public RunGenerationsThread(int genecnt, int genelen, int maxgenerations, double initrate, double acceptRate, double pc, double pm, CrossOverMethodType crossingOverMethod, ReplicationScheme replicationScheme, Protection protection) {
        super();
        this.genecnt = genecnt;
        this.genelen = genelen;
        this.maxgenerations = maxgenerations;
        this.initrate = initrate;
        this.acceptRate = acceptRate;
        this.pc = pc;
        this.pm = pm;
        this.crossingOverMethod = crossingOverMethod;
        this.replicationScheme = replicationScheme;
        this.protection = protection;
        this.rankBasedSelectionParameter_s = 2;
    }

    public void setRankBasedSelectionParameter_s(int s){
        this.rankBasedSelectionParameter_s = s;
    }


    public int getGenerationCount() {
        return generationCount;
    }

    public int getMaxValue() {
        return maxValue;
    }

    @Override
    public void run() {
        Gene[] genes = new Gene[genecnt];
        maxValue = 0;
        for (int j = 0; j < genecnt; j++) {
            genes[j] = new Gene(genelen, initrate);
        }

        while (genes[genecnt - 1].getFitness() <= (genelen * acceptRate)) {
            Gene[] selectedGenes;
            switch (protection) {
                case NONE:
                    selectedGenes = genes;
                    break;
                case BEST:
                    selectedGenes = new Gene[genecnt - 1];
                    System.arraycopy(genes, 0, selectedGenes, 0, genecnt - 1);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + protection);
            }
            mutateGenes(selectedGenes, pm);
            if (genesReachedDesiredFitness(genes)) {
                break;
            }
            crossOver(selectedGenes, pc);
            if (genesReachedDesiredFitness(genes)) {
                break;
            }
            replicateGenes(genes);
            generationCount++;
            if (generationCount >= maxgenerations)
                break;
        }
        maxValue = genes[genecnt - 1].getFitness();

    }

    private void crossOver(Gene[] genes, double pc) {
        switch (crossingOverMethod) {
            case RECOMBINATION:
                for (int i = 0; i < (genes.length * pc); i++) {
                    int position = ThreadLocalRandom.current().nextInt(0, genelen);
                    int geneIndex1 = ThreadLocalRandom.current().nextInt(0, genes.length);
                    int geneIndex2 = ThreadLocalRandom.current().nextInt(0, genes.length);
                    crossTwoGenes(genes[geneIndex1], genes[geneIndex2], position);
                }
                break;
            case TRANSPOSITION:
                for (int i = 0; i < (genes.length * pc); i++) {
                    int startPosition = ThreadLocalRandom.current().nextInt(0, genes.length);
                    int endPosition = ThreadLocalRandom.current().nextInt(0, genes.length);
                    if (startPosition > endPosition) {
                        int temp = startPosition;
                        startPosition = endPosition;
                        endPosition = temp;
                    }
                    int geneIndex1 = ThreadLocalRandom.current().nextInt(0, genes.length);
                    int geneIndex2 = ThreadLocalRandom.current().nextInt(0, genes.length);
                    transposeTwoGenes(genes[geneIndex1], genes[geneIndex2], startPosition, endPosition);
                }
                break;
            case FRONTREAR:
                for (int i = 0; i < (genes.length * pc); i++) {
                    int amount = ThreadLocalRandom.current().nextInt(0, genes.length);
                    int geneIndex1 = ThreadLocalRandom.current().nextInt(0, genes.length);
                    int geneIndex2 = ThreadLocalRandom.current().nextInt(0, genes.length);
                    frontRearTwoGenes(genes[geneIndex1], genes[geneIndex2], amount);
                }
                break;
            default:
                throw new IllegalArgumentException();
        }

    }

    private void crossTwoGenes(Gene gene1, Gene gene2, int position) {
        int length = genelen - position;
        int[] tempArr = new int[genelen];
        int[] data1 = gene1.getData();
        int[] data2 = gene2.getData();
        System.arraycopy(data1, position, tempArr, position, length);
        System.arraycopy(data2, position, data1, position, length);
        System.arraycopy(tempArr, position, data2, position, length);
        int fitness1 = 0;
        int fitness2 = 0;
        for (int i = position; i < data1.length; i++) {
            if (data1[i] == 1)
                fitness1++;
            if (data2[i] == 1)
                fitness2++;
        }
        gene1.setFitness(gene1.getFitness() + (fitness1 - fitness2));
        gene2.setFitness(gene2.getFitness() + (fitness2 - fitness1));
    }

    private void transposeTwoGenes(Gene gene1, Gene gene2, int startPosition, int endPosition) {
        int length = endPosition - startPosition;
        int[] data1 = gene1.getData();
        int[] data2 = gene2.getData();
        int fitnessOld = 0;
        for (int i = startPosition; i < endPosition; i++) {
            if (data1[i] == 1)
                fitnessOld++;
        }
        System.arraycopy(data2, startPosition, data1, startPosition, length);
        int fitnessNew = 0;
        for (int i = startPosition; i < endPosition; i++) {
            if (data1[i] == 1)
                fitnessNew++;
        }
        gene1.setFitness(gene1.getFitness() + (fitnessNew - fitnessOld));
    }

    private void frontRearTwoGenes(Gene gene1, Gene gene2, int amount){
        int[] tempArr = new int[genelen];
        int[] data1 = gene1.getData();
        int[] data2 = gene2.getData();
        System.arraycopy(data1, 0, tempArr, 0, genelen);
        System.arraycopy(data2, genelen-amount, data1, 0, amount);
        System.arraycopy(tempArr, genelen-amount, data2, 0, amount);
        gene1.setFitness(gene1.fitness());
        gene2.setFitness(gene2.fitness());
    }

    private void replicateGenes(Gene[] genes) {
        Arrays.sort(genes);
        switch (replicationScheme) {
            case DOUBLE_BEST_HALF:
                for (int i = 0; i < genecnt; i++) {
                    if (i < (genecnt / 2)) {
                        genes[i] = genes[(genecnt / 2) + i].clone();
                    }
                }
                break;
            case RANK_BASED_SELECTION:
                int n = genes.length;
                int s = rankBasedSelectionParameter_s;
                double[] psKum = new double[n];
                double[] randomNumbers = new double[n];
                Gene[] newGenes = new Gene[n];
                HashMap<Integer, Integer> indexCount = new HashMap<>();
                for (int r = 0; r < n; r++) {
                    indexCount.put(r, 0);
                    randomNumbers[r] = ThreadLocalRandom.current().nextDouble();
                    double ps = ((2 - (double) s) / ((double) n)) + ((2 * (double) r * ((double) s - 1)) / ((double) n * ((double) n - 1)));
                    if (r > 0)
                        psKum[r] = psKum[r - 1] + ps;
                    else {
                        psKum[r] = ps;
                    }
                }
                int counter = 0;
                for (double number : randomNumbers) {
                    int index = getIndex(n / 2, n - 1, 0, number, psKum);
                    indexCount.replace(index, indexCount.get(index) + 1);
                    if (indexCount.get(index) > 1) {
                        newGenes[counter] = genes[index].clone();
                    } else {
                        newGenes[counter] = genes[index];
                    }
                    counter++;
                }
                System.arraycopy(newGenes, 0, genes, 0, n);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }


    private int getIndex(int index, int upperBound, int lowerBound, double number, double[] data) {

        if (index == upperBound)
            return index;
        int dif = (upperBound - lowerBound) / 4;
        if (dif < 1)
            dif = 1;
        if ((data[index] < number && data[index + 1] > number) || data[index] == number) {
            return index;
        } else if (data[index] > number) {
            return getIndex((index - dif), index, lowerBound, number, data);
        } else if (data[index] < number) {
            return getIndex((index + dif), upperBound, index, number, data);
        } else {
            return index;
        }


    }

    private void mutateGenes(Gene[] genes, double pm) {
        for (int i = 0; i < (genelen * genes.length * pm); i++) {
            int geneIndex = ThreadLocalRandom.current().nextInt(0, genes.length);
            Gene geneNumber = genes[geneIndex];
            int pos = ThreadLocalRandom.current().nextInt(0, genelen);
            setGenePos(geneNumber, pos, -1);
        }
    }

    private void setGenePos(Gene gene, int pos, int value) {
        gene.setPos(pos, value);
    }

    private boolean genesReachedDesiredFitness(Gene[] genes) {
        Arrays.sort(genes);
        if (genes[genecnt - 1].getFitness() >= (genelen * acceptRate)) {
            if (genes[genecnt - 1].getFitness() > maxValue) {
                maxValue = genes[genecnt - 1].getFitness();
            }
            return true;
        }
        return false;
    }
}


