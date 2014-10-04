package de.hochschuletrier.gdw.ss14.ecs.components;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import de.hochschuletrier.gdw.commons.gdx.physix.PhysixBodyDef;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixFixtureDef;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixManager;
import de.hochschuletrier.gdw.ss14.ecs.components.PhysicsComponent;

public class DogPhysicsComponent extends PhysicsComponent {
    public Vector2 mPosition;
    public float mWidth;
    public float mHeight;
    public float mFriction;
    public float mRotation;
    public float mRestitution;

    /**
     * 
     * @param position → central position of the object
     * @param width    → the width of the object ( width >= heigth)
     * @param height   → the height of the object (height < width)
     * @param rotation
     *            the rotation in radians [0 .. 2*PI]
     * @param friction
     *            the friction of the object
     * @param restitution
     *            the restitution (elastitcy)
     */
    public DogPhysicsComponent(Vector2 position, float width, float height,
            float rotation, float friciton, float restitutioin) {
        
        if(height <= width) throw new IllegalArgumentException("cat needs to be higher than fat"); //cat/dog?
        
        mPosition = position;
        mWidth = width;
        mHeight = height;
        mRotation = rotation;
        mFriction = friciton;
        mRestitution = restitutioin;

    }

    public DogPhysicsComponent() {
        this(new Vector2(0, 0), 50f, 100f, 0f, 1f, 0f);
    }

    @Override
    public void initPhysics(PhysixManager manager) {

        PhysixFixtureDef fixturedef = new PhysixFixtureDef(manager).density(1)
                .friction(mFriction).restitution(mRestitution);

        physicsBody = new PhysixBodyDef(BodyType.DynamicBody, manager)
                .position(mPosition).fixedRotation(true).angle(mRotation)
                .create();

        physicsBody.createFixture(fixturedef.shapeBox(mWidth, mHeight-mWidth));
        physicsBody.createFixture(fixturedef.shapeCircle(mWidth/2, new Vector2(mPosition.x, mPosition.y + ( mHeight - mWidth)/2)));
        physicsBody.createFixture(fixturedef.shapeCircle(mWidth/2, new Vector2(mPosition.x, mPosition.y + (-mHeight + mWidth)/2)));
        setPhysicsBody(physicsBody);
    }
}
