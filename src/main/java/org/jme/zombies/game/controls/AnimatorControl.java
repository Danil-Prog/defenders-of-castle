package org.jme.zombies.game.controls;

import com.jme3.anim.AnimComposer;
import com.jme3.anim.SkinningControl;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import org.jme.zombies.game.utils.SceneFactory;

import java.util.Objects;

public class AnimatorControl extends AbstractControl {

    private SkinningControl skControl;
    private AnimComposer animComposer;
    private String currAnimName;

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);

        if (spatial != null) {
            skControl = SceneFactory.getComponentInChildren(spatial, SkinningControl.class);
            Objects.requireNonNull(skControl, "SkinningControl not found: " + spatial);

            animComposer = SceneFactory.getComponentInChildren(spatial, AnimComposer.class);
            Objects.requireNonNull(animComposer, "AnimComposer not found: " + spatial);

            configureAnimClips();
        }
    }

    private void configureAnimClips() {
        for (String name : animComposer.getAnimClipsNames()) {
            animComposer.action(name);
        }
    }

    public void setSpeed(float speed) {
        animComposer.setGlobalSpeed(speed);
    }

    public void setAnimation(String animName) {
        if (!animName.equals(currAnimName)) {
            animComposer.setCurrentAction(animName);
            currAnimName = animName;
        }
    }

    public String getCurrentAnimName() {
        return currAnimName;
    }

    @Override
    protected void controlUpdate(float tpf) {
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
