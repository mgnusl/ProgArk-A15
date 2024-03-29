package com.devikaas.monoball;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import owg.engine.Engine;
import owg.engine.EntryPoint;
import owg.engine.GameState;

public class MainFrame extends Frame implements EntryPoint{
	private static final long serialVersionUID = 4173317818578875569L;

	public static void main(String[] args) {
        new MainFrame();
    }


    public MainFrame() {
        this.setTitle("MonoBall Desktop");
        final int h = Toolkit.getDefaultToolkit().getScreenSize().height-80;
        Engine.initializeEngine(Engine.TargetPlatform.Desktop, this);
        Engine.scene().setPreferredSize((h*9)/16, h);
        this.addWindowListener(new WindowAdapter() {
        	@Override
        	public void windowClosing(WindowEvent e) {
        		System.exit(0);
        	}
		});
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }

    @Override
    public GameState getInitialState() {
        return new SplashState();
    }
}
