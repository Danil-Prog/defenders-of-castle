package org.jme.zombies.game.terrain;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.recast4j.geom.InputGeomProviderBuilder;
import com.jme3.recast4j.geom.JmeInputGeomProvider;
import com.jme3.recast4j.geom.JmeRecastBuilder;
import com.jme3.recast4j.geom.JmeRecastVoxelization;
import com.jme3.recast4j.geom.NavMeshModifier;
import com.jme3.recast4j.geom.OffMeshLink;
import com.jme3.recast4j.geom.Telemetry;
import com.jme3.recast4j.recast.NavMeshDebugRenderer;
import com.jme3.recast4j.recast.RecastConfigBuilder;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.util.SkyFactory;
import org.jme.zombies.Jmezombies;
import org.jme.zombies.game.Context;
import org.recast4j.detour.MeshData;
import org.recast4j.detour.NavMesh;
import org.recast4j.detour.NavMeshBuilder;
import org.recast4j.detour.NavMeshDataCreateParams;
import org.recast4j.recast.Heightfield;
import org.recast4j.recast.PolyMesh;
import org.recast4j.recast.PolyMeshDetail;
import org.recast4j.recast.RecastBuilder;
import org.recast4j.recast.RecastBuilderConfig;
import org.recast4j.recast.RecastConfig;
import org.recast4j.recast.RecastConstants;

import static com.jme3.recast4j.recast.JmeAreaMods.AREAMOD_DOOR;
import static com.jme3.recast4j.recast.JmeAreaMods.AREAMOD_GRASS;
import static com.jme3.recast4j.recast.JmeAreaMods.AREAMOD_GROUND;
import static com.jme3.recast4j.recast.JmeAreaMods.AREAMOD_ROAD;
import static com.jme3.recast4j.recast.JmeAreaMods.AREAMOD_WATER;
import static com.jme3.recast4j.recast.JmeAreaMods.POLYAREA_TYPE_DOOR;
import static com.jme3.recast4j.recast.JmeAreaMods.POLYAREA_TYPE_GRASS;
import static com.jme3.recast4j.recast.JmeAreaMods.POLYAREA_TYPE_GROUND;
import static com.jme3.recast4j.recast.JmeAreaMods.POLYAREA_TYPE_ROAD;
import static com.jme3.recast4j.recast.JmeAreaMods.POLYAREA_TYPE_WATER;
import static com.jme3.recast4j.recast.JmeAreaMods.POLYFLAGS_DOOR;
import static com.jme3.recast4j.recast.JmeAreaMods.POLYFLAGS_SWIM;
import static com.jme3.recast4j.recast.JmeAreaMods.POLYFLAGS_WALK;

public class TerrainFactory implements Context {

    private final BulletAppState bulletAppState;
    private final Node rootNode;
    private final AssetManager assetManager;
    private final ViewPort viewPort;
    private final Spatial worldMap;
    private final NavMeshDebugRenderer navMeshRenderer;

    private Node worldNode;
    private NavMesh navMeshTerrain;

    private final static float agentRadius = 0.3f;
    private final static float agentHeight = 1.7f;
    private final static float agentMaxClimb = 0.3f; // > 2*ch
    private final static float cellSize = 0.1f;      // cs=r/2
    private final static float cellHeight = 0.5f;    // ch=cs/2 but not < .1f

    public TerrainFactory(Jmezombies game) {
        this.bulletAppState = game.getStateManager().getState(BulletAppState.class);
        this.rootNode = game.getRootNode();
        this.assetManager = game.getAssetManager();
        this.viewPort = game.getViewPort();
        this.worldMap = assetManager.loadModel("Scenes/Map.obj");

        navMeshRenderer = new NavMeshDebugRenderer(assetManager);
    }

    public void buildTerrainEnvironment() {
        worldMap.setLocalTranslation(0, -2, 0);
        worldMap.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        RigidBodyControl worldRigidBodyControl = new RigidBodyControl(0f);
        worldMap.addControl(worldRigidBodyControl);

        bulletAppState.getPhysicsSpace().add(worldRigidBodyControl);

        worldNode = new Node("worldNode");
        worldNode.attachChild(worldMap);

        rootNode.attachChild(worldNode);

        environment();

        buildSoloModified();
        addLighting();
    }

