package com.devikaas.monoball;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import owg.engine.Engine;
import owg.engine.GameState;
import owg.engine.input.VirtualKey;

import com.devikaas.monoball.ingame.controller.Controller;
import com.devikaas.monoball.ingame.controller.InputController;
import com.devikaas.monoball.ingame.controller.KeyboardController;
import com.devikaas.monoball.ingame.controller.SystemKeyController;
import com.devikaas.monoball.ingame.controller.TouchController;
import com.devikaas.monoball.ingame.model.BallGameModel;
import com.devikaas.monoball.ingame.model.Player;
import com.devikaas.monoball.ingame.view.BallGameView;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
/**This class specifies the in-game state*/
public class BallGameState implements GameState {
	/**The game model*/
	BallGameModel model;
	/**The game view*/
	BallGameView view;
	/**The game controllers*/
    List<Controller> controllers = new ArrayList<>();
    
    /**Used to create a serialized clone of the game model*/
    Kryo kryoSerializer;
    /**If non-null, this contains a serialized clone of an old game model*/
    byte[] savedModel;

    /**An interceptor which captures controller input*/
    InputController inputController;
    
	public BallGameState() {
		Player one = new Player("Player 1");
        Player two = new Player("Player 2");
        one.addLives(3);
        two.addLives(3);
        
		BallGameModel m = new BallGameModel(one, two, (int)(Math.random() * 1000));
        setModel(m);
        
        kryoSerializer = new Kryo();
        savedModel = null;
	}
	/**Set the current game model. This will (re)create the controllers and the view and bind them to the model.*/
	public void setModel(BallGameModel m) {
		model = m;
		view = new BallGameView(model);
		
		inputController = new InputController();
		inputController.setGameModel(model);
        inputController.registerController(new TouchController(inputController));
        inputController.registerController(new SystemKeyController());
        inputController.registerController(new KeyboardController(inputController));
        //inputController.registerController(inputController, new ReplayController(""));
	}
	/**Returns an instance to the current model.*/
	public BallGameModel getModel() {
		return model;
	}
	/**Dispatch a game tick to the model and poll the controllers for input.*/
	@Override
	public void step() {
        // Reset model X-celeration
        model.setX(0);

        inputController.step();

        model.step();
        
        //Debugging: use F5/F6 to save/load the game model
        if (Engine.keyboard().isPressed(VirtualKey.VK_F5)) {
            savedModel = getModelBytes();
        }
        if (Engine.keyboard().isPressed(VirtualKey.VK_F6)) {
            if (savedModel != null)
                setModelBytes(savedModel);
        }
	}
	/**Sets the current game model from the given byte array, as returned by {@link #getModelBytes()}*/
	public void setModelBytes(byte[] modelBytes) {
		Input ki = new Input(new ByteArrayInputStream(modelBytes));
    	setModel(kryoSerializer.readObject(ki, BallGameModel.class));
    	ki.close();
	}
	/**Returns a copy of the current #{@link com.devikaas.monoball.ingame.model.BallGameModel} as a byte array.*/
	public byte[] getModelBytes() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
    	Output ko = new Output(bos);
    	kryoSerializer.writeObject(ko, getModel());
    	ko.flush();
    	ko.close();
    	return bos.toByteArray();
	}
	/**Dispatch a render event to the game view*/
	@Override
	public void render() {
		float alpha = 1;
        if (model.isRunning()) {
            float tickMs = 1000/Engine.getDefaultTickRate();
            long currentTime = System.currentTimeMillis();
            alpha = (currentTime-Engine.scene().getLastStepTime())/tickMs;
        }
        view.render(alpha);

	}
}
