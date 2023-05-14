/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.csafinal.basedefense;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.UserAction;
import com.csafinal.basedefense.components.EnemyComponent;
import com.csafinal.basedefense.components.MovementComponent;
import com.csafinal.basedefense.components.PlayerComponent;
import com.csafinal.basedefense.data.LivingThingData;
import com.csafinal.basedefense.data.TowerData;
import com.csafinal.basedefense.ui.TowerSelectionBox;
import javafx.animation.Interpolator;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import java.util.List;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.csafinal.basedefense.data.Vars.*;
// NOTE: this import above is crucial, it pulls in many useful methods

public class DropApp extends GameApplication {


    public enum Type {
        BUILDING, ENEMY, PLAYER, TREE, STONE, BULLET

    }
    List<String> levelNames = List.of(
            "level1.json"
    );
    boolean downPress, upPress, leftPress, rightPress;
//    private SelectBuildingBox buildingSelectionBox;
    private Entity player;

    private boolean playerAlive;

    private static int stone = 0;
    private static int wood = 0;

    public int money = 1000;

    private int numTowers;

    private static List<TowerData> towerData;

//    private LivingThingData enemy1Data = getAssetLoader().loadJSON("enemies/enemy1.json", LivingThingData.class).get();
//    private LivingThingData enemy2Data = getAssetLoader().loadJSON("enemies/enemy2.json", LivingThingData.class).get();

    @Override
    protected void initSettings(GameSettings settings) {
        // initialize common game / window settings.
        settings.setTitle("Zombs");
        settings.setVersion("1.0");
        settings.setWidth(1600);
        settings.setHeight(896);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars){
        vars.put(ENEMY_COUNT, 0);
        vars.put(PLAYER_COUNT, 0);
        vars.put(BUILDING_COUNT, 0);
        vars.put(MONEY, 0);
        vars.put(STONE, 0);
        vars.put(WOOD, 0);
        vars.put(SCORE, 0);
        vars.put(DAY_NUMBER, 0);
    }
    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new gameFactory());
        loadCurrentLevel();
        loadTowerData();

        LivingThingData playerData = getAssetLoader().loadJSON("playerTypes/player1.json", LivingThingData.class).get();
