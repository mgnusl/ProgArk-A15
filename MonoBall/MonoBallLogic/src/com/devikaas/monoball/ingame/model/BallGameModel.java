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
    private boolean currentPlayerOne = true;

    // Timing before changing player
    private final static int PLAYER_TIME_LIMIT = 21;
    public final static int PLAYER_ALARM_INDEX = 0;
    private final int playerTime;
    private Alarm alarm;

    @Kryo
	private BallGameModel(){
    	playerTime=0;mapModel=null;gravity=null;collisionHandler=null;cameraModel=null;ballModel=null;
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
		
		cameraModel.setVerticalSpeed(1f);
        playerTime = PLAYER_TIME_LIMIT*Engine.getDefaultTickRate();
        
        playerOneModel = one;
        playerTwoModel = two;

		running = true;

		alarm = new Alarm(1, this);
		alarm.set(PLAYER_ALARM_INDEX, playerTime);
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

    public boolean isReversed() {return reversed;}

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

            alarm.step();
        }
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
        if(playerOneModel.getLives() <=0 || playerTwoModel.getLives() <=0){
            Engine.scene().setState(new GameOverState(playerOneModel,playerTwoModel));
        }else{

            switchPlayer();
        }
    }

    public void switchPlayer() {
        currentPlayerOne = !currentPlayerOne;
        alarm = new Alarm(1, this);
        alarm.set(PLAYER_ALARM_INDEX, playerTime);

        reverse();
    }

    @Override
    public void alarm(int index) {
        switchPlayer();
    }

	public int getPlayerTime(){
		return playerTime;
	}
	public int getPlayerTimeLimit(){
		return PLAYER_TIME_LIMIT;
	}
}
