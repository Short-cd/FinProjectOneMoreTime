package com.csafinal.basedefense.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.ui.ProgressBar;
import com.csafinal.basedefense.DropApp;
import com.csafinal.basedefense.data.Config;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BulletComponent extends Component {

    private Entity tower;
    private Entity target;

    public BulletComponent(Entity tower, Entity target) {
        this.tower = tower;
        this.target = target;
    }

    @Override
    public void onUpdate(double tpf) {
        if (!target.isActive()) {
            entity.removeFromWorld();
            return;
        }

        if (entity.distanceBBox(target) < Config.BULLET_SPEED * tpf) {
            onTargetHit();
            return;
        }

        entity.translateTowards(target.getCenter(), Config.BULLET_SPEED * tpf);
    }

    private void onTargetHit() {
        TowerComponent data = tower.getComponent(TowerComponent.class);

        entity.removeFromWorld();

        var hp = target.getComponent(HealthIntComponent.class);

        hp.damage(data.getDamage());

//        target.getProperties((ProgressBar.class))

        if (hp.isZero()) {
            FXGL.<DropApp>getAppCast().onEnemyKilled(target);
            target.removeFromWorld();
        }
    }
}
