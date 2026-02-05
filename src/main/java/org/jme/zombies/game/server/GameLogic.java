package org.jme.zombies.game.server;

import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;
import org.jme.zombies.game.component.NodeComponent;
import org.jme.zombies.game.component.PositionComponent;

public class GameLogic {

    private final EntityData ed;
    private final EntitySet invaders;

    public GameLogic(EntityData ed) {
        this.ed = ed;
        this.invaders = ed.getEntities(PositionComponent.class, NodeComponent.class);
    }

    public void update() {
        invaders.applyChanges();

        for (Entity e : invaders) {
            NodeComponent nodeComponent = e.get(NodeComponent.class);
            var entity = nodeComponent.entity;

            System.out.println("Entity already in server exist by name: " + entity.getName());
        }
    }
}
