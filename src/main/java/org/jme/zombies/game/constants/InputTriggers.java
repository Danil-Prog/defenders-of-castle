package org.jme.zombies.game.constants;

import java.util.Arrays;

public enum InputTriggers {

    LEFT("Left"),
    RIGHT("Right"),
    UP("Up"),
    DOWN("Down"),

    SPACE("Space"),

    MOUSE_LEFT("MouseLeft");

    private final String name;

    InputTriggers(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static InputTriggers fromName(String name) {
        return Arrays.stream(InputTriggers.values())
                .filter(inputTriggers -> inputTriggers.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Failed to get keyboard by name: " + name));
    }
}
