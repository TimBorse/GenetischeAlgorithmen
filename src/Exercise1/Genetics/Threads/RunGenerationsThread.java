package Exercise1.Genetics.Threads;

import Exercise1.Genetics.Enums.CrossOverMethodType;
import Exercise1.Genetics.Enums.Protection;
import Exercise1.Genetics.Enums.ReplicationScheme;
import Exercise1.Genetics.Gene;

import java.util.Arrays;
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

        while (genes[genecnt - 1].getFitness() <= (genecnt * acceptRate)) {
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
                    int position = ThreadLocalRandom.current().nextInt(0, genes.length);
                    int geneIndex1 = ThreadLocalRandom.current().nextInt(0, genes.length);
                    int geneIndex2 = ThreadLocalRandom.current().nextInt(0, genes.length);
                    //crossTwoGenes(genes[geneIndex1], genes[geneIndex2], position);
                    crossTwoGenes(genes[geneIndex1], genes[geneIndex2], position);
                }
                break;
            default:
                throw new IllegalArgumentException();
        }

    }

    private void crossTwoGenesOld(Gene gene1, Gene gene2, int position) {
        int[] srcData1 = new int[position];
        System.arraycopy(gene1.getData(), 0, srcData1, 0, position);
        //Arrays.copyOfRange(gene1.getData(),0,position);
        int[] srcData2 = new int[position];
        System.arraycopy(gene2.getData(), 0, srcData2, 0, position);
        //Arrays.copyOfRange(gene2.getData(), 0, position);
        for (int i = 0; i < position; i++) {
            setGenePos(gene1, i, srcData2[i]);
            setGenePos(gene2, i, srcData1[i]);
        }
    }

    private void crossTwoGenes(Gene gene1, Gene gene2, int position) {
        int[] tempArr = new int[genelen];
        int[] data1 = gene1.getData();
        int[] data2 = gene2.getData();
        System.arraycopy(data1, 0, tempArr, 0, genelen);
        System.arraycopy(data2, 0, data1, 0, position);
        System.arraycopy(tempArr, 0, data2, 0, position);
        int fitness1 = 0;
        int fitness2 = 0;
        for (int i = 0; i < position; i++) {
            if (data1[i] == 1)
                fitness1++;
            if (data2[i] == 1)
                fitness2++;
        }
        gene1.setFitness(gene1.getFitness() + (fitness1 - fitness2));
        gene2.setFitness(gene2.getFitness() + (fitness2 - fitness1));
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
                int s = 2;
                double decimalN = (double) n;
                double decimalS = (double) s;
                double[] psKum = new double[n];
                double[] randomNumbers = new double[n/2];
                Gene[] newGenes = new Gene[n];
                for (int r = 0; r < n; r++) {
                    if(r<n/2)
                        randomNumbers[r] = ThreadLocalRandom.current().nextDouble();
                    double decimalR = (double) r;
                    double ps = ((2 - decimalS) / (decimalN)) + ((2 * decimalR * (decimalS - 1)) / (decimalN * (decimalN - 1)));
                    if (r > 0)
                        psKum[r] = psKum[r - 1] + ps;
                    else {
                        psKum[r] = ps;
                    }
                }
                int counter = 0;
                for (double number : randomNumbers) {
                    int index = getIndex(n / 2, n - 1, 0, number, psKum);
                    newGenes[counter] = genes[index];
                    newGenes[counter+1] = genes[index].clone();
                    counter += 2;
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
        }else{
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
        if (genes[genecnt - 1].getFitness() >= (genecnt * acceptRate)) {
            if (genes[genecnt - 1].getFitness() > maxValue) {
                maxValue = genes[genecnt - 1].getFitness();
            }
            return true;
        }
        return false;
    }
}


