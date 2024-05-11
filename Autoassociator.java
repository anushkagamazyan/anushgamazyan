import java.util.Random;

public class Autoassociator {
    private int weights[][];
    private int trainingCapacity;

    // Constructor
    public Autoassociator(CourseArray courses) {
        // Initialize weights with dimensions based on the number of courses
        int numCourses = courses.getNumCourses();
        weights = new int[numCourses][numCourses];
        trainingCapacity = numCourses;
        
        // Initialize weights randomly
        initializeWeights();
    }

    // Initialize weights randomly
    private void initializeWeights() {
        Random rand = new Random();
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                // Randomly assign weights as either 1 or -1
                weights[i][j] = rand.nextBoolean() ? 1 : -1;
            }
        }
    }

    // Get training capacity
    public int getTrainingCapacity() {
        return trainingCapacity;
    }

    // Train the autoassociator with a given pattern
    public void training(int pattern[]) {
        // Update weights based on the input pattern
        for (int i = 0; i < pattern.length; i++) {
            for (int j = 0; j < pattern.length; j++) {
                weights[i][j] += pattern[i] * pattern[j];
            }
        }
    }

    // Implement a single update step and return the index of the updated neuron
    public int unitUpdate(int neurons[]) {
        Random rand = new Random();
        int index = rand.nextInt(neurons.length);
        int sum = 0;
        for (int i = 0; i < neurons.length; i++) {
            sum += weights[index][i] * neurons[i];
        }
        neurons[index] = sum >= 0 ? 1 : -1;
        return index;
    }

    // Implement the update step of a single neuron specified by index
    public void unitUpdate(int neurons[], int index) {
        int sum = 0;
        for (int i = 0; i < neurons.length; i++) {
            sum += weights[index][i] * neurons[i];
        }
        neurons[index] = sum >= 0 ? 1 : -1;
    }

    // Implement the specified number of update steps
    public void chainUpdate(int neurons[], int steps) {
        for (int i = 0; i < steps; i++) {
            unitUpdate(neurons);
        }
    }

    // Update the input until the final state is achieved
    public void fullUpdate(int neurons[]) {
        boolean converged = false;
        while (!converged) {
            int[] copyNeurons = neurons.clone();
            unitUpdate(neurons);
            converged = isConverged(neurons, copyNeurons);
        }
    }

    // Check if the current state is the same as the previous state
    private boolean isConverged(int[] state1, int[] state2) {
        for (int i = 0; i < state1.length; i++) {
            if (state1[i] != state2[i]) {
                return false;
            }
        }
        return true;
    }
}
