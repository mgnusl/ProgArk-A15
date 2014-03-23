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

public class BallGameState implements GameState {
	BallGameModel model;
	BallGameView view;
    List<Controller> controllers = new ArrayList<>();
    
    Kryo kryoSerializer;
    byte[] savedModel;

    InputController inputController = InputController.getInstance();
    Player playerOne;
	
	public BallGameState() {
		Player one = new Player(model, "Arne");
        Player two = new Player(model, "Paul");
		BallGameModel m = new BallGameModel(one, two);
        //m.setGameRunning(true);
        
        setModel(m);
        kryoSerializer = new Kryo();
        savedModel = null;
	}
	
	public void setModel(BallGameModel m) {
		model = m;
		view = new BallGameView(model);
		
		inputController.setGameModel(model);
        inputController.registerController(new TouchController());
        inputController.registerController(new SystemKeyController());
        inputController.registerController(new KeyboardController());
        //inputController.registerController(new ReplayController(""));
	}
	
	public BallGameModel getModel() {
		return model;
	}

	@Override
	public void step() {
        // Reset model X-celeration
        model.setX(0);
        inputController.step();

        model.step();
        
        if(Engine.keyboard().isPressed(VirtualKey.VK_F5)) {
        	savedModel = getModelBytes(); 
        }
        if(Engine.keyboard().isPressed(VirtualKey.VK_F6)) {
        	if(savedModel != null)
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

	@Override
	public void render() {
		view.render();
	}
}
