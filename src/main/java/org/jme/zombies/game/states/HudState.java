package org.jme.zombies.game.states;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.scene.Node;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.component.SpringGridLayout;
import org.jme.zombies.GameApplication;

public class HudState extends BaseAppState {

    private Node guiNode;
    private Container hudRoot;

    @Override
    protected void initialize(Application app) {
        var application = (GameApplication) app;
        GuiGlobals.initialize(application);
        GuiGlobals.getInstance().setCursorEventsEnabled(false);

        this.guiNode = application.getGuiNode();
        this.hudRoot = new Container();
    }

    @Override
    protected void cleanup(Application app) {
        hudRoot.detachAllChildren();

    }

    @Override
    protected void onEnable() {
        guiNode.attachChild(hudRoot);
    }

    @Override
    protected void onDisable() {
        guiNode.detachAllChildren();
    }
}
