package Exercise2.Genetics.Threads;

import Exercise2.Genetics.Enums.Protection;
import Exercise2.Genetics.Enums.RecombinationType;
import Exercise2.Genetics.Enums.ReplicationScheme;
import Exercise2.Genetics.Models.Gene;
import Exercise2.Genetics.Models.GeneSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class RunGenerationsThread extends Thread {

    private final int genecnt;
    private final int cityCount;
    private final int maxgenerations;
    private final double acceptedFitness;
    private final double pc;
    private final double pm;
    private final RecombinationType crossingOverMethod;
    private final ReplicationScheme replicationScheme;
    private final Protection protection;

    public int generationCount;
    public double bestFitness;


    public RunGenerationsThread(int genecnt, int cityCount, int maxgenerations, double acceptedFitness, double pc, double pm, ReplicationScheme replicationScheme, RecombinationType crossingOverMethod, Protection protection) {
        super();
        this.genecnt = genecnt;
        this.maxgenerations = maxgenerations;
        this.acceptedFitness = acceptedFitness;
        this.replicationScheme = replicationScheme;
        this.crossingOverMethod = crossingOverMethod;
        this.protection = protection;
        this.cityCount = cityCount;
        this.pc = pc;
        this.pm = pm;
    }

    public int getGenerationCount() {
        return generationCount;
    }

    public double getBestFitness() {
        return bestFitness;
    }

    @Override
    public void run() {
        Gene[] genes = new Gene[genecnt];
        bestFitness = Double.MAX_VALUE;
        //Initializes new genes
        for (int j = 0; j < genecnt; j++) {
            genes[j] = new Gene(cityCount);
        }

        //Standard procedure of the simulation
        while (!genesReachedDesiredFitness(genes)) {
            Gene[] selectedGenes;
            switch (protection) {
                case NONE:
                    selectedGenes = genes;
                    break;
                case BEST:
                    selectedGenes = new Gene[genecnt - 1];
                    for (int i = 0; i < genecnt - 1; i++) {
                        selectedGenes[i] = genes[i+1];
                    }
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
        Arrays.sort(genes);
        if (GeneSet.bestGene != null && genes[0].getFitness() < GeneSet.bestGene.getFitness()) {
            GeneSet.bestGene = genes[0];
        }else if(GeneSet.bestGene == null){
            GeneSet.bestGene = genes[0];
        }
        if (genes[0].getFitness() < bestFitness) {
            bestFitness = genes[0].getFitness();
        }

    }

    private void crossOver(Gene[] genes, double pc) {
        switch (crossingOverMethod) {
            case GREEDY_CROSSOVER:
                Gene[] oldGenes = new Gene[genes.length];
                System.arraycopy(genes, 0, oldGenes, 0, genes.length);
                for (int i = 0; i < (int) (genecnt * pc); i++) {
                    ArrayList<Integer> usedCities = new ArrayList<>();
                    ArrayList<Integer> unusedCities = new ArrayList<>();
                    for (int k = 1; k <= cityCount; k++) {
                        unusedCities.add(k);
                    }
                    int geneIndex1 = ThreadLocalRandom.current().nextInt(0, genes.length);
                    int geneIndex2 = ThreadLocalRandom.current().nextInt(0, genes.length);
                    Gene gene1 = genes[geneIndex1];
                    Gene gene2 = genes[geneIndex2];
                    int[] data1 = gene1.getData();
                    int[] data2 = gene2.getData();
                    int[] newData = new int[cityCount];
                    newData[0] = data1[0];
                    usedCities.add(data1[0]);
                    unusedCities.remove(unusedCities.indexOf(data1[0]));
                    for (int j = 0; j < (cityCount - 1); j++) {
                        int index1 = getIndexOf(data1, newData[j]);
                        int index2 = getIndexOf(data2, newData[j]);
                        int nextValue1;
                        int nextValue2;
                        if (index1 < cityCount - 1)
                            nextValue1 = data1[index1 + 1];
                        else {
                            nextValue1 = data1[0];
                        }
                        if (index2 < cityCount - 1)
                            nextValue2 = data2[index2 + 1];
                        else {
                            nextValue2 = data2[0];
                        }
                        double dist1 = GeneSet.distanceMap[data1[index1] - 1][nextValue1 - 1];
                        double dist2 = GeneSet.distanceMap[data2[index2] - 1][nextValue2 - 1];
                        if (usedCities.contains(nextValue1) && usedCities.contains(nextValue2)) {
                            int rdm = ThreadLocalRandom.current().nextInt(unusedCities.size());
                            newData[j + 1] = unusedCities.get(rdm);
                            usedCities.add(unusedCities.get(rdm));
                            unusedCities.remove(rdm);
                        } else if (usedCities.contains(nextValue1)) {
                            newData[j + 1] = nextValue2;
                            usedCities.add(nextValue2);
                            unusedCities.remove(unusedCities.indexOf(nextValue2));
                        } else if (usedCities.contains(nextValue2)) {
                            newData[j + 1] = nextValue1;
                            usedCities.add(nextValue1);
                            unusedCities.remove(unusedCities.indexOf(nextValue1));
                        } else if (dist1 < dist2) {
                            newData[j + 1] = nextValue1;
                            usedCities.add(nextValue1);
                            unusedCities.remove(unusedCities.indexOf(nextValue1));
                        } else {
                            newData[j + 1] = nextValue2;
                            usedCities.add(nextValue2);
                            unusedCities.remove(unusedCities.indexOf(nextValue2));
                        }
                    }
                    genes[i].setData(newData);

                }

                for (int i = (int) (genecnt * pc); i < genes.length; i++) {
                    genes[i].setData(oldGenes[(i - (int) (genecnt * pc))].getData());
                }
                break;
            default:
                throw new IllegalArgumentException();
        }

    }

    private int getIndexOf(int[] data, int value) {
        for (int i = 0; i < data.length; i++) {
            if (data[i] == value)
                return i;
        }
        return -1;
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
                    } else if (i < (genecnt / 2))
                        newGenes[i] = secondBestGene.clone();
                    else {
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

    private void swapCityPositions(Gene gene, int pos1, int pos2) {
        int temp = gene.getData()[pos1];
        gene.setPos(pos1, gene.getData()[pos2]);
        gene.setPos(pos2, temp);
    }

    //Checks if a gene has reached the desired fitness
    private boolean genesReachedDesiredFitness(Gene[] genes) {
        Arrays.sort(genes);
        if (genes[0].getFitness() < bestFitness) {
            bestFitness = genes[0].getFitness();
        }
        return genes[0].getFitness() <= acceptedFitness;
    }
}



