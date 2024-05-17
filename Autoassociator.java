import java.util.Random;

public class Autoassociator {
    private int weights[][];
    private int trainingCapacity;

    public Autoassociator(CourseArray courses) {
        int numCourses = courses.length();
        weights = new int[numCourses][numCourses];
        initializeWeights();
    }

    public int getTrainingCapacity() {
        return trainingCapacity;
    }

    public void training(int pattern[]) {
        int numIterations = pattern.length * pattern.length;
        for (int iter = 0; iter < numIterations; iter++) {
            int index = new Random().nextInt(pattern.length);
            unitUpdate(pattern, index);
        }
    }

    public int unitUpdate(int neurons[]) {
        int index = new Random().nextInt(neurons.length);
        int output = calculateOutput(neurons, index);
        neurons[index] = output;
        return index;
    }

    public void chainUpdate(int neurons[], int steps) {
        for (int step = 0; step < steps; step++) {
            int index = unitUpdate(neurons);
            unitUpdate(neurons, index);
        }
    }

    public void fullUpdate(int neurons[]) {
        boolean stable = false;
        while (!stable) {
            int index = unitUpdate(neurons);
            stable = (neurons[index] == calculateOutput(neurons, index));
        }
    }

    private void initializeWeights() {
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                weights[i][j] = 0;
            }
        }
    }

    private int calculateOutput(int neurons[], int index) {
        int sum = 0;
        for (int i = 0; i < neurons.length; i++) {
            if (i != index) {
                sum += neurons[i] * weights[i][index];
            }
        }
        return (sum >= 0) ? 1 : -1;
    }
}
