package nl.openvalue.io.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Setter
@Getter
public class Species {

    // Attributes
    private double topFitness;
    private int staleness;
    private List<Genome> genomes;  // Assuming a Genome class
    private double averageFitness;
    private static final Random random = new Random();

    // Constructor
    public Species() {
        this.topFitness = 0;
        this.staleness = 0;
        this.genomes = new ArrayList<>();
        this.averageFitness = 0;
    }

    public void addToGenomes(Genome child){
        this.genomes.add(child);
    }

    public void incrementStaleness() {
        this.staleness++;
    }
}
