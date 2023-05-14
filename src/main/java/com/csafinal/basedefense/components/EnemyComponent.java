package com.csafinal.basedefense.components;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.csafinal.basedefense.DropApp;
import com.csafinal.basedefense.data.LivingThingData;
import javafx.geometry.Point2D;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameWorld;

public class EnemyComponent extends Component {

    Entity target;
    LivingThingData data;
    public EnemyComponent(LivingThingData data){
        this.data = data;
    }
    @Override
    public void onUpdate(double tpf){
        getGameWorld().getClosestEntity(entity, e -> e.isType(DropApp.Type.BUILDING)).ifPresent(nearestEnemy -> {
            target = nearestEnemy;
        });
        if (target != null) {
            Point2D targetLoc = target.getPosition();
            Vec2 targetDirection = new Vec2(targetLoc);
            targetDirection.mulLocal(data.speed() / targetDirection.distance(targetLoc));
        }
    }

    public double getDamage() {
        return data.damage();
    }

    public void hitTarget(Entity target){
        var hp = target.getComponent(HealthIntComponent.class);
        hp.damage(data.damage());
        if(hp.isZero()){
            target.removeFromWorld();
        }
    }
}
