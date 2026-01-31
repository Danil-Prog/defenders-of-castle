package org.jme.zombies.game.component;

import com.simsilica.es.EntityComponent;

public class DetachComponent implements EntityComponent {
    public long expireIn;

    public DetachComponent() {
    }

    public DetachComponent(long expireIn) {
        this.expireIn = expireIn;
    }
}
