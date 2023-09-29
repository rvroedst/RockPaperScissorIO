package nl.openvalue.io;

import nl.openvalue.io.model.Genome;
import nl.openvalue.io.model.Pool;
import nl.openvalue.io.model.Species;
import nl.openvalue.rps.Move;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static nl.openvalue.io.FileIO.loadFile;
import static nl.openvalue.io.FileIO.writeFile;
import static nl.openvalue.io.GameUtils.evaluateCurrent;
import static nl.openvalue.io.GameUtils.fitnessAlreadyMeasured;
import static nl.openvalue.io.Network.generateNetwork;
import static nl.openvalue.io.model.Pool.nextGenome;

public class Main {

    private static final Pool pool = Pool.getInstance();
//    private static int timeout;
    public static List<List<Integer>> playerMoves = new ArrayList<>();
    public static List<List<Integer>> aiMoves = new ArrayList<>();
    public static int score = 0;

    public static void main(String[] args) {
        initializeRun();
        nextRound();
    }

    public static Move nextRound() {
            Move currentMove = evaluateCurrent();

            Species species = pool.getSpecies().get(pool.getCurrentSpecies() - 1);
            Genome genome = species.getGenomes().get(pool.getCurrentGenome() - 1);
                int fitness = score;
                if (fitness == 0) {
                    fitness = -1;
                }
               genome.setFitness(fitness);

                if (fitness > pool.getMaxFitness()) {
                    pool.setMaxFitness(fitness);
                    try {
                        writeFile();
                    } catch (Exception e) {
                        printError(e, "writing");
                    }
                }
                System.out.println("Gen " + pool.getGeneration() + " species " + pool.getCurrentSpecies() + " genome " + pool.getCurrentGenome() + " fitness: " + fitness);
                System.out.println("Max fitness: " + pool.getCurrentGenome() + " fitness: " + fitness);
                pool.setCurrentSpecies(1);
                pool.setCurrentGenome(1);
                while (fitnessAlreadyMeasured()) {
                    nextGenome();
                }

            int measured = 0;
            int total = 0;

            for (Species poolSpecies : pool.getSpecies()) {
                for (Genome poolGenome : poolSpecies.getGenomes()) {
                    total++;
                    if (poolGenome.getFitness() != 0) {
                        measured++;
                    }
                }
            }

            // Assuming hideBanner is a boolean variable or method that checks the banner's state
            System.out.println("Round: " + pool.getCurrentRound());
            System.out.println("Gen " + pool.getGeneration() +
                    " species " + pool.getCurrentSpecies() +
                    " genome " + pool.getCurrentGenome() +
                    " (" + Math.floor((double) measured / total * 100) + "%)");

            System.out.println("Fitness: " +
                    Math.floor(((double) score / pool.getCurrentRound()) * score));

            System.out.println("Max Fitness: " + Math.floor(pool.getMaxFitness()));

            System.out.println( "Playing: " + currentMove );
            System.out.println();
            pool.setCurrentRound(pool.getCurrentRound() + 1);
            return currentMove;
    }

    public static Move initializeRun() {
        File file = new File(Config.FILENAME);
        if (file.exists()) {
            try {
                System.out.println("loading file");
                loadFile();  // Your existing method to load the saved state
            } catch (IOException e) {
                printError(e, "loading");  // Assuming you have a method to handle exceptions
            }
        } else {
            // Handle the scenario when the file doesn't exist
            // This could mean setting default values, initializing a new run, etc.
        }
        pool.populatePool();
        pool.setCurrentRound(1);
//        timeout = Config.TIMEOUT_CONSTANT;  // Assuming TIMEOUT_CONSTANT is in a Config class

        Species species = pool.species.get(pool.currentSpecies - 1);
        Genome genome = species.getGenomes().get(pool.currentGenome - 1);
        generateNetwork(genome);
        return evaluateCurrent();
    }

    public static void printError(Exception e, String action){
        System.out.println("Something went wrong with " + action + " the file: ");
        System.out.println(e);
        System.out.println("-----------------------");
    }
}