//        spawn("player");
        player = spawn("player", new SpawnData().put("playerData", playerData));
        playerAlive = true;

        LivingThingData enemyData = getAssetLoader().loadJSON("enemies/enemy1.json", LivingThingData.class).get();
        run(() -> spawnWithScale("enemy",
                new SpawnData()
                        .put("eData", enemyData),
                Duration.seconds(3),
                Interpolator.LINEAR), Duration.seconds(3));

        run(()-> spawn("building"), Duration.seconds(5));
        towerSelectionBox = new TowerSelectionBox(towerData);
    }
    @Override
    protected void initInput(){

        getInput().addAction(new UserAction("Left") {
            @Override
            protected void onAction() {
                if (playerAlive) {
                    player.getComponent(MovementComponent.class).changeMovement(new Vec2(player.getComponent(PlayerComponent.class).getData().speed() * -1, 0));
                    player.getComponent(MovementComponent.class).translate();
                    player.getComponent(MovementComponent.class).changeMovement(new Vec2(player.getComponent(PlayerComponent.class).getData().speed() * 1, 0));
                    leftPress = true;
                }
            }

            @Override
            protected void onActionEnd() {
                leftPress = false;
            }
        }, KeyCode.A);

        getInput().addAction(new UserAction("Right") {
            @Override
            protected void onAction() {
                if (playerAlive) {
                    player.getComponent(MovementComponent.class).changeMovement(new Vec2(player.getComponent(PlayerComponent.class).getData().speed() * 1, 0));
                    player.getComponent(MovementComponent.class).translate();
                    player.getComponent(MovementComponent.class).changeMovement(new Vec2(player.getComponent(PlayerComponent.class).getData().speed() * -1, 0));
                    rightPress = true;
                }
            }

            @Override
            protected void onActionEnd() {
                rightPress = false;
            }
        }, KeyCode.D);

        getInput().addAction(new UserAction("Down") {
            @Override
            protected void onAction() {
                if (playerAlive) {
                    player.getComponent(MovementComponent.class).changeMovement(new Vec2(0, player.getComponent(PlayerComponent.class).getData().speed() * 1));
                    player.getComponent(MovementComponent.class).translate();
                    player.getComponent(MovementComponent.class).changeMovement(new Vec2(0, player.getComponent(PlayerComponent.class).getData().speed() * -1));
                    downPress = true;
                }
            }

            @Override
            protected void onActionEnd() {
                downPress = false;
            }
        }, KeyCode.S);

        getInput().addAction(new UserAction("Up") {
            @Override
            protected void onAction() {
                if (playerAlive) {
                    player.getComponent(MovementComponent.class).changeMovement(new Vec2(0, player.getComponent(PlayerComponent.class).getData().speed() * -1));
                    player.getComponent(MovementComponent.class).translate();
                    player.getComponent(MovementComponent.class).changeMovement(new Vec2(0, player.getComponent(PlayerComponent.class).getData().speed() * 1));
                    upPress = true;
                }
            }

            @Override
            protected void onActionEnd() {
                upPress = false;
            }
        }, KeyCode.W);

        getInput().addAction(new UserAction("info") {
            @Override
            protected void onAction() {
                System.out.println("Stone count " + geti(STONE) + "\n Wood count: " + geti(WOOD));
            }
        }, KeyCode.I);

        getInput().addAction(new UserAction("Revive") {
            @Override
            protected void onAction() {
                System.out.println("R");
                revivePlayer();
            }
        }, KeyCode.R);

    }

    @Override
    protected void initPhysics() {
        int playerCount = 1;
        onCollisionBegin(Type.BUILDING, Type.ENEMY, (building, enemy) -> {
            var hp = building.getComponent(HealthIntComponent.class);
            if (hp.getValue() > 1){
                hp.damage(1);
                return;
            }
            building.removeFromWorld();
        });
        onCollisionBegin(Type.PLAYER, Type.ENEMY, (player, enemy) -> {
            var hp = player.getComponent(HealthIntComponent.class);
            if (hp.getValue() > 1){
                hp.damage(1);
                return;
            }
            player.removeFromWorld();
            playerAlive = false;
            if(playerCount<1){
                endGame(false);
            }
        });
        onCollision(Type.PLAYER, Type.BUILDING, (player, nonPlayer) -> {
            player.setPosition(checkCollisionLocation(player, nonPlayer));
        });
    }


    private Point2D checkCollisionLocation(Entity thing1, Entity thing2){
        double xLoc = thing1.getX(), yLoc = thing1.getY();
        if(leftPress&&!rightPress&&
                (thing1.getY()<thing2.getBottomY()-10&&thing1.getBottomY()>thing2.getY()+10)){
            xLoc = thing2.getRightX();
        }
        if(rightPress&&!leftPress&&
                (thing1.getY()<thing2.getBottomY()-10&&thing1.getBottomY()>thing2.getY()+10)){
            xLoc = thing2.getX()-thing1.getWidth();
        }
        if(downPress&&!upPress&&
                (thing1.getX()<thing2.getRightX()-10&&thing1.getRightX()>thing2.getX()+10)){
            yLoc = thing2.getY()-thing1.getHeight();
        }
        if(upPress&&!downPress&&
                (thing1.getX()<thing2.getRightX()-10&&thing1.getRightX()>thing2.getX()+10)){
            yLoc = thing2.getBottomY();
        }
        Point2D newPoint = new Point2D(xLoc, yLoc);
//        System.out.println("new point:" + newPoint + "\n playerLocation:" + thing1.getPosition() + " player Dimensions: "
//        + thing1.getWidth()+ " height:" + thing1.getHeight()+ "\n objectLocation:" + thing2.getPosition() +"object dimensions:"
//        + thing2.getWidth() + "height: " + thing2.getHeight());
        System.out.println("Stone count " + geti(STONE)+ "\n Wood count: " + geti(WOOD));
        return newPoint;
    }
    public void onResourceClicked(Entity e) {
        if (e.getProperties().exists("resourceHarvester")) {
            onHarvesterClicked(e);
        } else {
            collectResource(e, 1);
            showHarvester(e);
        }
    }
    public static void showHarvester(Entity e){

    }

    private TowerSelectionBox towerSelectionBox;

    public void onTowerSelected(Entity cell, TowerData data) {
        if (geti(MONEY) - data.cost() >= 0) {
            towerSelectionBox.setVisible(false);

            inc(MONEY, -data.cost());

            var tower = spawnWithScale(
                    "Tower",
                    new SpawnData(cell.getPosition()).put("towerData", data),
                    Duration.seconds(0.85),
                    Interpolators.ELASTIC.EASE_OUT()
            );

            cell.setProperty("tower", tower);

            numTowers++;
        }
    }

    public static void collectResource(Entity e, double increment){
        switch ((Type)e.getType()) {
            case TREE: {
//                inc(WOOD, increment);
                wood++;
            }
            case STONE: {
//                inc(STONE, increment);
                stone++;
            }
        }
    }

    private void onHarvesterClicked(Entity e) {//upgrade harvester

    }
    public void onCellClicked(Entity cell) {
        // if we already have a tower on this tower base, ignore call
        if (cell.getProperties().exists("tower"))
            return;

        towerSelectionBox.setCell(cell);
        towerSelectionBox.setVisible(true);

        var x = cell.getX() > getAppWidth() / 2.0 ? cell.getX() - 250 : cell.getX();

//        towerSelectionBox.setTranslateX(x);
//        towerSelectionBox.setTranslateY(cell.getY());
    }


    @Override
    protected void onUpdate(double tpf) {
    }
    private void loadPlayerData(){

    }

    private void loadTowerData() {
        List<String> towerNames = List.of(
                "tower1.json",
                "tower2.json",
                "tower3.json",
                "tower4.json",
                "tower5.json",
                "tower6.json"
        );

        towerData = towerNames.stream()
                .map(name -> getAssetLoader().loadJSON("towers/" + name, TowerData.class).get())
                .toList();
    }

    public static TowerData getTower() {
        return towerData.get(0);
    }

    private void loadCurrentLevel() {
        setLevelFromMap("tmx/td1.tmx");

        getGameWorld().getEntitiesFiltered(e -> e.isType("TiledMapLayer"))
                .forEach(e -> {
                    e.getViewComponent().addOnClickHandler(event -> {
                        towerSelectionBox.setVisible(true);
                        onCellClicked(spawn("cell"));
                    });
                });
    }
    public void removeEntity(Entity object){
        object.removeFromWorld();
    }

    public void onEnemyKilled(Entity enemy) {
        inc(MONEY, enemy.getComponent(EnemyComponent.class).getData().reward());
    }

    public void revivePlayer(){
        if (!playerAlive) {
            LivingThingData playerData = getAssetLoader().loadJSON("playerTypes/player1.json", LivingThingData.class).get();
//        spawn("player");
            player = spawn("player", new SpawnData().put("playerData", playerData));
            playerAlive = true;
        }
    }

    private void endGame(boolean won){
        if(won){

        }else{

        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
