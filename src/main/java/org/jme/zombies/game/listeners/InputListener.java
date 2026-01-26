package org.jme.zombies.game.listeners;

import com.jme3.bullet.control.CharacterControl;
import com.jme3.input.controls.ActionListener;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import org.jme.zombies.game.component.MoveComponent;
import org.jme.zombies.game.component.NodeComponent;
import org.jme.zombies.game.constants.InputTriggers;
import org.jme.zombies.game.entity.EntityFactory;

public class InputListener implements ActionListener {

    private final MoveComponent moveComponent;
    private final CharacterControl control;

    public InputListener() {
        EntityId playerId = EntityFactory.getPlayerEntityId();

        Entity entity = EntityFactory.entityData.getEntity(
                playerId,
                MoveComponent.class,
                NodeComponent.class
        );

        moveComponent = entity.get(MoveComponent.class);
        NodeComponent nodeComponent = entity.get(NodeComponent.class);

        control = nodeComponent.entity.getControl(CharacterControl.class);
    }

    @Override
    public void onAction(String binding, boolean value, float tpf) {
        InputTriggers inputTriggers = InputTriggers.fromName(binding);

        switch (inputTriggers) {
            case LEFT -> moveComponent.left = value;
            case RIGHT -> moveComponent.right = value;
            case UP -> moveComponent.up = value;
            case DOWN -> moveComponent.down = value;
            case SPACE -> control.jump();
        }
    }
}
