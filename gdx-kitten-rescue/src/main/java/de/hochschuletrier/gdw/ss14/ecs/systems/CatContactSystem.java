package de.hochschuletrier.gdw.ss14.ecs.systems;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.utils.Array;

import de.hochschuletrier.gdw.commons.gdx.physix.PhysixBody;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixContact;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixEntity;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixManager;
import de.hochschuletrier.gdw.ss14.ecs.EntityManager;
import de.hochschuletrier.gdw.ss14.ecs.components.CatBoxPhysicsComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.CatPhysicsComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.CatPropertyComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.Component;
import de.hochschuletrier.gdw.ss14.ecs.components.EnemyComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.GroundPropertyComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.JumpablePropertyComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.LaserPointerComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.LaserPointerComponent.ToolState;
import de.hochschuletrier.gdw.ss14.ecs.components.PhysicsComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.StairsPhysicsComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.WoolPhysicsComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.WoolPropertyComponent;
import de.hochschuletrier.gdw.ss14.physics.ICollisionListener;
import de.hochschuletrier.gdw.ss14.physics.RayCastPhysics;
import de.hochschuletrier.gdw.ss14.states.CatStateEnum;

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

        /////////////
        // get all neccessary information
        Array<Integer> physicEntities = entityManager.getAllEntitiesWithComponents(PhysicsComponent.class);
        Integer myEntity = null, otherEntity = null;
        PhysicsComponent otherPhysic = null;
        for(Integer i : physicEntities){
            PhysicsComponent tmp = entityManager.getComponent(i, PhysicsComponent.class);
            if(tmp.physicsBody == contact.getMyPhysixBody()){ myEntity = i;}
            if(tmp.physicsBody == contact.getOtherPhysixBody()){ otherEntity = i; otherPhysic = tmp; }
        }
        // checks if the center of the cat is collided, or just a sightcone collision
        boolean isCatInZone = false, mySightCone = false, otherSightCone = false;
        if(contact.getMyFixture().getUserData() != null){
            if(contact.getMyFixture().getUserData().equals("masscenter")){
                isCatInZone = true;
            }else if(contact.getMyFixture().getUserData().equals("sightcone")){
                mySightCone = true;
            }
        }
        if(contact.getOtherFixture().getUserData() != null){
            if(contact.getOtherFixture().getUserData().equals("sightcone")){
                otherSightCone = true;
            }
        }
            
        //////////
        // if something wierd happens, one of these is null then dont go on
        if(myEntity == null || otherEntity == null || otherPhysic == null) return;
        
        Component c = null, d = null;
        /* c → used to check if the other has component xy 
         * d → used to get a specific "my" component to react to the collision
         * */
        if( (c = entityManager.getComponent(otherEntity, EnemyComponent.class)) != null ){
            /*other → is enemy */
            if(otherPhysic instanceof CatPhysicsComponent){
                if(mySightCone) return; // katze sieht hund/ oder sichtfelder berühren sich → egal
                // kollidiert mit hund (oder anderer katze)
                if(otherSightCone){
                    // katzenkörper berührt hunde sichtfeld
                    
                }else{
                    // katzenkörper berührt hundekörper
                    
                }
            }
            
        }else if( (c = entityManager.getComponent(otherEntity, JumpablePropertyComponent.class) ) != null ){
            /*other → is jumpable object */
            switch(((JumpablePropertyComponent)c).type){
            case deadzone:
                if(! isCatInZone) break;
                if ((d = entityManager.getComponent(myEntity, CatPropertyComponent.class)) != null)
                    ((CatPropertyComponent)d).setState(CatStateEnum.FALL);  
            break;
            default:break;
            }
        }else if( (c = entityManager.getComponent(otherEntity, StairsPhysicsComponent.class) ) != null ){
            if(isCatInZone){
                // katze hat treppe betreten
                
            }
        }else if( otherPhysic instanceof WoolPhysicsComponent || (c = entityManager.getComponent(otherEntity, WoolPhysicsComponent.class) ) != null ){
            /* other → is groundobject */
            
            if(mySightCone){
                if ((d = entityManager.getComponent(myEntity, CatPropertyComponent.class)) != null)
                    ((CatPropertyComponent)d).isInfluenced = true;
                if((c = entityManager.getComponent(otherEntity, WoolPhysicsComponent.class)) != null){
                    ((WoolPhysicsComponent)c).isSeen = true;
                }
            }else{
//                if ((d = entityManager.getComponent(myEntity, CatPropertyComponent.class)) != null)
//                    ((CatPropertyComponent)d)  play with wool
            }
        }else if( (c = entityManager.getComponent(otherEntity, GroundPropertyComponent.class) ) != null ){
            /* other → is groundobject */
            if ((d = entityManager.getComponent(myEntity, CatPropertyComponent.class)) != null)
                ((CatPropertyComponent)d).groundWalking = ((GroundPropertyComponent)c).type;
        }else if( otherPhysic instanceof CatBoxPhysicsComponent ){
            if(mySightCone) return; // katze sieht katzenbox → egal
            if ((d = entityManager.getComponent(myEntity, CatPhysicsComponent.class)) != null)
                 entityManager.removeComponent(myEntity, d);
            if ((d = entityManager.getComponent(myEntity, CatPropertyComponent.class)) != null)
                ((CatPropertyComponent)d).isHidden = true;
                
            Array<Integer> lasers = entityManager.getAllEntitiesWithComponents(LaserPointerComponent.class);
            for (Integer entity : lasers){
                LaserPointerComponent laserPointerComponent = entityManager.getComponent(entity, LaserPointerComponent.class);
                laserPointerComponent.toolState = ToolState.WATERPISTOL;
            }
        }
        
        /////////

    }

    @Override
    public void update(float delta) {}

    @Override
    public void render() {}

    @Override
    public void fireEndCollision(PhysixContact contact) {
        PhysixBody owner = contact.getMyPhysixBody();//.getOwner();

        Object o = contact.getOtherPhysixBody().getFixtureList().get(0).getUserData();
        PhysixEntity other = contact.getOtherPhysixBody().getOwner();

        /////////////
        // get all neccessary information
        Array<Integer> physicEntities = entityManager.getAllEntitiesWithComponents(PhysicsComponent.class);
        Integer myEntity = null, otherEntity = null;
        PhysicsComponent otherPhysic = null;
        for(Integer i : physicEntities){
            PhysicsComponent tmp = entityManager.getComponent(i, PhysicsComponent.class);
            if(tmp.physicsBody == contact.getMyPhysixBody()){ myEntity = i;}
            if(tmp.physicsBody == contact.getOtherPhysixBody()){ otherEntity = i; otherPhysic = tmp; }
        }
        // checks if the center of the cat is collided
        boolean isCatInZone = false, mySightCone = false, otherSightCone = false;
        if(contact.getMyFixture().getUserData() != null){
            if(contact.getMyFixture().getUserData().equals("masscenter")){
                isCatInZone = true;
            }else if(contact.getMyFixture().getUserData().equals("sightcone")){
                mySightCone = true;
            }
        }
        if(contact.getOtherFixture().getUserData() != null){
            if(contact.getOtherFixture().getUserData().equals("sightcone")){
                otherSightCone = true;
            }
        }
            
        //////////
        // if something wierd happens, one of these is null then dont go on
        if(myEntity == null || otherEntity == null || otherPhysic == null) return;
        
        Component c = null, d = null;
        if(otherPhysic instanceof WoolPhysicsComponent || (c = entityManager.getComponent(otherEntity, WoolPhysicsComponent.class) ) != null ){
            /* other → is groundobject */
            if ((d = entityManager.getComponent(myEntity, CatPropertyComponent.class)) != null)
                ((CatPropertyComponent)d).isInfluenced = false;
            if((c = entityManager.getComponent(otherEntity, WoolPhysicsComponent.class)) != null){
                ((WoolPhysicsComponent)c).isSeen = false;
            }
        }else if( (c = entityManager.getComponent(otherEntity, EnemyComponent.class)) != null ){
            // cat does not collide with dogPhysx anymore which means ...
            if(otherSightCone && !mySightCone){
                // ... dog does not see the cat anymore
                
            }
            
        }
        

        
    }
}