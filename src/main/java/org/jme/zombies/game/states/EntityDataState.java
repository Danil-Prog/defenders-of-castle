package org.jme.zombies.game.states;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.EntityData;

public class EntityDataState extends AbstractAppState {
    private EntityData entityData;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        var entityState = stateManager.getState(EntityState.class);
        entityData = entityState.getEntityData();
    }

    public EntityData getEntityData() {
        return entityData;
    }

    @Override
    public void cleanup() {
        entityData.close();
        entityData = null; // cannot be reused
    }
}
