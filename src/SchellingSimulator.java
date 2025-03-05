import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * Simple simulator for Schelling Model of Segregation.
 * Running this version produces a textual visualization.
 * You can run SchellingVisualizer to see a nicer graphical
 * version.
 *
 *
 */
public class SchellingSimulator {

    /** Grid dimensions */
    private int width = 100;
    private int height = 100;

    /**
     * The actual grid
     */
    private int[][] grid;

    /**
     * How many classes of people?
     */
    private int classes = 2;

    /**
     * Total population to create
     */
    private int population = (int)(width * height * 0.92);

    /**
     * Min number of neighbors that must be "like" us for satisfaction
     */
    private int minNeighbors = 3;

    /**
     * Do we adopt a torus-like model where cells can see neighbors that
     * "wrap around to the other side of the world"?
     */
    public boolean wrapAround = false;

    /**
     * Random number generator
     */
    private Random randGen;

    /**
     * Initialize the simulator
     * @param seed Random number seed (for reproducibility)
     * @param numClasses Number of classes of people
     * @param pop Total population in grid (must be <= width * height)
     * @param numNeighbors Number of "like" neighbors needed to be satisfied
     * @param width Grid width
     * @param height Grid height
     */
    public SchellingSimulator(long seed, int numClasses, int pop, int numNeighbors, int width, int height) {
        this.width = width;
        this.height = height;
        population = pop;
        classes = numClasses;
        minNeighbors = numNeighbors;

        randGen = new Random(seed);

        grid = new int[width][height];
        clear();
    }

    /**
     * Accessor method for width
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Accessor method for height
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the status of the grid cell
     * @param x x-coordinate of the cell
     * @param y y-coordinate of the cell
     * @return an int that is the status of the grid: 0, if empty, else class id (1, 2, ...)
     */
    public int getGrid(int x, int y) {
        return grid[x][y];
    }

    /**
     * Empty the grid
     */
    private void clear() {
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                grid[i][j] = 0;
    }

    /**
     * Adds the specified population to the grid, choosing randomly
     */
    private void populate() {
        if (population > width * height)
            throw new RuntimeException("Only " + width * height + " cells exist!");

        for (int i = 0; i < population; i++) {
            int xChoice;
            int yChoice;
            do {
                xChoice = randGen.nextInt(width);
                yChoice = randGen.nextInt(height);

            } while (grid[xChoice][yChoice] != 0);

            grid[xChoice][yChoice] = randGen.nextInt(classes) + 1;
        }
    }

    /**
     * Move an unhappy individual to a random location
     * @param fromX old x-coordinate
     * @param fromY old y-coordinate
     */
    private void moveToRandom(int fromX, int fromY) {
        int xChoice;
        int yChoice;
        do {
            xChoice = randGen.nextInt(width);
            yChoice = randGen.nextInt(height);

        } while (grid[xChoice][yChoice] != 0);

        grid[xChoice][yChoice] = grid[fromX][fromY];
        grid[fromX][fromY] = 0;
    }

    /**
     * Is the occupant of this cell happy?
     * @param x x-coordinate of the cell
     * @param y y-coordinate of the cell
     * @return true if happy, false otherwise
     */
    private boolean isHappy(int x, int y) {
        // Empty cell is by definition happy
        // Also, in non-torus model: everyone on the edge of the grid stays happy
        if ((!wrapAround && isEdgeOfGrid(x,y)) ||
                grid[x][y] == 0)
            return true;

        int neighborsLikeMe = -1;	// Initialize to -1 since we'll count ourselves
        for (int xOff = -1; xOff <= 1; xOff++)
            for (int yOff = -1; yOff <= 1; yOff++)
                if (grid[(x + xOff + width) % width][(y + yOff+ height) % height] == grid[x][y])
                    neighborsLikeMe++;

        return (neighborsLikeMe >= minNeighbors);
    }

    /**
     * One round of simulation -- go through the grid and move
     * anyone who is unhappy.
     * @return True if everyone is happy, else false
     */
    private boolean movementRound() {
        boolean everyoneHappy = true;
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++) {
                if (!isHappy(x,y)) {
                    moveToRandom(x,y);
                    everyoneHappy = false;
                }
            }

