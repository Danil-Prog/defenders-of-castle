package org.jme.zombies.game;

import org.jme.zombies.Jmezombies;
import org.jme.zombies.game.entity.EntityFactory;
import org.jme.zombies.game.terrain.AreaFactory;

public class GameContext {

    private final AreaFactory areaFactory;

    public GameContext(Jmezombies game) {
        this.areaFactory = new AreaFactory(game);
    }

    public void initialize() {
        areaFactory.buildTerrainEnvironment();

        EntityFactory.worldNode = areaFactory.getWorldNode();
        EntityFactory.navMeshTerrain = areaFactory.getNavMeshTerrain();

        EntityFactory.createPlayer();
        EntityFactory.createEnemy(0f, 0f);
    }

    public AreaFactory getTerrainFactory() {
        return areaFactory;
    }
}
