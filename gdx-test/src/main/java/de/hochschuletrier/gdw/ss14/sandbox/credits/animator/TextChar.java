package de.hochschuletrier.gdw.ss14.sandbox.credits.animator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import de.hochschuletrier.gdw.commons.gdx.utils.DrawUtil;

/**
 *
 * @author Santo Pfingsten
 */
public class TextChar {
    private static final Vector2 temp1 = new Vector2();
    private static final Vector2 temp2 = new Vector2();

    private final Vector2 start = new Vector2(1, 0);
    private final Vector2 control = new Vector2(1, 0);
    private final Vector2 end = new Vector2();
    private final String text;
    private float animationTime;
    private float startTime;

    public TextChar(BitmapFont font, String text, int index, AnimatorData.Path.Animation animation, float totalAnimationTime) {
        startTime = index * totalAnimationTime * 0.5f;
        this.text = text.substring(index, index+1);
        if (index > 0) {
            BitmapFont.TextBounds bounds = font.getBounds(text, 0, index);
            end.x = bounds.width;
        }

        int angle = getRandomAngle(animation.minAngle, animation.maxAngle);
        int radius = getRandomRadius(animation.minRadius, animation.maxRadius);
        start.rotate(angle).scl(radius);
        start.x += end.x;

        if (MathUtils.randomBoolean()) {
            angle += MathUtils.random(45, 135);
        } else {
            angle -= MathUtils.random(45, 135);
        }
        if (angle < 0) {
            angle += 360;
        } else if (angle > 360) {
            angle -= 360;
        }
        control.rotate(angle).scl(radius);
        control.x += end.x;
    }

    private int getRandomRadius(int minRadius, int maxRadius) {
        int radius;
        if (minRadius == maxRadius) {
            radius = minRadius;
        } else {
            radius = MathUtils.random(minRadius, maxRadius);
        }
        return radius;
    }

    private int getRandomAngle(int minAngle, int maxAngle) {
        int angle;
        if (minAngle == maxAngle) {
            angle = minAngle;
        } else if (minAngle < maxAngle) {
            angle = MathUtils.random(minAngle, maxAngle);
        } else {
            minAngle -= 360;
            angle = MathUtils.random(minAngle, maxAngle);
            if (angle < 0) {
                angle += 360;
            }
        }
        return angle;
    }

    public boolean update(float delta, float totalAnimationTime) {
        if (startTime > 0) {
            startTime -= delta;
            if (startTime < 0) {
                animationTime += -startTime;
                startTime = 0;
            }
        } else {
            animationTime += delta;
        }
        return animationTime >= totalAnimationTime;
    }

    public void render(BitmapFont font, Color color, Vector2 offset, float totalAnimationTime) {
        if (startTime == 0) {
            float t = animationTime / totalAnimationTime;
            color.a = Math.min(1.0f, t*2);
//            t *= t;
            Bezier.quadratic(temp2, t, start, control, end, temp1);
            temp2.add(offset);
            font.setColor(color);
            font.draw(DrawUtil.batch, text, temp2.x, temp2.y);
        }
    }
}
