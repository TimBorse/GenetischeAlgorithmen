package Exercise2.Genetics.Threads;

import Exercise2.Genetics.Enums.RecombinationType;
import Exercise2.Genetics.Enums.Protection;
import Exercise2.Genetics.Enums.ReplicationScheme;
import Exercise2.Genetics.Models.Gene;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class RunGenerationsThread extends Thread {

    private final int genecnt;
    private final int cityCount;
    private final int maxgenerations;
    private final double acceptRate;
    private final RecombinationType crossingOverMethod;
    private final ReplicationScheme replicationScheme;
    private final Protection protection;

    public int generationCount;
    public double fastesPath;


    public RunGenerationsThread(int genecnt, int cityCount, int maxgenerations, double acceptRate, ReplicationScheme replicationScheme, RecombinationType crossingOverMethod, Protection protection) throws FileNotFoundException {
        super();
        this.genecnt = genecnt;
        this.maxgenerations = maxgenerations;
        this.acceptRate = acceptRate;
        this.replicationScheme = replicationScheme;
        this.crossingOverMethod = crossingOverMethod;
        this.protection = protection;
        this.cityCount = cityCount;
    }

    public int getGenerationCount() {
        return generationCount;
    }

    public int getMaxValue() {
        return fastesPath;
    }

    @Override
    public void run() {
        Gene[] genes = new Gene[genecnt];
        fastesPath = 0;
        //Initializes new genes
        for (int j = 0; j < genecnt; j++) {
            genes[j] = new Gene(cityCount);
        }

        //Standard procedure of the simulation
        while (genes[genecnt - 1].fitness() > cityCount) {
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
        fastesPath = genes[genecnt - 1].getFitness();

    }

    private void crossOver(Gene[] genes, double pc) {
        switch (crossingOverMethod) {
            case GREEDY_CROSSOVER:
                //ToDo: Edit to Greedy!
                Gene[] newGenes = new Gene[genecnt];
                for (int i = 0; i < (genecnt * pc); i++) {
                    int geneIndex1 = ThreadLocalRandom.current().nextInt(0, genes.length);
                    int geneIndex2 = ThreadLocalRandom.current().nextInt(0, genes.length);
                    Gene gene1 = genes[geneIndex1];
                    Gene gene2 = genes[geneIndex2];
                    int[] data1 = gene1.getData();
                    int[] data2 = gene2.getData();
                    int[] newData = new int[cityCount];
                    for(int j=0; j<cityCount;j++){
                        int from = data1[j];

                    }
                }
                break;
            default:
                throw new IllegalArgumentException();
        }

    }

    private void replicateGenes(Gene[] genes) {
        Arrays.sort(genes);
        switch (replicationScheme) {
            case DOUBLE_BEST_HALF:
                Gene bestGene = genes[0];
                Gene secondBestGene = genes[1];
                Gene[] newGenes = new Gene[genecnt];
                for (int i = 0; i < genecnt; i++) {
                    if (i < (genecnt / 4)) {
                        newGenes[i] = bestGene.clone();
                    }else if(i < (genecnt/2))
                        newGenes[i] = secondBestGene.clone();
                    else{
                       int randomIndex = ThreadLocalRandom.current().nextInt(genecnt);
                       newGenes[i] = genes[randomIndex].clone();
                    }
                }
                System.arraycopy(newGenes, 0, genes, 0, genecnt);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }


    private void mutateGenes(Gene[] genes, double pm) {
        for (int i = 0; i < (genecnt * cityCount * pm); i++) {
            int geneIndex = ThreadLocalRandom.current().nextInt(0, genes.length);
            int pos1 = ThreadLocalRandom.current().nextInt(0, cityCount);
            int pos2 = ThreadLocalRandom.current().nextInt(0, cityCount);
            swapCityPositions(genes[geneIndex], pos1, pos2);
        }
    }
    
    private void swapCityPositions(Gene gene, int pos1, int pos2){
        int temp = gene.getData()[pos1];
        gene.setPos(pos1, gene.getData()[pos2]);
        gene.setPos(pos2, temp);
    }

    //Checks if a gene has reached the desired fitness
    private boolean genesReachedDesiredFitness(Gene[] genes) {
        Arrays.sort(genes);
        if (genes[genecnt - 1].getFitness() <= cityCount) {
            if (genes[genecnt - 1].getFitness() > fastesPath) {
                fastesPath = genes[genecnt - 1].getFitness();
            }
            return true;
        }
        return false;
    }
}



