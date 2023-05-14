package com.csafinal.basedefense;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.AutoRotationComponent;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.csafinal.basedefense.components.*;
import com.csafinal.basedefense.data.LivingThingData;
import com.csafinal.basedefense.data.TowerData;
import javafx.beans.binding.Bindings;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.csafinal.basedefense.DropApp.Type.BULLET;
import static com.csafinal.basedefense.DropApp.Type.TOWER_BASE;


public class gameFactory implements EntityFactory{

    @Spawns("player")
    public Entity spawnPlayer(SpawnData data){
        LivingThingData playerData = data.get("playerData");
        return entityBuilder(data)
                .type(DropApp.Type.PLAYER)
                .at(100, 100)
                .scale(0.2,0.2)
                .with(new HealthIntComponent(playerData.hp()))
                .with(new MovementComponent(playerData.speed(), "player"))
                .with(new PlayerComponent(playerData))
                .viewWithBBox("player.png")
                .zIndex(5)
                .collidable()
                .build();
    }

    @Spawns("enemy")
    public Entity spawnEnemy(SpawnData data){
        LivingThingData eData = data.get("eData");
        HealthIntComponent hp = new HealthIntComponent(2);
        int xval = FXGLMath.random(0, getAppWidth() - 64);
        int yval = FXGLMath.random(0, getAppHeight() - 64);;
        int targetx = 200;
        int targety = 200;
        System.out.println(xval);
        return entityBuilder(data)
                .type(DropApp.Type.ENEMY)
                .at(xval, yval)
                .with(new ProjectileComponent(new Point2D((int) (targetx - xval), (int) (targety - yval)), 150))
                .with(new HealthIntComponent(eData.hp()))
                .with(new EnemyComponent(eData))
                .viewWithBBox("enemies/enemy1.png")
                .collidable()
                .build();
    }

    @Spawns("Bullet")
    public Entity spawnBullet(SpawnData data) {
        String imageName = data.get("imageName");

        Node view = texture(imageName);
        view.setRotate(90);

        Entity tower = data.get("tower");
        Entity target = data.get("target");

        return entityBuilder(data)
                .type(BULLET)
                .viewWithBBox(view)
                .collidable()
                .with(new BulletComponent(tower, target))
                .with(new AutoRotationComponent())
                .zIndex(4)
                .build();
    }

    @Spawns("towerBase")
    public Entity newTowerBase(SpawnData data) {
        var rect = new Rectangle(64, 64, Color.GREEN);
        rect.setOpacity(0.25);

        var cell = entityBuilder(data)
                .type(TOWER_BASE)
                .viewWithBBox(rect)
                .onClick(e -> {
                    FXGL.<DropApp>getAppCast().onCellClicked(e);
                })
                .build();

        rect.fillProperty().bind(
                Bindings.when(cell.getViewComponent().getParent().hoverProperty())
                        .then(Color.DARKGREEN)
                        .otherwise(Color.GREEN)
        );

        return cell;
    }

    @Spawns("building")
    public Entity spawnBuilding(SpawnData data){
        TowerData towerData = data.get("towerData");

        return entityBuilder(data)
                .type(DropApp.Type.BUILDING)
                .viewWithBBox(towerData.imageName())
                .with(new HealthIntComponent(10))
                .with(new TowerComponent(towerData))
                .collidable()
                .build();
    }

    @Spawns("tree")
    public Entity newTree(SpawnData data) {
        var rect = new Rectangle(64, 64, Color.BLUE);
        rect.setOpacity(0.25);

        var cell = entityBuilder(data)
                .type(DropApp.Type.TREE)
                .viewWithBBox(rect)
                .onClick(e -> {
                    FXGL.<DropApp>getAppCast().onResourceClicked(e);
                })
                .build();

        rect.fillProperty().bind(
                Bindings.when(cell.getViewComponent().getParent().hoverProperty())
                        .then(Color.DARKBLUE)
                        .otherwise(Color.rgb(46, 204, 113))
        );

        return cell;
    }

    @Spawns("stone")
    public Entity newStone(SpawnData data) {
        var rect = new Rectangle(64, 64, Color.GREEN);
        rect.setOpacity(0.25);

        var cell = entityBuilder(data)
                .type(DropApp.Type.STONE)
                .viewWithBBox(rect)
                .onClick(e -> {
                    FXGL.<DropApp>getAppCast().onResourceClicked(e);
                })
                .build();

        rect.fillProperty().bind(
                Bindings.when(cell.getViewComponent().getParent().hoverProperty())
                        .then(Color.DARKGREEN)
                        .otherwise(Color.rgb(46, 204, 113))
        );

        return cell;
    }

}
