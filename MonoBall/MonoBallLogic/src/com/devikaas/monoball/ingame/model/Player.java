package com.devikaas.monoball.ingame.model;

import owg.engine.Engine;

public class Player implements Steppable {
    private int score = 0;
    private long prevTime;
    private long counter;

    private final static int POINT_DELAY = 4;
    private final static int POINT_BURST = 2;


    private final BallGameModel ballGameModel;
    private final String name;


    public Player(BallGameModel ballGameModel, String name) {
        prevTime = 0;
        counter = 0;
        this.ballGameModel = ballGameModel;
        this.name = name;
    }

    @Override
    public void step() {
    	counter++;
        long currentTime = (counter*1000)/Engine.getDefaultFPS();
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
