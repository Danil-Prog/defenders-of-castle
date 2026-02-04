package org.jme.zombies.game.utils;

import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.material.Material;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;

public class ShapeUtils {

    private final AssetManager assetManager;
    private Material stoneMaterial;
    private Texture stoneTexture;

    public ShapeUtils(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public Geometry createBall() {
        stoneMaterialInit();

        Sphere ball = new Sphere(32, 32, 0.4f, true, false);
        ball.setTextureMode(Sphere.TextureMode.Projected);

        Geometry geometry = new Geometry("Ball", ball);

        geometry.setMaterial(stoneMaterial);
        geometry.setShadowMode(ShadowMode.CastAndReceive);

        return geometry;
    }

    private void stoneMaterialInit() {
        if (stoneMaterial != null && stoneTexture != null) {
            return;
        }

        TextureKey textureKey = new TextureKey("Textures/Terrain/Rock/Rock.PNG");
        textureKey.setGenerateMips(true);

        stoneTexture = assetManager.loadTexture(textureKey);

        stoneMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        stoneMaterial.setTexture("ColorMap", stoneTexture);
    }
}
