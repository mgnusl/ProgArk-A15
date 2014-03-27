package com.devikaas.monoball.ingame.controller;


import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 *  Controller for using a replay sequence as inputs
 *
 * Created by Stein on 23.03.14.
 */
public class ReplayController implements Controller{
    InputController inputController;
    ArrayList<String> commands;
    int tickCount = 0;

    public ReplayController(String replay){

        inputController = InputController.getInstance();
        commands = interpretReplay(replay);

    }

    @Override
    public void step() {

        if(commands.get(tickCount) != null){
            runCommand(commands.get(tickCount));
        }

        tickCount++;
       }

    private void runCommand(String command){
        String cmd = command.split(":")[0];
        Float arg = Float.parseFloat(command.split(":")[1]);

        try{
            Method m = inputController.getClass().getDeclaredMethod(cmd,float.class);
            m.invoke(inputController,arg);
        } catch (Exception e){
           e.printStackTrace();
        }
    }

    private ArrayList<String> interpretReplay( String replay){
        ArrayList<String> list = new ArrayList<String>();
        String[] split = replay.split(";");
        String temp = "";
        int tick;

        int maxTick = Integer.parseInt(split[split.length-1].split("!")[0]);

        //Populate arraylist
        for(int i= 0;i<maxTick;i++){
            list.add(i,null);
        }

        for(int i = 0; i<split.length;i++){
            temp = split[i].split("!")[0];
            tick = Integer.parseInt(temp);
            list.add(tick,split[i].split("!")[1]);
        }

        return list;
    }

}
