package Exercise1.Genetics;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Gene implements Comparable<Gene> {

    private int[] data;
    private int fitness;
    public static long fitnessTime = 0;

    public Gene(int length, double initrate) {
        fitness = 0;
        data = new int[length];
        for (int i = 0; i < data.length; i++) {
            if (ThreadLocalRandom.current().nextFloat() <= initrate) {
                data[i] = 1;
                fitness++;
            } else {
                data[i] = 0;
            }
        }

    }

    private Gene(int[] data, int fitness) {
        this.data = new int[data.length];
        this.fitness = fitness;
        initializeCopyData(data);
    }

    private void initializeCopyData(int[] data) {
        System.arraycopy(data, 0, this.data, 0, data.length);
    }

    public Gene clone() {
        return new Gene(this.getData(), this.fitness);
    }

    public void setPos(int pos, int value) {
        if (value == -1) {
            if (data[pos] == 0){
                data[pos] = 1;
                fitness++;
            }
            else{
                data[pos] = 0;
                fitness--;
            }

        } else if(value == 0 ) {
            if (data[pos] == 1){
                data[pos] = value;
                fitness--;
            }
        }else if(value == 1){
            if(data[pos] == 0){
                data[pos] = value;
                fitness++;
            }
        }else
            throw new IllegalArgumentException();
    }

    public int getFitness() {
        return fitness;
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    public int fitness() {
        long startTime = System.currentTimeMillis();
        int fitness = 0;
        for (int date : data) {
            fitness += date;
        }
        long endTime = System.currentTimeMillis();
        fitnessTime += (endTime-startTime);
        return fitness;
    }

    public int[] getData() {
        return data;
    }

    public void setData(int[] data){
        this.data = data;
    }


    @Override
    public int compareTo(Gene gene) {
        return Integer.compare(fitness, gene.getFitness());
    }
}
