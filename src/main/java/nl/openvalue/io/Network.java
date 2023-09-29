package nl.openvalue.io;

import lombok.Getter;
import lombok.Setter;
import nl.openvalue.io.model.Gene;
import nl.openvalue.io.model.Genome;
import nl.openvalue.io.model.Neuron;
import nl.openvalue.rps.Move;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

@Getter
@Setter
public class Network {

    private final Map<Integer, Neuron> neurons;

    public Network(){
        neurons = new HashMap<>();
    }

    public static void generateNetwork(Genome genome) {
        Network network = new Network();  // Assuming a Network class

        for (int i = 0; i <= Config.INPUTS - 1; i++) {  // Assuming INPUTS is defined in the Config class
            network.neurons.put(i, new Neuron());
        }

        for (int o = 0; o <= Config.OUTPUTS - 1; o++) {  // Assuming OUTPUTS is defined in the Config class
            network.neurons.put(Config.MAX_NODES + o, new Neuron());  // Assuming MAX_NODES is defined in the Config class
        }

        // Sorting the genes based on the 'out' attribute
        genome.genes.sort(Comparator.comparingInt(gene -> gene.out));  // Assuming an 'out' attribute in the Gene class

        for (Gene gene : genome.genes) {
            if (gene.enabled) {  // Assuming an 'enabled' attribute in the Gene class
                if (network.neurons.isEmpty() || !network.neurons.containsKey(gene.out)) {
                    network.neurons.put(gene.out, new Neuron());
                }

                Neuron neuron = network.neurons.get(gene.out);
                neuron.incoming.add(gene);  // Assuming an 'incoming' attribute of type List<Gene> in the Neuron class

                if (network.neurons.isEmpty() || !network.neurons.containsKey(gene.into)) {
                    network.neurons.put(gene.into, new Neuron());
                }
            }
        }

        genome.setNetwork(network);  // Assuming a 'network' attribute in the Genome class
    }

    public static Optional<Move> evaluateNetwork(Network network, List<List<Integer>> inputs) {
        if (inputs.size() != Config.INPUTS) {
            throw new IllegalArgumentException("Incorrect number of neural network inputs.");
        }

        // Assigning input values to neurons
        IntStream.range(0, Config.INPUTS)
                .forEach(i -> network.neurons.get(i).value = inputs.get(i).get(0));

        // Calculating neuron values
        for (Neuron neuron : network.neurons.values()) {
            double sum = 0;
            for (Gene incoming : neuron.incoming) {
                Neuron other = network.neurons.get(incoming.into);
                sum += incoming.weight * other.value;
            }

            if (!neuron.incoming.isEmpty()) {
                neuron.value = sigmoid(sum);
            }
        }

        // Determining the move with the highest output value
        return IntStream.range(0, Config.OUTPUTS)
                .boxed()
                .max(Comparator.comparing(o -> network.neurons.get(Config.MAX_NODES + o).value))
                .map(o -> Move.values()[o]);
    }


    public static double sigmoid(double x) {
        return 2 / (1 + Math.exp(-4.9 * x)) - 1;
    }

}
