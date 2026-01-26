package org.jme.zombies.game.listeners;

import com.jme3.input.controls.ActionListener;
import com.jme3.renderer.Camera;
import org.jme.zombies.game.entity.EntityFactory;

/**
 * Tracks the left mouse button click and creates entity shot.
 */
public class ShootListener implements ActionListener {

    private final Camera camera;

    public ShootListener(Camera camera) {
        this.camera = camera;
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (!isPressed) {
            EntityFactory.createBall(camera.getLocation(), camera.getDirection());
        }
    }
}
