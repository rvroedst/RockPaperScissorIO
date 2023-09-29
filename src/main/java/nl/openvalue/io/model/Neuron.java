package nl.openvalue.io.model;

import lombok.Getter;
import lombok.Setter;
import nl.openvalue.io.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Getter
@Setter
public class Neuron {

    // Attributes
    public List<Gene> incoming;  // Assuming incoming is a list of integers
    public double value;
    private static final Random random = new Random();

    // Constructor
    public Neuron() {
        this.incoming = new ArrayList<>();
        this.value = 0.0;
    }

    public static int randomNeuron(List<Gene> genes, boolean nonInput) {
        Map<Integer, Boolean> neurons = new HashMap<>();

        if (!nonInput) {
            for (int i = 0; i < Config.INPUTS; i++) {
                neurons.put(i, true);
            }
        }

        for (int o = 0; o < Config.OUTPUTS; o++) {
            neurons.put(Config.MAX_NODES + o, true);
        }

        for (Gene gene : genes) {
            if ((!nonInput) || gene.into >= Config.INPUTS) {
                neurons.put(gene.into, true);
            }
            if ((!nonInput) || gene.out >= Config.INPUTS) {
                neurons.put(gene.out, true);
            }
        }

        int count = neurons.size();

        int n = random.nextInt(count);

        for (Integer key : neurons.keySet()) {
            if (n == 0) {
                return key;
            }
            n--;
        }

        return -1;
    }

}
