package se.chalmers.tda367.team15.game.model.world;

import java.util.Random;

/**
 * Utility class for generating 2D Perlin noise. (Stolen from the internet)
 */
public class PerlinNoise {
    private static final int PERMUTATION_SIZE = 256;
    private final int[] permutation;

    public PerlinNoise(long seed) {
        this.permutation = generatePermutation(seed);
    }

    /** @return a value between -1 and 1 */
    public double noise(double x, double y) {
        int xi = (int) Math.floor(x) & 255;
        int yi = (int) Math.floor(y) & 255;

        double xf = x - Math.floor(x);
        double yf = y - Math.floor(y);

        double u = fade(xf);
        double v = fade(yf);

        int aa = permutation[permutation[xi] + yi];
        int ab = permutation[permutation[xi] + yi + 1];
        int ba = permutation[permutation[xi + 1] + yi];
        int bb = permutation[permutation[xi + 1] + yi + 1];

        double x1 = lerp(grad(aa, xf, yf), grad(ba, xf - 1, yf), u);
        double x2 = lerp(grad(ab, xf, yf - 1), grad(bb, xf - 1, yf - 1), u);

        return lerp(x1, x2, v);
    }

    private int[] generatePermutation(long seed) {
        int[] perm = new int[PERMUTATION_SIZE * 2];
        int[] base = new int[PERMUTATION_SIZE];

        for (int i = 0; i < PERMUTATION_SIZE; i++) {
            base[i] = i;
        }

        Random rng = new Random(seed);
        for (int i = PERMUTATION_SIZE - 1; i > 0; i--) {
            int j = rng.nextInt(i + 1);
            int temp = base[i];
            base[i] = base[j];
            base[j] = temp;
        }

        for (int i = 0; i < PERMUTATION_SIZE; i++) {
            perm[i] = base[i];
            perm[PERMUTATION_SIZE + i] = base[i];
        }

        return perm;
    }

    private double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private double lerp(double a, double b, double t) {
        return a + t * (b - a);
    }

    private double grad(int hash, double x, double y) {
        int h = hash & 3;
        double u = h < 2 ? x : y;
        double v = h < 2 ? y : x;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }
}

