package org.jme.zombies.game.entity;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.base.DefaultEntityData;
import java.util.Random;
import org.jme.zombies.game.component.AIComponent;
import org.jme.zombies.game.component.CharacterControlComponent;
import org.jme.zombies.game.component.ModelComponent;
import org.jme.zombies.game.component.MoveComponent;
import org.jme.zombies.game.component.NameComponent;
import org.jme.zombies.game.component.PositionComponent;
import org.jme.zombies.game.component.VelocityComponent;
import org.jme.zombies.game.terrain.TerrainFactory;
import static com.jme3.renderer.queue.RenderQueue.ShadowMode.CastAndReceive;

public class EntityFactory {

    public static final EntityData entityData = new DefaultEntityData();

    public static Node rootNode;
    public static AssetManager assetManager;
    public static BulletAppState bulletAppState;
    public static TerrainFactory terrainFactory;

    private static EntityId playerEntityId;

    public static void createPlayer() {
        playerEntityId = entityData.createEntity();

        ModelComponent modelComponent = new ModelComponent();

        PositionComponent positionComponent = new PositionComponent();
        positionComponent.position = new Vector3f(0, 0, 0);

        VelocityComponent velocityComponent = new VelocityComponent();

        MoveComponent moveComponent = new MoveComponent();

        CharacterControlComponent characterComponent = new CharacterControlComponent();

        characterComponent.shape = new CapsuleCollisionShape(1.5f, 2f, 1);
        characterComponent.control = new CharacterControl(characterComponent.shape, 0.01f);

        Vector3f location = new Vector3f(-20.5f, 4f, 8f);

        characterComponent.control.setPhysicsLocation(location);
        characterComponent.control.setJumpSpeed(20);
        characterComponent.control.setFallSpeed(30);
        characterComponent.control.setGravity(50);

        bulletAppState.getPhysicsSpace().add(characterComponent.control);

        entityData.setComponents(
                playerEntityId,
                modelComponent,
                positionComponent,
                velocityComponent,
                characterComponent,
                moveComponent
        );
    }

    public static void createEnemy(float x, float z) {
        EntityId id = entityData.createEntity();

        NameComponent nameComponent = new NameComponent();
        nameComponent.name = "Enemy_" + new Random().nextInt(1, 99);

        ModelComponent modelComponent = new ModelComponent();

        modelComponent.box = new Box(0f, 0f, 0f);
        modelComponent.geometry = new Geometry("EnemyBox", modelComponent.box);
        modelComponent.betterCharacterControl = new BetterCharacterControl(0.1f, 1.6f, 10f);

        modelComponent.geometry.setLocalTranslation(new Vector3f(x, 0.1f, z));

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);

        modelComponent.geometry.setMaterial(mat);
        modelComponent.geometry.addControl(modelComponent.betterCharacterControl);

        bulletAppState.getPhysicsSpace().add(modelComponent.geometry);

        rootNode.attachChild(modelComponent.geometry);

        AIComponent aiComponent = new AIComponent();

        entityData.setComponents(
                id,
                nameComponent,
                aiComponent,
                modelComponent
        );
    }

    public static EntityId getPlayerEntityId() {
        return playerEntityId;
    }
}
