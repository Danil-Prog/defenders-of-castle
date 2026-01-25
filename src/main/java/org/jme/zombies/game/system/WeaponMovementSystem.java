package org.jme.zombies.game.system;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.renderer.Camera;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import org.jme.zombies.game.component.MoveComponent;
import org.jme.zombies.game.component.NodeComponent;
import org.jme.zombies.game.component.PositionComponent;
import org.jme.zombies.game.component.VelocityComponent;
import org.jme.zombies.game.entity.EntityFactory;

public class WeaponMovementSystem extends AbstractAppState {

    private Entity player;
    private Camera camera;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        EntityId playerId = EntityFactory.getPlayerEntityId();

        this.player = EntityFactory.entityData.getEntity(
                playerId,
                NodeComponent.class,
                MoveComponent.class,
                VelocityComponent.class,
                PositionComponent.class
        );

        this.camera = app.getCamera();
    }

    @Override
    public void update(float tpf) {
        NodeComponent nodeComponent = player.get(NodeComponent.class);

    }
}
