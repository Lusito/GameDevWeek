package de.hochschuletrier.gdw.ss14.ecs.components;

import de.hochschuletrier.gdw.ss14.states.*;

public class CatPropertyComponent implements Component
{
    public int amountLives;
    public boolean canSeeLaserPointer;
    public boolean isAlive;
    public CatStateEnum state;
    public float jumpBuffer = 0;

    public CatPropertyComponent()
    {
        canSeeLaserPointer = true;
        amountLives = 9;
        isAlive = true;
        state = CatStateEnum.IDLE;
    }
}
