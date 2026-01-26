package org.jme.zombies.game.utils;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.renderer.Camera;
import org.jme.zombies.game.constants.InputTriggers;
import org.jme.zombies.game.listeners.InputListener;
import org.jme.zombies.game.listeners.ShootListener;

public class MappingInput {

    public static void mapKeyboard(InputManager inputManager) {
        String left = InputTriggers.LEFT.getName();
        String right = InputTriggers.RIGHT.getName();
        String up = InputTriggers.UP.getName();
        String down = InputTriggers.DOWN.getName();
        String jump = InputTriggers.SPACE.getName();

        inputManager.addMapping(left, new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping(right, new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping(up, new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping(down, new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping(jump, new KeyTrigger(KeyInput.KEY_SPACE));

        inputManager.addListener(new InputListener(), left, right, up, down, jump);
    }

    public static void mapMouse(InputManager inputManager, Camera camera) {
        String mouseLeft = InputTriggers.MOUSE_LEFT.getName();
        inputManager.addMapping(mouseLeft, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(new ShootListener(camera), mouseLeft);
    }
}
