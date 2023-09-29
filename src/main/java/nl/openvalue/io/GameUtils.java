package nl.openvalue.io;

import nl.openvalue.io.model.Genome;
import nl.openvalue.io.model.Pool;
import nl.openvalue.io.model.Species;
import nl.openvalue.rps.Move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static nl.openvalue.io.Config.INPUT_SIZE;
import static nl.openvalue.io.FileIO.writeFile;
import static nl.openvalue.io.Main.aiMoves;
import static nl.openvalue.io.Main.printError;
import static nl.openvalue.io.Network.evaluateNetwork;
import static nl.openvalue.io.service.SpeciesService.addToSpecies;
import static nl.openvalue.io.service.SpeciesService.breedChild;
import static nl.openvalue.io.service.SpeciesService.cullSpecies;
import static nl.openvalue.io.service.SpeciesService.removeStaleSpecies;
import static nl.openvalue.io.service.SpeciesService.removeWeakSpecies;

public class GameUtils {

    public static Pool pool = Pool.getInstance();

    static Random random = new Random();

    public static void rankGlobally() {
        List<Genome> global = new ArrayList<>();
        for (Species species : Pool.getInstance().species) {
            global.addAll(species.getGenomes());
        }
        global.sort(Comparator.comparingDouble(g -> g.fitness));

        for (int g = 0; g < global.size(); g++) {
            global.get(g).globalRank = g + 1;  // +1 because Java uses 0-based indexing
        }
    }

    public static void calculateAverageFitness(Species species) {
        double total = 0;
        for (Genome genome : species.getGenomes()) {
            total += genome.globalRank;
        }
        species.setAverageFitness(total / species.getGenomes().size());
    }

    public static double totalAverageFitness() {
        double total = 0;
        for (Species species : Pool.getInstance().species) {
            total += species.getAverageFitness();
        }
        return total;
    }

    public static boolean fitnessAlreadyMeasured() {
        Species species = pool.species.get(pool.currentSpecies - 1);
        Genome genome = species.getGenomes().get(pool.currentGenome - 1);

        return genome.fitness != 0;
    }

    public static void newGeneration() {
        cullSpecies(false);  // Cull the bottom half of each species
        rankGlobally();
        removeStaleSpecies();
        rankGlobally();
        for (Species species : pool.species) {
            calculateAverageFitness(species);
        }
        removeWeakSpecies();
        double sum = totalAverageFitness();
        List<Genome> children = new ArrayList<>();
        for (Species species : pool.species) {
            int breed = (int) Math.floor(species.getAverageFitness() / sum * Config.POPULATION) - 1;
            for (int i = 0; i < breed; i++) {
                children.add(breedChild(species));
            }
        }
        cullSpecies(true);  // Cull all but the top member of each species
        while (children.size() + pool.species.size() < Config.POPULATION) {
            Species species = pool.species.get(random.nextInt(pool.species.size()));
            children.add(breedChild(species));
        }
        for (Genome child : children) {
            addToSpecies(child);
        }

        pool.generation++;

        // Assuming a method writeFile(String filename) exists
        try {
            writeFile();
//            writeFile("backup." + pool.generation + ".pool");  // Assuming saveLoadFile is a global variable or similar
        }
        catch(Exception e){
            printError(e, "writing");
        }
    }

    public static Move evaluateCurrent() {
        Species species = pool.species.get(pool.currentSpecies - 1);
        Genome genome = species.getGenomes().get(pool.currentGenome - 1);

        List<List<Integer>> inputs = getInputs();  // Assuming getInputs() returns a List<Double>
        return evaluateNetwork(genome.network, inputs).orElse(null);
    }

    public static List<List<Integer>> getInputs() {
        List<List<Integer>> inputs = new ArrayList<>();

        // Add player's last 5 moves to inputs
//        for (int i = 0; i < INPUT_SIZE; i++) {
//            if (i < playerMoves.size()) {
//                inputs.addAll(Arrays.asList(playerMoves.get(playerMoves.size() - 1 - i)));
//            } else {
//                inputs.add(Arrays.asList(-1, -1, -1));  // -1 signifies no move, useful for the start of the game
//            }
//        }

        // Add opponent's last 5 moves to inputs
        for (int i = 0; i < INPUT_SIZE; i++) {
            if (i < aiMoves.size()) {
                inputs.addAll(Arrays.asList(aiMoves.get(aiMoves.size() - 1 - i)));
            } else {
                inputs.add(Arrays.asList(-1, -1, -1));  // -1 signifies no move, useful for the start of the game
            }
        }

        return inputs;
    }

    public static List<Integer> oneHotEncoding(Move move){
        List<Integer> encoding = new ArrayList<>(Collections.nCopies(Move.values().length, 0));
        encoding.set(move.ordinal(), 1);
        return encoding;
    }
}