        return everyoneHappy;
    }

    /**
     * Run the simulation until no one complains
     */
    private void simulateUntilHappy() {
        while (!movementRound());
    }

    /**
     * Print out the grid
     */
    public void display() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (grid[x][y] == 0)
                    System.out.print(' ');
                else {
                    if (grid[x][y] == 1)
                        System.out.print("x");
                    else if (grid[x][y] == 2)
                        System.out.print(".");
                    else
                        System.out.print(grid[x][y]);
                }
            }
            System.out.println();
        }
    }

    /**
     * Look at our neighbors, and sum up how many are like us
     * @param x x-coordinate
     * @param y y-coordinate
     * @return count of neighbors who are same as me
     */
    private int countLikeNeighbors(int x, int y) {
        // If cell unoccupied, return 0
        // If non-torus model: don't count anything for edges
        if ((!wrapAround && isEdgeOfGrid(x,y)) ||
                grid[x][y] == 0)
            return 0;

        int neighborsLikeMe = -1;	// Initialize to -1 since we'll count ourselves
        for (int xOff = -1; xOff <= 1; xOff++)
            for (int yOff = -1; yOff <= 1; yOff++)
                if (grid[(x + xOff + width) % width][(y + yOff+ height) % height] == grid[x][y])
                    neighborsLikeMe++;

        return neighborsLikeMe;
    }

    /**
     * Look at our neighbors, and sum up how many aren't like us
     * @param x x-coordinate
     * @param y y-coordinate
     * @return count of neighbors who are not same as me
     */
    private int countUnlikeNeighbors(int x, int y) {
        // If cell unoccupied, return 0
        // If non-torus model: don't count anything for edges
        if ((!wrapAround && isEdgeOfGrid(x,y)) ||
                grid[x][y] == 0)
            return 0;

        int neighborsUnlikeMe = 0;
        for (int xOff = -1; xOff <= 1; xOff++)
            for (int yOff = -1; yOff <= 1; yOff++)
                if (grid[(x + xOff + width) % width][(y + yOff+ height) % height] != grid[x][y])
                    neighborsUnlikeMe++;

        return neighborsUnlikeMe;
    }


    /**
     * Returns true if we're at the border of the grid
     * @param x
     * @param y
     * @return true if it is at the edge of the grid, false otherwise
     */
    private boolean isEdgeOfGrid(int x, int y) {
        return x == 0 || y == 0 || x == width - 1 || y == height - 1;
    }

    /**
     * Compute for each class the ratio of neighbors like vs. unlike it.  Average
     * these and return, as homophily ratio.
     * @return the average homophily ratio
     */
    private double homophilyRatio() {
        int numPeople = 0;							// Total population, not counting those immobile due to edges
        int numInClass[] = new int[classes];		// Sum, for each class, of population
        int likeInClass[] = new int[classes];		// Sum, for each class, of "like" neighbors
        int unlikeInClass[] = new int[classes];		//  "                   of "unlike" neighbors
        for (int i = 0; i < classes; i++) {
            numInClass[i] = 0;
            likeInClass[i] = 0;
            unlikeInClass[i] = 0;
        }
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (grid[x][y] != 0 && (wrapAround || !isEdgeOfGrid(x,y))) {
                    int c = countLikeNeighbors(x,y);
                    numPeople++;
                    numInClass[grid[x][y] - 1]++;
                    likeInClass[grid[x][y] - 1] += c;
                    unlikeInClass[grid[x][y] - 1] += countUnlikeNeighbors(x,y);
                }
            }
        }

        double avgHomophily = 0;
        for (int i = 0; i < classes; i++) {
            System.out.println(" Average homophily ratio for class " + (i+1) + ": " +
                    ((double)likeInClass[i] / ((double)likeInClass[i] + (double)unlikeInClass[i])) +
                    " / ratio of population: " + ((double)numInClass[i] / numPeople));

            avgHomophily += ((double)likeInClass[i] / ((double)likeInClass[i] + (double)unlikeInClass[i]));
        }
        avgHomophily /= classes;

        return avgHomophily;
    }

    /**
     * Compute for each class the ratio of neighbors like vs. unlike it.  Average
     * these and return, as homophily ratio.
     * @return the average homophily ratio
     */
    private double[] homophilyRatioByClass() {
        int numPeople = 0;							// Total population, not counting those immobile due to edges
        int numInClass[] = new int[classes];		// Sum, for each class, of population
        int likeInClass[] = new int[classes];		// Sum, for each class, of "like" neighbors
        int unlikeInClass[] = new int[classes];		//  "                   of "unlike" neighbors
        double[] avgHomophilyByClass = new double[classes];
        for (int i = 0; i < classes; i++) {
            numInClass[i] = 0;
            likeInClass[i] = 0;
            unlikeInClass[i] = 0;
        }
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (grid[x][y] != 0 && (wrapAround || !isEdgeOfGrid(x,y))) {
                    int c = countLikeNeighbors(x,y);
                    numPeople++;
                    numInClass[grid[x][y] - 1]++;
                    likeInClass[grid[x][y] - 1] += c;
                    unlikeInClass[grid[x][y] - 1] += countUnlikeNeighbors(x,y);
                }
            }
        }

        for (int i = 0; i < classes; i++) {
            System.out.println(" Average homophily ratio for class " + (i+1) + ": " +
                    ((double)likeInClass[i] / ((double)likeInClass[i] + (double)unlikeInClass[i])) +
                    " / ratio of population: " + ((double)numInClass[i] / numPeople));

            avgHomophilyByClass[i] += ((double)likeInClass[i] / ((double)likeInClass[i] + (double)unlikeInClass[i]));
        }

        return avgHomophilyByClass;
    }

    /**
     * Run the simulation
     * @param K the number of trials
     * @return the homophily ratios for each trial
     */
    public ArrayList<Double> simulate(int K) {
        ArrayList<Double> homophily = new ArrayList<Double>();
        wrapAround = false;
        for (int i = 0; i < K; i++) {
            System.out.println("Trial " + i + ":");
            clear();
            populate();
            simulateUntilHappy();
            homophily.add(homophilyRatio());
        }
        return homophily;

    }
    /**
     * Move a certain percentage of the population to new random locations (evictions).
     * @param evictionRate Percentage of population to be evicted (0.0 to 1.0)
     */
    private void evictAndRelocate(double evictionRate, int classToEvict) {
        int numEvictions = (int) (population * evictionRate);
        ArrayList<int[]> toEvict = new ArrayList<>();

        for (int i = 0; i < numEvictions; i++) {
            int x, y;
            do {
                x = randGen.nextInt(width);
                y = randGen.nextInt(height);
            } while (grid[x][y] == 0 || contains(toEvict, x, y) || grid[x][y] != classToEvict);

            toEvict.add(new int[]{x, y});
        }

        for (int[] cell : toEvict) {
            grid[cell[0]][cell[1]] = 0;
        }

        for (int[] cell : toEvict) {
            int xNew, yNew;
            do {
                xNew = randGen.nextInt(width);
                yNew = randGen.nextInt(height);
            } while (grid[xNew][yNew] != 0);

            grid[xNew][yNew] = classToEvict;
        }
    }

    /**
     * Helper method to check if a list contains a given coordinate
     */
    private boolean contains(ArrayList<int[]> list, int x, int y) {
        for (int[] pair : list) {
            if (pair[0] == x && pair[1] == y) return true;
        }
        return false;
    }

    /**
     * Run one round of movement, but now also includes forced evictions.
     * @return True if everyone is happy, else false
     */
    private boolean movementRound(double evictionRate, double evictionProbability, int classToEvict) {
        boolean everyoneHappy = true;
        if(randGen.nextDouble() < evictionProbability) {
            evictAndRelocate(evictionRate, classToEvict);
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!isHappy(x, y)) {
                    moveToRandom(x, y);
                    everyoneHappy = false;
                }
            }
        }
        return everyoneHappy;
    }

    /**
     * Run simulation with evictions.
     * @param K Number of trials
     * @param evictionRate Percentage of population to be evicted each round
     * @return Homophily ratios for each trial
     */
    public Map<Integer, ArrayList<Double>> simulateWithEvictions(int K, double evictionRate,
    double evictionProbability, int classToEvict) {
        Map<Integer, ArrayList<Double>> avgHomophily = new TreeMap<>();
        wrapAround = false;

        for(int i = 0; i < classes; i++) {
            avgHomophily.put(i, new ArrayList<>());
        }
        for (int i = 0; i < K; i++) {
            System.out.println("Trial " + i + " with eviction rate: " + evictionRate + " and " +
                    "eviction probability: " + evictionProbability);

            clear();
            populate();

            while (!movementRound(evictionRate, evictionProbability, classToEvict));
            double[] homophilyByClass = homophilyRatioByClass();
            for(int j = 0; j<classes; j++) {
                double homophily = homophilyByClass[j];
                avgHomophily.get(j).add(homophily);
            }
        }
        return avgHomophily;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        SchellingSimulator sch = new SchellingSimulator(1, 2, (int)(50*50*0.92), 3, 50, 50);

        ArrayList<Double> homophily = new ArrayList<Double>();
        int K = 10;

        homophily = sch.simulate(K);

        // Show the last one
        sch.display();

        // Compute mean + stddev
        double avgHom = 0;
        double stDev = 0;
        for (int i = 0; i < K; i++) {
            avgHom += homophily.get(i);
        }
        avgHom /= K;
        for (int i = 0; i < K; i++) {
            stDev += (avgHom - homophily.get(i)) * (avgHom - homophily.get(i));
        }
        stDev = Math.sqrt(stDev / K);

        System.out.println("Average homophily ratio across trials: " + avgHom);
        System.out.println("Standard deviation across trials: " + stDev);
    }

}
