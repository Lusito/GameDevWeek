package de.hochschuletrier.gdw.ss14.ecs.systems;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.hochschuletrier.gdw.ss14.ecs.EntityManager;
import de.hochschuletrier.gdw.ss14.ecs.components.CatPropertyComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.InputComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.JumpDataComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.LaserPointerComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.MovementComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.PhysicsComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.PlayerComponent;
import de.hochschuletrier.gdw.ss14.states.CatStateEnum;

/**
 * Created by Daniel Dreher on 03.10.2014.
 */
public class CatMovementSystem extends ECSystem
{
    public CatMovementSystem(EntityManager entityManager)
    {
        super(entityManager, 1);
    }

    @Override
    public void render()
    {

    }

    @Override
    public void update(float delta)
    {
        Array<Integer> entities = entityManager.getAllEntitiesWithComponents(PlayerComponent.class, MovementComponent.class, PhysicsComponent.class, InputComponent.class, CatPropertyComponent.class, JumpDataComponent.class);
        Array<Integer> laser = entityManager.getAllEntitiesWithComponents(LaserPointerComponent.class);

        LaserPointerComponent laserPointerComponent = null;

        if (laser.size > 0)
        {
            laserPointerComponent = entityManager.getComponent(laser.first(), LaserPointerComponent.class);
        }

        for (Integer entity : entities)
        {
            MovementComponent movementComponent = entityManager.getComponent(entity, MovementComponent.class);
            PhysicsComponent physicsComponent = entityManager.getComponent(entity, PhysicsComponent.class);
            InputComponent inputComponent = entityManager.getComponent(entity, InputComponent.class);
            CatPropertyComponent catPropertyComponent = entityManager.getComponent(entity, CatPropertyComponent.class);
            JumpDataComponent jumpDataComponent = entityManager.getComponent(entity, JumpDataComponent.class);


            if (catPropertyComponent.state == CatStateEnum.IDLE || catPropertyComponent.state == CatStateEnum.WALK || catPropertyComponent.state == CatStateEnum.RUN)
            {
                Vector2 tmp = new Vector2(inputComponent.whereToGo.x - physicsComponent.getPosition().x, inputComponent.whereToGo.y - physicsComponent.getPosition().y);
                float distance = tmp.len();

                //falls Maus nicht zu nah an Katze (glitscht nicht mehr
                   if(!(distance <= 5)){
                       movementComponent.directionVec.x = tmp.x;
                       movementComponent.directionVec.y = tmp.y;
                    }



                 if (distance >= 200)
                {
                    movementComponent.velocity += movementComponent.acceleration * delta;

                    if (movementComponent.velocity >= movementComponent.maxVelocity)
                    {
                        movementComponent.velocity = movementComponent.maxVelocity;
                    }
                }
                else if (distance >= 100)
                {
                    /**
                     * Falls wir von unserem Stand aus losgehen soll unsere Katze beschleunigen, bis sie "geht"
                     */
                    if (movementComponent.velocity >= movementComponent.middleVelocity)
                    {
                        movementComponent.velocity += movementComponent.damping * delta;
                        if (movementComponent.velocity <= movementComponent.middleVelocity)
                        {
                            movementComponent.velocity = movementComponent.middleVelocity;
                        }
                    }
                    /**
                     * Falls unsere Katze aus dem "Rennen" aus zu nah an unseren Laserpointer kommt, soll
                     * sie stetig langsamer werden
                     */
                    else if (movementComponent.velocity < movementComponent.middleVelocity)
                    {
                        movementComponent.velocity += movementComponent.acceleration * delta;
                        if (movementComponent.velocity >= movementComponent.middleVelocity)
                        {
                            movementComponent.velocity = movementComponent.middleVelocity;
                        }
                    }
                }
                else
                {
                    movementComponent.velocity += movementComponent.damping * 1.5f * delta;
                    if (movementComponent.velocity <= movementComponent.minVelocity)
                    {
                        movementComponent.velocity = 0;
                    }
                }

                if (distance <= 70 && distance >= 30)
                {
                    if (catPropertyComponent.state == CatStateEnum.IDLE)
                    {
                        catPropertyComponent.timeTillJumpTimer = catPropertyComponent.timeTillJumpTimer + delta;
                        if (catPropertyComponent.timeTillJumpTimer >= 0.5)
                        {
                            catPropertyComponent.state = CatStateEnum.JUMP;
                            jumpDataComponent.jumpDirection = movementComponent.directionVec.nor();
                        }
                    }
                }
                else
                {
                    catPropertyComponent.timeTillJumpTimer = 0.0f;
                }

                movementComponent.directionVec = movementComponent.directionVec.nor();

                float angle = (float) Math.atan2(-movementComponent.directionVec.x, movementComponent.directionVec.y);

                if (laserPointerComponent != null)
                {
                    if (!laserPointerComponent.isVisible)
                    {
                        movementComponent.velocity = 0.0f;
                    }

                    if (!catPropertyComponent.isHidden && laserPointerComponent.isVisible)
                    {
                        physicsComponent.setRotation(angle);
                    }
                }

                physicsComponent.setVelocityX(movementComponent.directionVec.x * movementComponent.velocity);
                physicsComponent.setVelocityY(movementComponent.directionVec.y * movementComponent.velocity);

            } // end if (state check)
            else
            {
                if (catPropertyComponent.state == CatStateEnum.JUMP)
                {
                    movementComponent.velocity = jumpDataComponent.jumpVelocity;
                    physicsComponent.setVelocityX(jumpDataComponent.jumpDirection.x * movementComponent.velocity);
                    physicsComponent.setVelocityY(jumpDataComponent.jumpDirection.y * movementComponent.velocity);
                }
            }

        }

    }
}
