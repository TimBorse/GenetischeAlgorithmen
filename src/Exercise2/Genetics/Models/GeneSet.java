package Exercise2.Genetics.Models;

import Exercise2.Genetics.Enums.Protection;
import Exercise2.Genetics.Enums.RecombinationType;
import Exercise2.Genetics.Enums.ReplicationScheme;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class GeneSet {

    private int mapSize = 10;
    private int cityCount;
    private int[][] citiesMap;
    public static double[][] distanceMap;
    private final int genecnt;
    private final int maxgenerations;
    private double pc;
    private double pm;
    private final double acceptRate;
    private final int numberOfRuns;
    private final ReplicationScheme replicationScheme;
    private final RecombinationType crossingOverMethod;
    private final Protection protection;

    public GeneSet(int genecnt, int maxgenerations, double acceptRate, int numberOfRuns, ReplicationScheme replicationScheme, RecombinationType crossingOverMethod, Protection protection) throws FileNotFoundException {
        this.genecnt = genecnt;
        this.maxgenerations = maxgenerations;
        this.acceptRate = acceptRate;
        this.numberOfRuns = numberOfRuns;
        this.replicationScheme = replicationScheme;
        this.crossingOverMethod = crossingOverMethod;
        this.protection = protection;
        generateDistanceMap();
    }

    private void generateDistanceMap() throws FileNotFoundException {
        citiesMap = new int[mapSize][mapSize];
        File cityFile = new File("cities/05-map-10x10-36border.txt");
        Scanner reader = new Scanner(cityFile);
        for(int i=0;i<mapSize;i++){
            String cities = reader.nextLine();
            if(cities.charAt(0) == ' ')
                cities = cities.replaceFirst("\\s+", "");
            String[] citiesInLine = cities.split("\\s+");
            for(int j = 0; j<citiesInLine.length; j++){
                if(!citiesInLine[j].equals("0") && !citiesInLine[j].equals("00"))
                    cityCount++;
                citiesMap[i][j] = Integer.parseInt(citiesInLine[j]);
            }
        }
        distanceMap = new double[cityCount][cityCount];
        for(int y = 0; y < cityCount; y++){
            for(int x = 0; x < cityCount; x++){
                int[] coordinatesA = searchIndices(x+1);
                int[] coordinatesB = searchIndices(y+1);
                int xA = coordinatesA[0];
                int yA = coordinatesA[1];
                int xB = coordinatesB[0];
                int yB = coordinatesB[1];
                double xDif = Math.abs(xA-xB);
                double yDif = Math.abs(yA-yB);
                double dist = Math.sqrt(xDif*xDif+yDif*yDif);
                distanceMap[y][x] = dist;
            }
        }
    }

    private int[] searchIndices(int city){
        for(int y = 0; y < mapSize; y++){
            for(int x = 0; x < mapSize; x++){
                if(citiesMap[y][x] == city){
                   int[] coordinates = {x,y};
                   return coordinates;
                }
            }
        }
        return null;
    }

}
