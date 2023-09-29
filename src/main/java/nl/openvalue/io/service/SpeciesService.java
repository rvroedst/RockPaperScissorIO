package nl.openvalue.io.service;

import nl.openvalue.io.Config;
import nl.openvalue.io.model.Genome;
import nl.openvalue.io.model.Pool;
import nl.openvalue.io.model.Species;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static nl.openvalue.io.GameUtils.totalAverageFitness;
import static nl.openvalue.io.model.Genome.copyGenome;
import static nl.openvalue.io.model.Genome.crossover;
import static nl.openvalue.io.model.Genome.mutate;
import static nl.openvalue.io.model.Genome.sameSpecies;

public class SpeciesService {

    private static final Pool pool = Pool.getInstance();
    private static final Random random = new Random();

    public static void cullSpecies(boolean cutToOne) {
        for (Species species : Pool.getInstance().species) {
            List<Genome> genomes = species.getGenomes();
            genomes.sort(Comparator.comparingDouble(g -> -g.fitness)); // Sort in descending order

            int remaining = (int) Math.ceil(genomes.size() / 2.0);
            if (cutToOne) {
                remaining = 1;
            }
            while (species.getGenomes().size() > remaining) {
                species.getGenomes().remove(genomes.size() - 1);  // Remove the last element
            }
        }
    }

    public static Genome breedChild(Species species) {
        Genome child;
        List<Genome> genomes = species.getGenomes();
        if (random.nextDouble() < Config.CROSSOVER_CHANCE) {
            Genome g1 = genomes.get(random.nextInt(genomes.size()));
            Genome g2 = genomes.get(random.nextInt(genomes.size()));
            child = crossover(g1, g2);
        } else {
            Genome g = genomes.get(random.nextInt(genomes.size()));
            child = copyGenome(g);
        }

        mutate(child);

        return child;
    }

    public static void removeStaleSpecies() {
        List<Species> survived = new ArrayList<>();

        for (Species species : pool.species) {
            List<Genome> genomes = species.getGenomes();
            genomes.sort(Comparator.comparingDouble(g -> -g.fitness)); // Sort in descending order

            if (genomes.get(0).fitness > species.getTopFitness()) {
                species.setTopFitness(genomes.get(0).fitness);
                species.setStaleness(0);
            } else {
                species.incrementStaleness();
            }
            if (species.getStaleness() < Config.STALE_SPECIES || species.getTopFitness() >= pool.maxFitness) {
                survived.add(species);
            }
        }

        pool.species = survived;
    }

    public static void removeWeakSpecies() {
        List<Species> survived = new ArrayList<>();

        double sum = totalAverageFitness();
        for (Species species : pool.species) {
            int breed = (int) Math.floor(species.getAverageFitness() / sum * Config.POPULATION);
            if (breed >= 1) {
                survived.add(species);
            }
        }
        pool.species = survived;
    }

    public static void addToSpecies(Genome child) {
        boolean foundSpecies = false;
        for (Species species : pool.species) {
            if (!foundSpecies && sameSpecies(child, species.getGenomes().get(0))) {
                species.addToGenomes(child);
                foundSpecies = true;
            }
        }

        if (!foundSpecies) {
            Species childSpecies = new Species();
            childSpecies.addToGenomes(child);
            pool.species.add(childSpecies);
        }
    }
}
