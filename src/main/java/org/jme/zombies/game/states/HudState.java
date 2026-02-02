package org.jme.zombies.game.states;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.scene.Node;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.component.IconComponent;
import org.jme.zombies.GameApplication;

public class HudState extends BaseAppState {

    private Node guiNode;
    private Container hud;

    @Override
    protected void initialize(Application app) {
        var application = (GameApplication) app;
        GuiGlobals.initialize(application);
        GuiGlobals.getInstance().setCursorEventsEnabled(false);

        this.guiNode = application.getGuiNode();
        this.hud = new Container();
    }

    @Override
    protected void cleanup(Application app) {
        hud.detachAllChildren();

    }

    @Override
    protected void onEnable() {
        guiNode.attachChild(hud);

        hud.setLocalTranslation(200, 100, 0);
        var score = new Label("Score");

        hud.addChild(score);
    }

    @Override
    protected void onDisable() {
        guiNode.detachAllChildren();
    }
}
