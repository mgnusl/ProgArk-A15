package com.devikaas.monoball.ingame.controller;

import com.devikaas.monoball.ingame.model.Steppable;

/**
 * Interface for controllers. Controllers should poll for events in the step event.
 * Commands should be sent to the InputController if they have to do with the gameplay.
 */
public interface Controller extends Steppable {
}
