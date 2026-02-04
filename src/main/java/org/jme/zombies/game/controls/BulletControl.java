package org.jme.zombies.game.controls;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class BulletControl extends AbstractControl {

    private boolean hasHit = false;

    public boolean isHasHit() {
        return hasHit;
    }

    public void markHit() {
        hasHit = true;
    }

    @Override
    protected void controlUpdate(float tpf) {

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }
}
