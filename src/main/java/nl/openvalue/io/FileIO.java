package nl.openvalue.io;

import nl.openvalue.io.model.Gene;
import nl.openvalue.io.model.Genome;
import nl.openvalue.io.model.Pool;
import nl.openvalue.io.model.Species;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import static nl.openvalue.io.Config.FILENAME;
import static nl.openvalue.io.GameUtils.fitnessAlreadyMeasured;
import static nl.openvalue.io.Main.initializeRun;
import static nl.openvalue.io.model.Pool.nextGenome;

public class FileIO {
    private static final Pool pool = Pool.getInstance();

    public static void writeFile() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME))) {
            writer.write(pool.getGeneration() + "\n");
            writer.write(pool.getMaxFitness() + "\n");
            writer.write(pool.getSpecies().size() + "\n");
            for (Species species : pool.getSpecies()) {
                writer.write(species.getTopFitness() + "\n");
                writer.write(species.getStaleness() + "\n");
                writer.write(species.getGenomes().size() + "\n");
                for (Genome genome : species.getGenomes()) {
                    writer.write(genome.getFitness() + "\n");
                    writer.write(genome.getMaxNeuron() + "\n");
                    for (Map.Entry<String, Double> entry : genome.getMutationRates().entrySet()) {
                        writer.write(entry.getKey() + "\n");
                        writer.write(entry.getValue() + "\n");
                    }
                    writer.write("done\n");
                    writer.write(genome.getGenes().size() + "\n");
                    for (Gene gene : genome.getGenes()) {
                        writer.write(gene.getInto() + " ");
                        writer.write(gene.getOut() + " ");
                        writer.write(gene.getWeight() + " ");
                        writer.write(gene.getInnovation() + " ");
                        writer.write(gene.isEnabled() ? "1\n" : "0\n");
                    }
                }
            }
        }
    }

    public static void loadFile() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
            pool.reset();
            pool.setGeneration(Integer.parseInt(reader.readLine()));
            pool.setMaxFitness(Double.parseDouble(reader.readLine()));
            int numSpecies = Integer.parseInt(reader.readLine());
            for (int s = 0; s < numSpecies; s++) {
                Species species = new Species();
                pool.getSpecies().add(species);
                species.setTopFitness(Double.parseDouble(reader.readLine()));
                species.setStaleness(Integer.parseInt(reader.readLine()));
                int numGenomes = Integer.parseInt(reader.readLine());
                for (int g = 0; g < numGenomes; g++) {
                    Genome genome = new Genome();
                    species.getGenomes().add(genome);
                    genome.setFitness(Double.parseDouble(reader.readLine()));
                    genome.setMaxNeuron(Integer.parseInt(reader.readLine()));
                    String line = reader.readLine();
                    while (!line.equals("done")) {
                        genome.getMutationRates().put(line, Double.parseDouble(reader.readLine()));
                        line = reader.readLine();
                    }
                    int numGenes = Integer.parseInt(reader.readLine());
                    for (int n = 0; n < numGenes; n++) {
                        Gene gene = new Gene();
                        genome.getGenes().add(gene);
                        String[] geneData = reader.readLine().split(" ");
                        gene.setInto(Integer.parseInt(geneData[0]));
                        gene.setOut(Integer.parseInt(geneData[1]));
                        gene.setWeight(Double.parseDouble(geneData[2]));
                        gene.setInnovation(Integer.parseInt(geneData[3]));
                        gene.setEnabled("1".equals(geneData[4]));
                    }
                }
            }
        }

        while (fitnessAlreadyMeasured()) {
            nextGenome();
        }
        initializeRun();
        pool.setCurrentRound(pool.getCurrentRound() + 1);
    }

}
