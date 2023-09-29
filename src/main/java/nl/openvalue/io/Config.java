package nl.openvalue.io;

public class Config {

    // Constants
    // Assuming we consider the last 5 moves for both player and AI, with one move being represented with 3 numbers
    // One-hot
    public static final int INPUT_SIZE = 5;
    public static final int INPUTS = 5;
    public static int OUTPUTS = 3;  // This will be set based on the length of buttonNames
    public static final int POPULATION = 300;
    public static final double DELTA_DISJOINT = 2.0;
    public static final double DELTA_WEIGHTS = 0.4;
    public static final double DELTA_THRESHOLD = 1.0;
    public static final int STALE_SPECIES = 15;
    public static final double MUTATE_CONNECTIONS_CHANCE = 0.25;
    public static final double PERTURB_CHANCE = 0.90;
    public static final double CROSSOVER_CHANCE = 0.75;
    public static final double LINK_MUTATION_CHANCE = 2.0;
    public static final double NODE_MUTATION_CHANCE = 0.50;
    public static final double BIAS_MUTATION_CHANCE = 0.40;
    public static final double STEP_SIZE = 0.1;
    public static final double DISABLE_MUTATION_CHANCE = 0.4;
    public static final double ENABLE_MUTATION_CHANCE = 0.2;
    public static final int TIMEOUT_CONSTANT = 20;
    public static final int MAX_NODES = 1000000;
    public static final String FILENAME = "gamestate";
}

