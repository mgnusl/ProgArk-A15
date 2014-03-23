package com.devikaas.monoball.ingame.model;

import owg.engine.Engine;

public class Player implements Steppable {
    private int score = 0;
    private long counter;
    /**Delay between point increments, in seconds*/
    private final static int POINT_DELAY = 4;
    /**Amount to increase points by, at each {@link #POINT_DELAY} interval*/
    private final static int POINT_BURST = 2;


    private final BallGameModel ballGameModel;
    private final String name;


    public Player(BallGameModel ballGameModel, String name) {
        counter = 0;
        this.ballGameModel = ballGameModel;
        this.name = name;
    }

    @Override
    public void step() {
    	counter++;
        if(counter >= Engine.getDefaultFPS()*POINT_DELAY) {
            counter = 0;
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
