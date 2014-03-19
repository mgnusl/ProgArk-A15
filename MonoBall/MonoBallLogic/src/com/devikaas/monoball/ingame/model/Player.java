package com.devikaas.monoball.ingame.model;

public class Player implements Steppable {
    private int score = 0;
    private long prevTime;

    private final static int POINT_DELAY = 4;
    private final static int POINT_BURST = 2;


    private final BallGameModel ballGameModel;
    private final String name;


    public Player(BallGameModel ballGameModel, String name) {
        prevTime = System.currentTimeMillis();
        this.ballGameModel = ballGameModel;
        this.name = name;
    }

    @Override
    public void step() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - prevTime > POINT_DELAY * 1000) {
            prevTime = currentTime;
            score += POINT_BURST;
        }

    }

    public void addScore(int score) {
        score += score;
    }

    public void subtractScore(int score) {
        score -= score;
    }

    public int getScore() { return score; }

    public String getName() {
        return name;
    }
}