    public NavMesh getNavMeshTerrain() {
        return this.navMeshTerrain;
    }

    public Node getWorldNode() {
        return this.worldNode;
    }

    /**
     * Initialization world environment (sky).
     */
    private void environment() {
        Spatial sky = SkyFactory.createSky(
                assetManager,
                assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_west.jpg"),
                assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_east.jpg"),
                assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_north.jpg"),
                assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_south.jpg"),
                assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_up.jpg"),
                assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_down.jpg")
        );

        rootNode.attachChild(sky);
    }

    private void addLighting() {
        rootNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        // Set the viewport's background color to light blue.
        ColorRGBA skyColor = new ColorRGBA(0.1f, 0.2f, 0.4f, 1f);
        viewPort.setBackgroundColor(skyColor);

        AmbientLight al = new AmbientLight();
        al.setName("Global");
        rootNode.addLight(al);

        DirectionalLight directionalLight = new DirectionalLight();
        directionalLight.setName("Sun");
        directionalLight.setDirection(new Vector3f(-7f, -3f, -5f).normalizeLocal());
        rootNode.addLight(directionalLight);

        // Render shadows based on the directional light.
        DirectionalLightShadowFilter shadowFilter = new DirectionalLightShadowFilter(assetManager, 2_048, 3);
        shadowFilter.setLight(directionalLight);
        shadowFilter.setShadowIntensity(0.4f);
        shadowFilter.setShadowZExtend(256);
        shadowFilter.setEdgeFilteringMode(EdgeFilteringMode.PCFPOISSON);

        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        fpp.addFilter(shadowFilter);
        fpp.addFilter(new FXAAFilter());
        viewPort.addProcessor(fpp);
    }

    private void buildSoloModified() {
        JmeInputGeomProvider m_geom = InputGeomProviderBuilder.build(worldNode);
        setNavMeshModifiers(m_geom, worldNode);

        RecastConfig cfg = new RecastConfigBuilder()
                .withPartitionType(RecastConstants.PartitionType.WATERSHED)
                .withWalkableAreaMod(AREAMOD_GROUND)
                .withAgentRadius(agentRadius)
                .withAgentHeight(agentHeight)
                .withCellSize(cellSize)
                .withCellHeight(cellHeight)
                .withAgentMaxClimb(agentMaxClimb)
                .withAgentMaxSlope(45f)
                .withEdgeMaxLen(2.4f) // r*8
                .withEdgeMaxError(1.3f) // 1.1 - 1.5
                .withDetailSampleDistance(8.0f) // increase if exception
                .withDetailSampleMaxError(8.0f) // increase if exception
                .withVertsPerPoly(3)
                .build();

        //Create a RecastBuilderConfig builder with world bounds of our geometry.
        RecastBuilderConfig builderCfg = new RecastBuilderConfig(cfg, m_geom.getMeshBoundsMin(), m_geom.getMeshBoundsMax());

        Telemetry telemetry = new Telemetry();
        // Rasterize input polygon soup.
        Heightfield solid = JmeRecastVoxelization.buildSolidHeightfield(m_geom, builderCfg, telemetry);

        JmeRecastBuilder rcBuilder = new JmeRecastBuilder();
        RecastBuilder.RecastBuilderResult rcResult = rcBuilder.build(builderCfg.borderSize, builderCfg.buildMeshDetail, m_geom, cfg, solid, telemetry);

        System.out.println("Telemetry:");
        telemetry.print();

        // Build the parameter object.
        NavMeshDataCreateParams params = getNavMeshCreateParams(m_geom,
                rcResult);

        updateAreaAndFlags(params);

        MeshData meshData = NavMeshBuilder.createNavMeshData(params);

        navMeshRenderer.drawMeshByArea(meshData, true);

        //Build the NavMesh.
        navMeshTerrain = new NavMesh(meshData, cfg.maxVertsPerPoly, 0);
    }

