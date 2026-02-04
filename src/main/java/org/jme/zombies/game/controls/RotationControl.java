package org.jme.zombies.game.controls;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class RotationControl extends AbstractControl {

    private static final float speed = 2f;

    @Override
    protected void controlUpdate(float tpf) {
        spatial.rotate(0, speed * tpf, 0);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }
}
