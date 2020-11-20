package Exercise1.Genetics.Threads;

import Exercise1.Genetics.Enums.CrossOverMethodType;
import Exercise1.Genetics.Enums.ReplicationScheme;
import Exercise1.Genetics.Gene;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class RunGenerationsThread extends Thread{

    public static long crossTime = 0;
    public static long mutateTime = 0;
    public static long replicateTime = 0;

    private final int genecnt;
    private final int genelen;
    private final int maxgenerations;
    private final double initrate;
    private final double acceptRate;
    private final double pc;
    private final double pm;
    private final CrossOverMethodType crossingOverMethod;
    private final ReplicationScheme replicationScheme;

    public int generationCount;
    public int maxValue;


    public RunGenerationsThread(int genecnt, int genelen, int maxgenerations, double initrate, double acceptRate, double pc, double pm, CrossOverMethodType crossingOverMethod, ReplicationScheme replicationScheme) {
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
    }

    public int getGenerationCount() {
        return generationCount;
    }

    public int getMaxValue() {
        return maxValue;
    }

    @Override
    public void run(){
        Gene[] genes = new Gene[genecnt];
        maxValue = 0;
        for (int j = 0; j < genecnt; j++) {
            genes[j] = new Gene(genelen, initrate);
        }

        while (genes[genecnt - 1].getFitness() <= (genecnt * acceptRate)) {
            mutateGenes(genes, pm);
            if (genesReachedDesiredFitness(genes))
                break;
            crossOver(genes, pc);
            if (genesReachedDesiredFitness(genes))
                break;
            replicateGenes(genes);
            generationCount++;
            if (generationCount >= maxgenerations)
                break;
        }
            maxValue = genes[genecnt - 1].getFitness();

    }

    private void crossOver(Gene[] genes, double pc) {
        long startTime = System.currentTimeMillis();
        switch (crossingOverMethod) {
            case RECOMBINATION:
                for (int i = 0; i < (genecnt * pc); i++) {
                    int position = ThreadLocalRandom.current().nextInt(0, genelen);
                    int geneIndex1 = ThreadLocalRandom.current().nextInt(0, genecnt);
                    int geneIndex2 = ThreadLocalRandom.current().nextInt(0, genecnt);
                    //crossTwoGenes(genes[geneIndex1], genes[geneIndex2], position);
                    crossTwoGenes(genes[geneIndex1], genes[geneIndex2], position);
                }
                break;
            default:
                throw new IllegalArgumentException();
        }
        long endTime = System.currentTimeMillis();
        crossTime += (endTime-startTime);

    }

    private void crossTwoGenesOld(Gene gene1, Gene gene2, int position) {
        int[] srcData1 = Arrays.copyOfRange(gene1.getData(),0,position);
        int[] srcData2 = Arrays.copyOfRange(gene2.getData(), 0, position);
        for (int i = 0; i < position; i++) {
            setGenePos(gene1, i, srcData2[i]);
            setGenePos(gene2, i, srcData1[i]);
        }
    }

    private void crossTwoGenes(Gene gene1, Gene gene2, int position){
        int[] tempArr = new int[genelen];
        System.arraycopy(gene1.getData(), 0, tempArr, 0, genelen);
        System.arraycopy(gene2.getData(), 0, gene1.getData(), 0, position);
        System.arraycopy(tempArr, 0, gene2.getData(), 0, position);
        int fitness1 = 0;
        int fitness2 = 0;
        int[] data1 = gene1.getData();
        int[] data2 = gene2.getData();
        for(int i = 0; i<position; i++){
            if(data1[i] == 1)
                fitness1++;
            if(data2[i] == 1)
                fitness2++;
        }
        gene1.setFitness(gene1.getFitness()+(fitness1-fitness2));
        gene2.setFitness(gene2.getFitness()+(fitness2-fitness1));
    }

    private void replicateGenes(Gene[] genes) {
        long startTime = System.currentTimeMillis();
        Arrays.sort(genes);
        switch (replicationScheme) {
            case DOUBLE_BEST_HALF:
                for (int i = 0; i < genecnt; i++) {
                    if (i < (genecnt / 2)) {
                        genes[i] = genes[(genecnt / 2) + i].clone();
                    }
                }
                break;
            default:
                throw new IllegalArgumentException();
        }
        long endTime = System.currentTimeMillis();
        replicateTime += (endTime-startTime);
    }

    private void mutateGenes(Gene[] genes, double pm){
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < (genelen * genecnt * pm); i++) {
            int geneIndex = ThreadLocalRandom.current().nextInt(0, genecnt);
            Gene geneNumber = genes[geneIndex];
            int pos = ThreadLocalRandom.current().nextInt(0, genelen);
            setGenePos(geneNumber, pos, -1);
        }
        long endTime = System.currentTimeMillis();
        mutateTime += (endTime - startTime);
    }

    private void setGenePos(Gene gene, int pos, int value) {
            gene.setPos(pos, value);
    }

    private boolean genesReachedDesiredFitness(Gene[] genes){
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


