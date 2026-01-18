package org.jme.zombies.game.controls;

import com.jme3.recast4j.ai.NavMeshAgent;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

import java.util.Objects;

public class AgentAnimationControl extends AbstractControl {

    private NavMeshAgent agent;
    private AnimatorControl animator;

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        if (spatial != null) {
            this.agent = getComponent(spatial, NavMeshAgent.class);
            Objects.requireNonNull(agent, "NavMeshAgent not found: " + spatial);

            this.animator = getComponent(spatial, AnimatorControl.class);
            Objects.requireNonNull(animator, "Animator not found: " + spatial);
        }
    }

    @Override
    public void controlUpdate(float tpf) {
        if (agent.remainingDistance() < agent.getStoppingDistance() && !agent.pathPending()) {
            animator.setAnimation("Idle");
            animator.setSpeed(1);
        } else {
            animator.setAnimation("Walk");
            animator.setSpeed(2);
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public static <T extends Control> T getComponent(Spatial sp, Class<T> type) {
        return sp.getControl(type);
    }
}
