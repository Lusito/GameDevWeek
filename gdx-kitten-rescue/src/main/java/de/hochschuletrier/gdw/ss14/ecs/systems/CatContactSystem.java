package de.hochschuletrier.gdw.ss14.ecs.systems;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.*;

import de.hochschuletrier.gdw.ss14.ecs.components.*;
import de.hochschuletrier.gdw.ss14.states.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hochschuletrier.gdw.commons.gdx.physix.PhysixBody;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixContact;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixEntity;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixManager;
import de.hochschuletrier.gdw.ss14.ecs.EntityManager;
import de.hochschuletrier.gdw.ss14.physics.ICollisionListener;
import de.hochschuletrier.gdw.ss14.physics.RayCastPhysics;
import de.hochschuletrier.gdw.ss14.ecs.components.CatPhysicsComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.ConePhysicsComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.JumpablePhysicsComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.JumpablePropertyComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.PhysicsComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.WoolPhysicsComponent;

public class CatContactSystem extends ECSystem implements ICollisionListener{

    private static final Logger logger = LoggerFactory.getLogger(CatContactSystem.class);

    private PhysixManager phyManager;
    private RayCastPhysics rcp;

    public CatContactSystem(EntityManager entityManager, PhysixManager physicsManager) {
        super(entityManager);
        phyManager = physicsManager;
    }

    @Override
    public void fireBeginnCollision(PhysixContact contact) {
        PhysixBody owner = contact.getMyPhysixBody();//.getOwner();

        Object o = contact.getOtherPhysixBody().getFixtureList().get(0).getUserData();
        PhysixEntity other = contact.getOtherPhysixBody().getOwner();

        if(other instanceof CatPhysicsComponent){
            logger.debug("cat collides with dog ... or another cat");


        }else if(other instanceof ConePhysicsComponent){
            logger.debug("cat collides with sight-cone");
            phyManager.getWorld().rayCast(rcp, other.getPosition(), owner.getPosition());
            if(rcp.m_hit && rcp.m_fraction <= ((ConePhysicsComponent)other).mRadius){
                for(Fixture f : other.physicsBody.getFixtureList()){
                    if(rcp.m_fixture == f){
                        logger.debug("Katze sichtbar für Hund");
                    }
                }
                
            }else{
                //dog sees cat not
            }
            rcp.reset();
        }else if(other instanceof WoolPhysicsComponent){
            
        }else if(other instanceof JumpablePhysicsComponent){
            Array<Integer> compos = entityManager.getAllEntitiesWithComponents(JumpablePropertyComponent.class);
            for (Integer p : compos) {
                JumpablePropertyComponent property = entityManager.getComponent(p, JumpablePropertyComponent.class);
                PhysicsComponent puddlecompo = entityManager.getComponent(p, PhysicsComponent.class);
                if(puddlecompo == other && property.type == JumpableState.deadzone){
                    boolean isCatInZone = false;
                    if(contact.getMyFixture().getUserData() == null) return;
                    if(contact.getMyFixture().getUserData().equals("masscenter")){
                        isCatInZone = true;
                    }
                    if(isCatInZone){
                        // cat fall down
                        Array<Integer> entities = entityManager.getAllEntitiesWithComponents(PlayerComponent.class, PhysicsComponent.class);

                        if(entities.size > 0)
                        {
                            int player = entities.first();
                            CatPropertyComponent catPropertyComponent = entityManager.getComponent(player, CatPropertyComponent.class);

                            //catPropertyComponent.isAlive = false;
                            catPropertyComponent.setState(CatStateEnum.FALL);
                        }

                    }
                }
                
            }
            
        }else if(other == null){
            if(!(o instanceof String)) return;
        }
        else if(other instanceof CatBoxPhysicsComponent)
        {
            Array<Integer> entities = entityManager.getAllEntitiesWithComponents(PlayerComponent.class, CatPropertyComponent.class, RenderComponent.class);

            if(entities.size > 0)
            {
                int player = entities.first();

                RenderComponent renderComponent = entityManager.getComponent(player, RenderComponent.class);
                CatPropertyComponent catPropertyComponent = entityManager.getComponent(player, CatPropertyComponent.class);

                entityManager.removeComponent(player, renderComponent);
                
                //catPropertyComponent.setState(CatStateEnum.HIDDEN);

                catPropertyComponent.isHidden = true;
            }

            Array<Integer> lasers = entityManager.getAllEntitiesWithComponents(LaserPointerComponent.class);

            for (Integer entity : lasers)
            {
                LaserPointerComponent laserPointerComponent = entityManager.getComponent(entity, LaserPointerComponent.class);

                laserPointerComponent.isVisible = false;
            }

        }

    }

    @Override
    public void update(float delta) {}

    @Override
    public void render() {}

    @Override
    public void fireEndCollision(PhysixContact contact) {
        // TODO Auto-generated method stub
        
    }
}
