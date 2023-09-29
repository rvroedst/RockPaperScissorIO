package nl.openvalue.io.model;

import lombok.Getter;
import lombok.Setter;
import nl.openvalue.io.Config;

import java.util.ArrayList;
import java.util.List;

import static nl.openvalue.io.GameUtils.newGeneration;
import static nl.openvalue.io.Main.initializeRun;
import static nl.openvalue.io.model.Genome.basicGenome;
import static nl.openvalue.io.service.SpeciesService.addToSpecies;

@Getter
@Setter
public class Pool {

    // Attributes
    public List<Species> species;  // Assuming a Species class
    public int generation;
    public int innovation;
    public int currentSpecies;
    public int currentGenome;
    public int currentRound;
    public double maxFitness;

    private static final Pool pool = new Pool();

    private Pool() {
        initialize();
    }

    private void initialize(){
        this.species = new ArrayList<>();
        this.generation = 1;
        this.innovation = Config.OUTPUTS;  // Assuming OUTPUTS is defined in the Config class
        this.currentSpecies = 1;
        this.currentGenome = 1;
        this.currentRound = 1;
        this.maxFitness = 0;
    }

    public void populatePool(){
        for (int i = 0; i < Config.POPULATION; i++) {  // Assuming POPULATION is defined in the Config class or equivalent
            Genome basic = basicGenome();
            addToSpecies(basic);
        }
    }

    public void reset(){
        initialize();
    }

    public static Pool getInstance() {
        return pool;
    }

    public static int newInnovation(){
        pool.innovation = pool.innovation + 1;
        return pool.innovation;
    }

    public static void nextGenome() {
        pool.currentGenome++;
        if (pool.currentGenome > pool.species.get(pool.currentSpecies - 1).getGenomes().size()) {
            pool.currentGenome = 1;
            pool.currentSpecies++;
            if (pool.currentSpecies > pool.species.size()) {
                newGeneration();
                pool.currentSpecies = 1;
            }
        }
    }
}
