package com.devikaas.monoball.ingame.model;

import com.devikaas.monoball.GameOverState;
import owg.engine.Engine;
import owg.engine.util.Alarm;
import owg.engine.util.Kryo;
import owg.engine.util.V3F;

import com.devikaas.monoball.ingame.model.map.CollidableList;
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

	private final CollidableList collisionHandler;

    // World info
	private final V3F gravity;
    private final int SEED = 7;

    // Gametime variables
    private boolean reversed = false;
    private boolean running = false;
    private boolean timeout = false;

    private boolean currentPlayerOne = true;

    // Timing before changing player
    public final static int PLAYER_TIME_LIMIT = 21;
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

	public BallGameModel(Player one, Player two) {
		final float w = MapModel.MAP_WIDTH;
		final float h = (w*16)/9;
		cameraModel = new CameraModel(this, new V3F(MapModel.MAP_X, -h/2f, 0), w, h);
		
		mapModel = new MapModel(this, new AssetMapGenerator(SEED));
		collisionHandler = new CollidableList(mapModel);
		gravity = new V3F(0, 0.8f, 0);
		ballModel = new BallModel(this, new V3F(MapModel.MAP_X+MapModel.MAP_WIDTH/2, 0, 0), Row.ROW_HEIGHT/2-1);
		
		collisionHandler.addCollidable(ballModel);

        // Set timings
        playerTime = PLAYER_TIME_LIMIT*Engine.getDefaultTickRate();
        timeoutTime = TIMEOUT_TIME_LIMIT*Engine.getDefaultTickRate();
        
        playerOneModel = one;
        playerTwoModel = two;

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
        return currentPlayerOne ? playerOneModel : playerTwoModel;
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

            if (currentPlayerOne)
                playerOneModel.step();
            else
                playerTwoModel.step();
        }

        alarm.step();

	}
	public V3F getGravity() {
		return gravity;
	}

    public void setGameRunning(boolean state) {
        if (!running) switchPlayer();
        running = state;
    }

    public void killPlayer(){
        if (currentPlayerOne)
            playerOneModel.subtractLives(1);
        else
            playerTwoModel.subtractLives(1);

        //Check if game is over
        if (playerOneModel.getLives() <=0 || playerTwoModel.getLives() <=0) {
            Engine.scene().setState(new GameOverState(playerOneModel,playerTwoModel));
        } else {
            startTimeout();
        }
    }

    public void switchPlayer() {
        currentPlayerOne = !currentPlayerOne;
        alarm.set(ALARM_PLAYTIME_INDEX, playerTime);

        reverse();

        timeout = false;
        running = true;
    }

    @Override
    public void alarm(int index) {
        switch (index) {
            case ALARM_PLAYTIME_INDEX:
                startTimeout();
                alarm.stop(ALARM_PLAYTIME_INDEX);
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
}
