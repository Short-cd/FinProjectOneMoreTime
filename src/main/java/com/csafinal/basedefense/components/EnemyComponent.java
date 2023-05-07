package com.csafinal.basedefense.components;

import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.csafinal.basedefense.data.EnemyData;

public class EnemyComponent extends Component {
    EnemyData data;
    public EnemyComponent(EnemyData data){
        this.data = data;
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
