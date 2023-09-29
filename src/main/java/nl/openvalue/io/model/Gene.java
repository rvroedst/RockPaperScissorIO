package nl.openvalue.io.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

@Getter
@Setter
public class Gene {

    public int into;
    public int out;
    public double weight;
    public boolean enabled;
    public int innovation;
    private static Random random = new Random();

    // Constructor
    public Gene() {
        this.into = 0;
        this.out = 0;
        this.weight = 0.0;
        this.enabled = true;
        this.innovation = 0;
    }

    public static Gene copyGene(Gene originalGene) {
        Gene copiedGene = new Gene();
        copiedGene.into = originalGene.into;
        copiedGene.out = originalGene.out;
        copiedGene.weight = originalGene.weight;
        copiedGene.enabled = originalGene.enabled;
        copiedGene.innovation = originalGene.innovation;

        return copiedGene;
    }

    public static boolean containsLink(List<Gene> genes, Gene link) {
        for (Gene gene : genes) {
            if (gene.into == link.into && gene.out == link.out) {
                return true;
            }
        }
        return false;
    }

    public static double disjoint(List<Gene> genes1, List<Gene> genes2) {
        Set<Integer> i1 = new HashSet<>();
        for (Gene gene : genes1) {
            i1.add(gene.innovation);
        }

        Set<Integer> i2 = new HashSet<>();
        for (Gene gene : genes2) {
            i2.add(gene.innovation);
        }

        int disjointGenes = 0;
        for (Gene gene : genes1) {
            if (!i2.contains(gene.innovation)) {
                disjointGenes++;
            }
        }

        for (Gene gene : genes2) {
            if (!i1.contains(gene.innovation)) {
                disjointGenes++;
            }
        }

        int n = Math.max(genes1.size(), genes2.size());

        return (double) disjointGenes / n;
    }

    public static double weights(List<Gene> genes1, List<Gene> genes2) {
        Map<Integer, Gene> i2 = new HashMap<>();
        for (Gene gene : genes2) {
            i2.put(gene.innovation, gene);
        }

        double sum = 0;
        int coincident = 0;
        for (Gene gene : genes1) {
            if (i2.containsKey(gene.innovation)) {
                Gene gene2 = i2.get(gene.innovation);
                sum += Math.abs(gene.weight - gene2.weight);
                coincident++;
            }
        }
        return sum / coincident;
    }



}
