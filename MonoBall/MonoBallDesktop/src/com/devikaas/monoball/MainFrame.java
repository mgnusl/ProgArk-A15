package com.devikaas.monoball;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.devikaas.monoball.states.MenuGameState;
import owg.engine.Engine;
import owg.engine.EntryPoint;
import owg.engine.GameState;

/**
 * Created with IntelliJ IDEA.
 * User: bvx89
 * Date: 13/03/14
 * Time: 15:32
 * To change this template use File | Settings | File Templates.
 */
public class MainFrame extends Frame implements EntryPoint{
	private static final long serialVersionUID = 4173317818578875569L;

	public static void main(String[] args) {
        new MainFrame();
    }


    public MainFrame() {
        this.setTitle("MonoBall Desktop");
        Engine.initializeEngine(Engine.TargetPlatform.Desktop, this);
        this.addWindowListener(new WindowAdapter() {
        	@Override
        	public void windowClosing(WindowEvent e) {
        		System.exit(0);
        	}
		});
        this.pack();
        this.setVisible(true);

    }

    @Override
    public GameState getInitialState() {
        return new MenuGameState();
    }
}
