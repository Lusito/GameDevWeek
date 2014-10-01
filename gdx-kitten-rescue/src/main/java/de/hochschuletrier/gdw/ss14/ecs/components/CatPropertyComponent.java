package de.hochschuletrier.gdw.ss14.ecs.components;

import de.hochschuletrier.gdw.ss14.states.CatStateEnum;

public class CatPropertyComponent implements Component {

    public int amountLives;
    public boolean isAlive;
    public CatStateEnum state;

    public CatPropertyComponent() {
        amountLives = 9;
        isAlive = true;
    }

}
