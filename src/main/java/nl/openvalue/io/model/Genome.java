package nl.openvalue.io.model;

import lombok.Getter;
import lombok.Setter;
import nl.openvalue.io.Config;
import nl.openvalue.io.Network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static nl.openvalue.io.Mutation.enableDisableMutate;
import static nl.openvalue.io.Mutation.linkMutate;
import static nl.openvalue.io.Mutation.nodeMutate;
import static nl.openvalue.io.Mutation.pointMutate;
import static nl.openvalue.io.model.Gene.copyGene;
import static nl.openvalue.io.model.Gene.disjoint;
import static nl.openvalue.io.model.Gene.weights;

@Getter
@Setter
public class Genome {
    // Attributes
    public List<Gene> genes;  // Assuming a Gene class
    public double fitness;
    public double adjustedFitness;
    public Network network;  // Using a Map for the network
    public int maxNeuron;
    public int globalRank;
    public Map<String, Double> mutationRates;
    public static final Random random = new Random();

    // Constructor
    public Genome() {
        this.genes = new ArrayList<>();
        this.fitness = 0;
        this.adjustedFitness = 0;
        this.network = new Network();
        this.maxNeuron = 0;
        this.globalRank = 0;
        this.mutationRates = new HashMap<>();
        this.mutationRates.put("connections", Config.MUTATE_CONNECTIONS_CHANCE);  // Assuming constants are defined in Config
        this.mutationRates.put("link", Config.LINK_MUTATION_CHANCE);
        this.mutationRates.put("bias", Config.BIAS_MUTATION_CHANCE);
        this.mutationRates.put("node", Config.NODE_MUTATION_CHANCE);
        this.mutationRates.put("enable", Config.ENABLE_MUTATION_CHANCE);
        this.mutationRates.put("disable", Config.DISABLE_MUTATION_CHANCE);
        this.mutationRates.put("step", Config.STEP_SIZE);
    }

    public static Genome copyGenome(Genome originalGenome) {
        Genome copiedGenome = new Genome();

        for (Gene gene : originalGenome.genes) {  // Assuming a Gene class with a copy method
            copiedGenome.genes.add(copyGene(gene));  // Assuming a copy method in the Gene class
        }

        copiedGenome.maxNeuron = originalGenome.maxNeuron;
        copiedGenome.mutationRates.put("connections", originalGenome.mutationRates.get("connections"));
        copiedGenome.mutationRates.put("link", originalGenome.mutationRates.get("link"));
        copiedGenome.mutationRates.put("bias", originalGenome.mutationRates.get("bias"));
        copiedGenome.mutationRates.put("node", originalGenome.mutationRates.get("node"));
        copiedGenome.mutationRates.put("enable", originalGenome.mutationRates.get("enable"));
        copiedGenome.mutationRates.put("disable", originalGenome.mutationRates.get("disable"));

        return copiedGenome;
    }

    public static Genome basicGenome() {
        Genome genome = new Genome();

        genome.maxNeuron = Config.INPUTS;
        mutate(genome);

        return genome;
    }

    public static Genome crossover(Genome g1, Genome g2) {
        // Make sure g1 is the higher fitness genome
        if (g2.fitness > g1.fitness) {
            Genome tempG = g1;
            g1 = g2;
            g2 = tempG;
        }

        Genome child = new Genome();

        Map<Integer, Gene> innovations2 = new HashMap<>();
        for (Gene gene : g2.genes) {
            innovations2.put(gene.innovation, gene);
        }


        for (Gene gene1 : g1.genes) {
            Gene gene2 = innovations2.get(gene1.innovation);
            if (gene2 != null && random.nextInt(2) == 1 && gene2.enabled) {
                child.genes.add(copyGene(gene2));
            } else {
                child.genes.add(copyGene(gene1));
            }
        }

        child.maxNeuron = Math.max(g1.maxNeuron, g2.maxNeuron);
        child.mutationRates.putAll(g1.mutationRates);

        return child;
    }

    public static void mutate(Genome genome) {

        for (Map.Entry<String, Double> entry : genome.mutationRates.entrySet()) {
            double rate = entry.getValue();
            if (random.nextInt(2) == 0) {
                genome.mutationRates.put(entry.getKey(), 0.95 * rate);
            } else {
                genome.mutationRates.put(entry.getKey(), 1.05263 * rate);
            }
        }

        if (random.nextDouble() < genome.mutationRates.get("connections")) {
            pointMutate(genome);
        }

        double p = genome.mutationRates.get("link");
        while (p > 0) {
            if (random.nextDouble() < genome.mutationRates.get("link")) {
                linkMutate(genome, false);
            }
            p--;
        }

        p = genome.mutationRates.get("bias");
        while (p > 0) {
            if (random.nextDouble() < genome.mutationRates.get("bias")) {
                linkMutate(genome, true);
            }
            p--;
        }

        p = genome.mutationRates.get("node");
        while (p > 0) {
            if (random.nextDouble() < genome.mutationRates.get("node")) {
                nodeMutate(genome);
            }
            p--;
        }

        p = genome.mutationRates.get("enable");
        while (p > 0) {
        if (random.nextDouble() < genome.mutationRates.get("enable")) {
            enableDisableMutate(genome, true);
        }
            p--;
        }

        p = genome.mutationRates.get("disable");
        while (p > 0) {
            if (random.nextDouble() < genome.mutationRates.get("disable")) {
                enableDisableMutate(genome, false);
            }
            p--;
        }
    }

    public static boolean sameSpecies(Genome genome1, Genome genome2) {
        double dd = Config.DELTA_DISJOINT * disjoint(genome1.genes, genome2.genes);
        double dw = Config.DELTA_WEIGHTS * weights(genome1.genes, genome2.genes);
        return dd + dw < Config.DELTA_THRESHOLD;
    }

}
