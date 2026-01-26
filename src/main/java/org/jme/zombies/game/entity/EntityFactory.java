package org.jme.zombies.game.entity;

import com.jme3.anim.util.AnimMigrationUtils;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.recast4j.ai.NavMeshAgent;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.base.DefaultEntityData;
import org.jme.zombies.game.component.AIComponent;
import org.jme.zombies.game.component.MoveComponent;
import org.jme.zombies.game.component.NodeComponent;
import org.jme.zombies.game.component.PlayerComponent;
import org.jme.zombies.game.component.PositionComponent;
import org.jme.zombies.game.component.ShootComponent;
import org.jme.zombies.game.component.VelocityComponent;
import org.jme.zombies.game.controls.AgentAnimationControl;
import org.jme.zombies.game.controls.AnimatorControl;
import org.jme.zombies.game.utils.ShapeUtils;
import org.recast4j.detour.NavMesh;

import java.util.ArrayList;
import java.util.List;

public class EntityFactory {

    public static final EntityData entityData = new DefaultEntityData();

    public static Node worldNode;
    public static AssetManager assetManager;
    public static BulletAppState bulletAppState;
    public static NavMesh navMeshTerrain;

    public static List<EntityId> enemies = new ArrayList<>();

    private static EntityId playerEntityId;

    private static int index = 0;

    public static void createPlayer() {
        playerEntityId = entityData.createEntity();

        NodeComponent nodeComponent = new NodeComponent();
        nodeComponent.entity = new Node("Player");

        var player = nodeComponent.entity;

        var shape = new CapsuleCollisionShape(1.5f, 2f, 1);
        var control = new CharacterControl(shape, 0.01f);

        control.setJumpSpeed(20);
        control.setFallSpeed(30);
        control.setGravity(50);

        player.setLocalTranslation(new Vector3f(0f, 10, 0f));

        player.addControl(control);
        player.setShadowMode(ShadowMode.CastAndReceive);

        worldNode.attachChild(player);

        bulletAppState.getPhysicsSpace().add(player);

        entityData.setComponents(
                playerEntityId,
                nodeComponent,
                new PlayerComponent(),
                new MoveComponent(),
                new VelocityComponent(),
                new PositionComponent()
        );
    }

    public static void createEnemy(float x, float z) {
        EntityId id = entityData.createEntity();
        Spatial model = assetManager.loadModel("Models/Jaime/Jaime.j3o");

        NodeComponent nodeComponent = new NodeComponent();
        nodeComponent.entity = (Node) AnimMigrationUtils.migrate(model);

        var npc = nodeComponent.entity;
        npc.scale(2f);
        npc.setName("Enemy_" + index++);
        npc.setLocalTranslation(new Vector3f(x, 3f, z));

        npc.addControl(new BetterCharacterControl(0.5f, 3f, 10f));
        npc.addControl(new NavMeshAgent(navMeshTerrain));
        npc.addControl(new AnimatorControl());
        npc.addControl(new AgentAnimationControl());

        npc.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        bulletAppState.getPhysicsSpace().add(npc);
        worldNode.attachChild(npc);

        AIComponent aiComponent = new AIComponent();

        entityData.setComponents(
                id,
                aiComponent,
                nodeComponent
        );

        enemies.add(id);
    }

    public static void createBall(Vector3f location, Vector3f direction) {
        EntityId id = entityData.createEntity();

        ShapeUtils shapeUtils = new ShapeUtils(assetManager);

        Geometry sphere = shapeUtils.createBall();
        RigidBodyControl ballControl = new RigidBodyControl(0.1f);

        NodeComponent nodeComponent = new NodeComponent();
        nodeComponent.entity = new Node();

        ShootComponent shootComponent = new ShootComponent();

        var ball = nodeComponent.entity;

        ball.attachChild(sphere);
        ball.setName("Ball_" + index++);
        ball.addControl(ballControl);
        ball.setShadowMode(ShadowMode.CastAndReceive);

        ballControl.setPhysicsLocation(location.add(direction.mult(3f)));
        ballControl.setLinearVelocity(direction.mult(20));

        bulletAppState.getPhysicsSpace().add(ball);
        worldNode.attachChild(ball);

        entityData.setComponents(
                id,
                nodeComponent,
                shootComponent
        );

    }

    public static EntityId getPlayerEntityId() {
        return playerEntityId;
    }
}
