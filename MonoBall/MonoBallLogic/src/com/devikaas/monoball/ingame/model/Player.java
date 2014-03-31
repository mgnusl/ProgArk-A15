package com.devikaas.monoball.ingame.model;

import owg.engine.Engine;
import owg.engine.util.Kryo;

public class Player implements Steppable {
    private float bonus = 1;
    private int score = 0;
    private long counter;
    private int lives = 0;

    /**Delay between point increments, in seconds*/
    private final static float POINT_DELAY = 1;

    /**Amount to increase points by, at each {@link #POINT_DELAY} interval*/
    private final static float POINT_BURST = 1;

    private final String name;
    
    @Kryo
    private Player() {
    	name=null;
	}
    public  Player(String name) {
        counter = 0;
    	this.name=name;
	}

    @Override
    public void step() {
    	counter++;
        if(counter >= Engine.getDefaultTickRate()*POINT_DELAY) {
            counter = 0;
            score += POINT_BURST * bonus;
        }
    }
    
    

    public void addScore(int score) {
        this.score += score;
    }

    public void subtractScore(int score) {
        this.score -= score;
    }

    public void addLives(int lives) {
        this.lives += lives;
    }

    public void subtractLives(int lives) {
        this.lives -= lives;
    }
    public int getScore() { return score; }
    public int getLives() { return lives; }

    public String getName() {
        return name;
    }

    public void startBonusRound() {
        bonus = lives;
    }
}
