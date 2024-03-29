package com.devikaas.monoball.ingame.model;

import com.devikaas.monoball.GameOverState;

import owg.engine.Engine;
import owg.engine.util.Alarm;
import owg.engine.util.Kryo;
import owg.engine.util.V3F;

import com.devikaas.monoball.ingame.model.map.CollidableList;
import com.devikaas.monoball.ingame.model.map.MapGenerator;
import com.devikaas.monoball.ingame.model.map.MapModel;
import com.devikaas.monoball.ingame.model.map.Row;
/**A fully specified instance of the game model.*/
public class BallGameModel implements Alarm.AlarmTriggerable {
    // Other models that the game model controls
	private final CameraModel cameraModel;
	private final MapModel mapModel;
	private final BallModel ballModel;
    private Player playerOneModel;
    private Player playerTwoModel;
    private Player currentPlayer;

    public static final int BONUS_MULITPLIER = 3;

	private final CollidableList collisionHandler;

    // World info
	private final V3F gravity;

    // Gametime variables
    private boolean reversed = false;
    private boolean running = false;
    private boolean timeout = false;
    private boolean bonus = false;


    // Timing before changing player
    public final static int PLAYER_TIME_LIMIT = 25;
    public final static int TIMEOUT_TIME_LIMIT = 4;
    public final static int ALARM_PLAYTIME_INDEX = 0;
    public final static int ALARM_TIMEOUT_INDEX = 1;
    private final int playerTime;
    private final int timeoutTime;
    private Alarm alarm;


    @Kryo
	private BallGameModel() {
    	playerTime=0;
        mapModel=null;
        gravity=null;
        collisionHandler=null;
        cameraModel=null;
        ballModel=null;
        timeoutTime=0;
    }

	public BallGameModel(Player one, Player two, int seed) {
		final float w = MapModel.MAP_WIDTH;
		final float h = (w*16)/9;
		cameraModel = new CameraModel(this, new V3F(MapModel.MAP_X, -h/2f, 0), w, h);
		
		MapGenerator generator;
		//generator = new TestGenerator(seed);
		generator = new AssetMapGenerator(seed);
		//generator = new FontMapGenerator("misc/asciifont.txt", "bad map is bad ", seed);
		
		
		mapModel = new MapModel(this, generator);
		
		collisionHandler = new CollidableList(mapModel);
		gravity = new V3F(0, 1f, 0);
		ballModel = new BallModel(this, new V3F(MapModel.MAP_X+MapModel.MAP_WIDTH/2, 0, 0), Row.ROW_HEIGHT/2-1);
		
		collisionHandler.addCollidable(ballModel);

        // Set timings
        playerTime = PLAYER_TIME_LIMIT*Engine.getDefaultTickRate();
        timeoutTime = TIMEOUT_TIME_LIMIT*Engine.getDefaultTickRate();
        
        playerOneModel = one;
        playerTwoModel = two;

        currentPlayer = playerOneModel;

		running = true;

		alarm = new Alarm(2, this);
		alarm.set(ALARM_PLAYTIME_INDEX, playerTime);
	}
	/**Returns the game camera model. 
	 * This camera defines the borders where a player will lose a life if they fall outside.*/
	public CameraModel getCamera() {
		return cameraModel;
	}
	public BallModel getBall() {
		return ballModel;
	}
	public MapModel getMap() {
		return mapModel;
	}
    public Alarm getAlarm() { return alarm; }
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setX(float x) {
        gravity.x(x);
    }

    public boolean isReversed() { return reversed; }
    public boolean isRunning() { return running; }

    public void reverse() {
        gravity.reverse();
        cameraModel.reverse();
        reversed = !reversed;
    }

	public void step() {
        if (running) {
            ballModel.step();
            cameraModel.step();
            mapModel.step();

            collisionHandler.step();

            currentPlayer.step();
        }

        alarm.step();

	}
	public V3F getGravity() {
		return gravity;
	}

    public void killPlayer() {
        currentPlayer.subtractLives(1);

        alarm.stop(ALARM_PLAYTIME_INDEX);

        // Check if both players are dead
        if (playerOneModel.getLives() == 0 && playerTwoModel.getLives() == 0) {
            Engine.scene().setState(new GameOverState(playerOneModel, playerTwoModel));

        // Only current player are dead
        } else {
            startTimeout();
        }
    }

    public void switchPlayer() {
        System.out.println(currentPlayer);

        if (currentPlayer.getLives() == 0) {
            // Changes the current player
            currentPlayer = (currentPlayer == playerOneModel ? playerTwoModel : playerOneModel);

            // Initiate bonus round if the player lives is above one
            if (currentPlayer.getLives() > 1) {
                bonus = true;
                currentPlayer.startBonusRound();

            }
        } else {
            // Changes the current player
            currentPlayer = (currentPlayer == playerOneModel ? playerTwoModel : playerOneModel);
        }

        alarm.set(ALARM_PLAYTIME_INDEX, playerTime);

        reverse();

        timeout = false;
        running = true;
    }

    @Override
    public void alarm(int index) {
        switch (index) {
            case ALARM_PLAYTIME_INDEX:
                if (!bonus)
                    startTimeout();
                break;
            case ALARM_TIMEOUT_INDEX:
                switchPlayer();
                break;
        }
    }

    private void startTimeout() {
        timeout = true;
        running = false;

        alarm.set(ALARM_TIMEOUT_INDEX, timeoutTime);
    }

    public int getPlayerTime(){
		return playerTime;
	}
	public int getPlayerTimeLimit(){
		return PLAYER_TIME_LIMIT;
	}

    public boolean isTimeout() {
        return timeout;
    }
    public boolean isBonus() { return bonus; }
}
