package nl.openvalue.io;

import nl.openvalue.io.model.Gene;
import nl.openvalue.io.model.Genome;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static nl.openvalue.io.model.Gene.containsLink;
import static nl.openvalue.io.model.Gene.copyGene;
import static nl.openvalue.io.model.Neuron.randomNeuron;
import static nl.openvalue.io.model.Pool.newInnovation;

public class Mutation {

    private Mutation(){}

    private static final Random random = new Random();

    public static void pointMutate(Genome genome) {
        double step = genome.mutationRates.get("step");


        for (Gene gene : genome.genes) {
            if (random.nextDouble() < Config.PERTURB_CHANCE) {  // Assuming PERTURB_CHANCE is defined in the Config class
                gene.weight += random.nextDouble() * step * 2 - step;
            } else {
                gene.weight = random.nextDouble() * 4 - 2;
            }
        }
    }

    public static void linkMutate(Genome genome, boolean forceBias) {
        int neuron1 = randomNeuron(genome.genes, false);
        int neuron2 = randomNeuron(genome.genes, true);

        Gene newLink = new Gene();
        if (neuron1 <= Config.INPUTS && neuron2 <= Config.INPUTS) {
            return;  // Both input nodes
        }
        if (neuron2 <= Config.INPUTS) {
            int temp = neuron1;
            neuron1 = neuron2;
            neuron2 = temp;
        }

        newLink.into = neuron1;
        newLink.out = neuron2;
        if (forceBias) {
            newLink.into = Config.INPUTS;
        }

        if (containsLink(genome.genes, newLink)) {
            return;
        }
        newLink.innovation = newInnovation();
        newLink.weight = random.nextDouble() * 4 - 2;

        genome.genes.add(newLink);
    }

    public static void nodeMutate(Genome genome) {
        if (genome.genes.isEmpty()) {
            return;
        }

        genome.maxNeuron++;

        Gene gene = genome.genes.get(random.nextInt(genome.genes.size()));
        if (!gene.enabled) {
            return;
        }
        gene.enabled = false;

        Gene gene1 = copyGene(gene);
        gene1.out = genome.maxNeuron;
        gene1.weight = 1.0;
        gene1.innovation = newInnovation();
        gene1.enabled = true;
        genome.genes.add(gene1);

        Gene gene2 = copyGene(gene);
        gene2.into = genome.maxNeuron;
        gene2.innovation = newInnovation();
        gene2.enabled = true;
        genome.genes.add(gene2);
    }

    public static void enableDisableMutate(Genome genome, boolean enable) {
        List<Gene> candidates = new ArrayList<>();
        for (Gene gene : genome.genes) {
            if (gene.enabled != enable) {
                candidates.add(gene);
            }
        }
        if (candidates.isEmpty()) {
            return;
        }
        Gene gene = candidates.get(random.nextInt(candidates.size()));
        gene.enabled = !gene.enabled;
    }
}
