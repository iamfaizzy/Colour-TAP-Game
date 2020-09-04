package com.faizan.assignmentblacklight;

import java.util.Random;

public class Tiles {
    public static final int RED=1;
    public static final int BLUE=2;
    public static final int YELLOW=3;
    public static final int GREEN=4;


    public static int getRandomWithExclusion(int... exclude) {
        Random rnd = new Random();
        int random = RED + rnd.nextInt(GREEN - RED + 1 - exclude.length);
        for (int ex : exclude) {
            if (random < ex) {
                break;
            }
            random++;
        }
        return random;
    }
}