    private NavMeshDataCreateParams getNavMeshCreateParams(
            JmeInputGeomProvider m_geom,
            RecastBuilder.RecastBuilderResult rcResult
    ) {
        PolyMesh m_pmesh = rcResult.getMesh();
        PolyMeshDetail m_dmesh = rcResult.getMeshDetail();
        NavMeshDataCreateParams params = new NavMeshDataCreateParams();

        for (int i = 0; i < m_pmesh.npolys; ++i) {
            m_pmesh.flags[i] = 1;
        }

        params.verts = m_pmesh.verts;
        params.vertCount = m_pmesh.nverts;
        params.polys = m_pmesh.polys;
        params.polyAreas = m_pmesh.areas;
        params.polyFlags = m_pmesh.flags;
        params.polyCount = m_pmesh.npolys;
        params.nvp = m_pmesh.nvp;

        if (m_dmesh != null) {
            params.detailMeshes = m_dmesh.meshes;
            params.detailVerts = m_dmesh.verts;
            params.detailVertsCount = m_dmesh.nverts;
            params.detailTris = m_dmesh.tris;
            params.detailTriCount = m_dmesh.ntris;
        }

        params.walkableHeight = TerrainFactory.agentHeight;
        params.walkableRadius = TerrainFactory.agentRadius;
        params.walkableClimb = TerrainFactory.agentMaxClimb;
        params.bmin = m_pmesh.bmin;
        params.bmax = m_pmesh.bmax;
        params.cs = TerrainFactory.cellSize;
        params.ch = TerrainFactory.cellHeight;
        params.buildBvTree = true;

        params.offMeshConCount = m_geom.getOffMeshConnections().size();
        params.offMeshConVerts = new float[params.offMeshConCount * 6];
        params.offMeshConRad = new float[params.offMeshConCount];
        params.offMeshConDir = new int[params.offMeshConCount];
        params.offMeshConAreas = new int[params.offMeshConCount];
        params.offMeshConFlags = new int[params.offMeshConCount];
        params.offMeshConUserID = new int[params.offMeshConCount];

        for (int i = 0; i < params.offMeshConCount; i++) {
            OffMeshLink offMeshConn = m_geom.getOffMeshConnections().get(i);
            System.arraycopy(offMeshConn.verts, 0, params.offMeshConVerts, 6 * i, 6);
            params.offMeshConRad[i] = offMeshConn.radius;
            params.offMeshConDir[i] = offMeshConn.biDirectional ? NavMesh.DT_OFFMESH_CON_BIDIR : 0;
            params.offMeshConAreas[i] = offMeshConn.area;
            params.offMeshConFlags[i] = offMeshConn.flags;
            params.offMeshConUserID[i] = offMeshConn.userID;
        }

        return params;
    }

    private void setNavMeshModifiers(JmeInputGeomProvider m_geom, Node root) {

        root.depthFirstTraversal(new SceneGraphVisitorAdapter() {
            @Override
            public void visit(Geometry geo) {

                String[] name = geo.getMaterial().getName().split("_");
                NavMeshModifier mod = switch (name[0]) {
                    case "water" -> new NavMeshModifier(geo, AREAMOD_WATER);
                    case "road" -> new NavMeshModifier(geo, AREAMOD_ROAD);
                    case "grass" -> new NavMeshModifier(geo, AREAMOD_GRASS);
                    case "door" -> new NavMeshModifier(geo, AREAMOD_DOOR);
                    default -> new NavMeshModifier(geo, AREAMOD_GROUND);
                };

                m_geom.addModification(mod);
                System.out.println("setNavMeshArea " + mod);
            }
        });
    }

    private void updateAreaAndFlags(NavMeshDataCreateParams params) {
        final int DT_TILECACHE_WALKABLE_AREA = 63;

        for (int i = 0; i < params.polyCount; ++i) {

            if (params.polyAreas[i] == DT_TILECACHE_WALKABLE_AREA) {
                params.polyAreas[i] = POLYAREA_TYPE_GROUND;
            }

            if (params.polyAreas[i] == POLYAREA_TYPE_GROUND
                    || params.polyAreas[i] == POLYAREA_TYPE_GRASS
                    || params.polyAreas[i] == POLYAREA_TYPE_ROAD) {
                params.polyFlags[i] = POLYFLAGS_WALK;

            } else if (params.polyAreas[i] == POLYAREA_TYPE_WATER) {
                params.polyFlags[i] = POLYFLAGS_SWIM;

            } else if (params.polyAreas[i] == POLYAREA_TYPE_DOOR) {
                params.polyFlags[i] = POLYFLAGS_WALK | POLYFLAGS_DOOR;
            }
        }
    }
}
