package org.jme.zombies;

import com.github.stephengold.wrench.LwjglAssetLoader;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.font.BitmapText;
import com.jme3.system.AppSettings;
import org.jme.zombies.game.GameContext;
import org.jme.zombies.game.entity.EntityFactory;
import org.jme.zombies.game.listeners.BallCollisionListener;
import org.jme.zombies.game.system.AIMovementSystem;
import org.jme.zombies.game.system.PlayerMovementSystem;
import org.jme.zombies.game.system.WeaponMovementSystem;
import org.jme.zombies.game.utils.MappingInput;
import org.recast4j.detour.NavMesh;

public class Jmezombies extends SimpleApplication {

    private final BulletAppState bulletAppState;

    public Jmezombies(BulletAppState bulletAppState) {
        this.bulletAppState = bulletAppState;
    }

    public static void main(String[] args) {
        var bulletAppState = new BulletAppState();
        Jmezombies app = new Jmezombies(bulletAppState);

        AppSettings settings = new AppSettings(true);
        settings.setTitle("Jmezombies");
        settings.setFullscreen(false);
        settings.setWindowSize(1920, 1080);

        app.setSettings(settings);

        app.start();
    }

    @Override
    public void simpleInitApp() {
        assetManager.registerLoader(LwjglAssetLoader.class,
                "3ds", "3mf", "blend", "bvh", "dae", "fbx", "glb", "gltf",
                "lwo", "meshxml", "mesh.xml", "obj", "ply", "stl");

        EntityFactory.assetManager = assetManager;

        stateManager.attach(bulletAppState);

        GameContext gameContext = new GameContext(this);

        initCrossHairs();

        EntityFactory.bulletAppState = bulletAppState;

        gameContext.initialize();

        NavMesh navMesh = gameContext.getTerrainFactory().getNavMeshTerrain();

        AIMovementSystem aiMovementSystem = new AIMovementSystem(navMesh);
        PlayerMovementSystem playerMovementSystem = new PlayerMovementSystem();
        WeaponMovementSystem weaponMovementSystem = new WeaponMovementSystem();

        stateManager.attach(aiMovementSystem);
        stateManager.attach(playerMovementSystem);
        stateManager.attach(weaponMovementSystem);

        bulletAppState.setDebugEnabled(false);
        bulletAppState.getPhysicsSpace().addCollisionListener(new BallCollisionListener(this));

        MappingInput.mapKeyboard(inputManager);
        MappingInput.mapMouse(inputManager, cam);
    }

    protected void initCrossHairs() {
        float x = (float) settings.getWidth() / 2;
        float y = (float) settings.getHeight() / 2;

        setDisplayStatView(false);

        BitmapText cursor = new BitmapText(guiFont);

        cursor.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        cursor.setText("+");
        cursor.setLocalTranslation(x, y, 0);

        guiNode.attachChild(cursor);
    }
}

