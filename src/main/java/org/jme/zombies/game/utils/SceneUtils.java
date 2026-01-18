package org.jme.zombies.game.utils;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

public class SceneUtils {


    public static <T extends Control> T getComponent(Spatial sp, Class<T> type) {
        return sp.getControl(type);
    }

    public static <T extends Control> T getComponentInChildren(Spatial sp, final Class<T> type) {
        T control = sp.getControl(type);
        if (control != null) {
            return control;
        }

        if (sp instanceof Node) {
            for (Spatial child : ((Node) sp).getChildren()) {
                control = getComponentInChildren(child, type);
                if (control != null) {
                    return control;
                }
            }
        }

        return null;
    }
}
