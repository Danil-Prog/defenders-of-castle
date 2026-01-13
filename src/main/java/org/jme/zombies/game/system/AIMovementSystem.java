package org.jme.zombies.game.system;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.recast4j.ai.NavMeshAgent;
import com.jme3.recast4j.ai.NavMeshAgentDebug;
import com.jme3.recast4j.ai.NavMeshPath;
import com.jme3.recast4j.ai.NavMeshPathStatus;
import com.jme3.recast4j.ai.NavMeshQueryFilter;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import java.util.concurrent.Executors;
import javax.annotation.Nonnull;
import org.jme.zombies.game.component.AIComponent;
import org.jme.zombies.game.component.ModelComponent;
import org.jme.zombies.game.component.NameComponent;
import org.jme.zombies.game.component.PositionComponent;
import org.jme.zombies.game.entity.EntityFactory;
import org.recast4j.detour.DefaultQueryFilter;
import org.recast4j.detour.FindRandomPointResult;
import org.recast4j.detour.NavMesh;
import org.recast4j.detour.NavMeshQuery;
import org.recast4j.detour.Result;
import static com.jme3.recast4j.recast.JmeAreaMods.POLYFLAGS_DISABLED;
import static com.jme3.recast4j.recast.JmeAreaMods.POLYFLAGS_DOOR;
import static com.jme3.recast4j.recast.JmeAreaMods.POLYFLAGS_JUMP;
import static com.jme3.recast4j.recast.JmeAreaMods.POLYFLAGS_SWIM;
import static com.jme3.recast4j.recast.JmeAreaMods.POLYFLAGS_WALK;
import static org.recast4j.detour.NavMeshQuery.FRand;

public class AIMovementSystem extends AbstractAppState {

    /**
     * AI Entities.
     */
    private EntitySet entities;
    private Entity player;
    private AssetManager assetManager;

    private final NavMeshQuery navMeshQuery;
    private final NavMesh navMeshTerrain;

    public AIMovementSystem(NavMesh navMeshTerrain) {
        this.navMeshTerrain = navMeshTerrain;
        this.navMeshQuery = new NavMeshQuery(navMeshTerrain);
    }

    @Override
    public void initialize(
            AppStateManager stateManager,
            Application application
    ) {
        super.initialize(stateManager, application);
        EntityId playerId = EntityFactory.getPlayerEntityId();

        this.entities = EntityFactory.entityData.getEntities(
                AIComponent.class,
                NameComponent.class,
                ModelComponent.class
        );

        this.player = EntityFactory.entityData.getEntity(
                playerId,
                PositionComponent.class
        );

        this.assetManager = application.getAssetManager();

        var executorService = Executors.newSingleThreadScheduledExecutor();
//        executorService.scheduleAtFixedRate(this::generateRandomEnemy, 500, 5000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void update(float tpf) {
        entities.forEach(entity -> {
            ModelComponent modelComponent = entity.get(ModelComponent.class);
            AIComponent aiComponent = entity.get(AIComponent.class);

            PositionComponent playerPosition = player.get(PositionComponent.class);

            if (aiComponent.agent == null) {
                aiComponent.agent = new NavMeshAgent(navMeshTerrain);

                modelComponent.geometry.addControl(aiComponent.agent);
                modelComponent.geometry.addControl(new NavMeshAgentDebug(assetManager));

                aiComponent.agent.setSpatial(modelComponent.geometry);

                NavMeshQueryFilter filter = getNavMeshQueryFilter();

                aiComponent.agent.setQueryFilter(filter);
            }

            NavMeshPath path = new NavMeshPath();

            Vector3f targetPosition = playerPosition.position;
            aiComponent.agent.calculatePath(targetPosition, path);

            if (path.getStatus() == NavMeshPathStatus.PathComplete) {
                aiComponent.agent.setPath(path);

                System.out.println(path.getCorners());

            }
        });
    }

    @Nonnull
    private static NavMeshQueryFilter getNavMeshQueryFilter() {
        int includeFlags = POLYFLAGS_WALK | POLYFLAGS_DOOR | POLYFLAGS_SWIM | POLYFLAGS_JUMP;

        float[] polyExtents = new float[]{2, 2, 2};

        NavMeshQueryFilter filter = new NavMeshQueryFilter(includeFlags, POLYFLAGS_DISABLED);
//      filter.setPolyExtents(polyExtents);

        return filter;
    }

    private void generateRandomEnemy() {
        var filter = new DefaultQueryFilter();
        var rand = new FRand();

        Result<FindRandomPointResult> result = navMeshQuery.findRandomPoint(filter, rand);

        if (result.succeeded()) {
            FindRandomPointResult temp = result.result;
            float[] position = temp.getRandomPt();

            EntityFactory.createEnemy(position[0], position[1]);
        }
    }
}