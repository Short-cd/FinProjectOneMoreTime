/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.csafinal.basedefense;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import com.csafinal.basedefense.components.CollectionComponent;
import com.csafinal.basedefense.components.MovementComponent;
import com.csafinal.basedefense.components.PlayerComponent;
import com.csafinal.basedefense.data.TowerData;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import java.util.List;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.csafinal.basedefense.DropApp.Vars.*;
// NOTE: this import above is crucial, it pulls in many useful methods

public class DropApp extends GameApplication {

    static final class Vars{
        static final String ENEMY_COUNT = "enemyCount";
        static final String PLAYER_COUNT = "playerCount";
        static final String BUILDING_COUNT = "buildingCount";
        static final String MONEY = "money";
        static final String STONE = "stone";
        static final String WOOD = "wood";
        static final String SCORE = "score";
        static final String DAY_NUMBER = "dayNumber";
    }
    public enum Type {
        BUILDING, ENEMY, PLAYER, TREE, STONE, BULLET

    }
    List<String> levelNames = List.of(
            "level1.json"
    );
    boolean downPress, upPress, leftPress, rightPress;

    private Entity player;

    private static List<TowerData> towerData;

    private PlayerComponent playerComponent;
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

        player = spawn("player");

        run(() -> spawn("enemy"), Duration.seconds(3));
        run(()-> spawn("building"), Duration.seconds(5));
//        player.rota
//        loopBGM("bgm.mp3");

//        loadTowerData();

        // construct UI objects
//        towerSelectionBox = new TowerSelectionBox(towerData);
    }
    @Override
    protected void initInput(){
        getInput().addAction(new UserAction("Left"){
            @Override
            protected void onAction(){
                player.getComponent(MovementComponent.class).changeMovement(new Vec2(player.getComponent(PlayerComponent.class).getData().speed()*-1, 0));
                player.getComponent(MovementComponent.class).translate();
                leftPress = true;
            }
            @Override
            protected void onActionEnd(){
                leftPress = false;
            }
        }, KeyCode.A);

        getInput().addAction(new UserAction("Right"){
            @Override
            protected void onAction(){
                player.getComponent(MovementComponent.class).changeMovement(new Vec2(player.getComponent(PlayerComponent.class).getData().speed(), 0));
                player.getComponent(MovementComponent.class).translate();
                rightPress = true;
            }

            @Override
            protected void onActionEnd(){
                rightPress = false;
            }
        }, KeyCode.D);

        getInput().addAction(new UserAction("Down"){
            @Override
            protected void onAction(){
                player.getComponent(MovementComponent.class).changeMovement(new Vec2(0, player.getComponent(PlayerComponent.class).getData().speed()*-1));
                player.getComponent(MovementComponent.class).translate();
                downPress = true;
            }
            @Override
            protected void onActionEnd(){
                downPress = false;
            }
        }, KeyCode.S);

        getInput().addAction(new UserAction("Up"){
            @Override
            protected void onAction(){
                player.getComponent(MovementComponent.class).changeMovement(new Vec2(0, player.getComponent(PlayerComponent.class).getData().speed()));
                player.getComponent(MovementComponent.class).translate();
                upPress = true;
            }

            @Override
            protected void onActionEnd(){
                upPress = false;
            }
        }, KeyCode.W);

        getInput().addAction(new UserAction("info"){
            @Override
            protected void onAction(){
                System.out.println("Stone count " + geti(STONE)+ "\n Wood count: " + geti(WOOD));
            }
        }, KeyCode.I);
    }

    @Override
    protected void initPhysics() {
        int playerCount = 1;
        onCollision(Type.BUILDING, Type.ENEMY, (building, enemy) -> {
            var hp = building.getComponent(HealthIntComponent.class);
            if (hp.getValue() > 1){
                hp.damage(1);
                return;
            }
            building.removeFromWorld();
        });
        onCollision(Type.PLAYER, Type.ENEMY, (player, enemy) -> {
            var hp = player.getComponent(HealthIntComponent.class);
            if (hp.getValue() > 1){
                hp.damage(1);
                return;
            }
            player.removeFromWorld();
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
//        System.out.println("new point:" + newPoint + "\n playerLocation:" + thing1.getPosition() + " player Dimensions: " + thing1.getWidth()+ " height:" + thing1.getHeight()+ "\n objectLocation:" + thing2.getPosition() +"object dimensions:" + thing2.getWidth() + "height: " + thing2.getHeight());
        System.out.println("Stone count " + geti(STONE)+ "\n Wood count: " + geti(WOOD));
        return newPoint;
    }
    public void onResourceClicked(Entity e) {
        if (e.getProperties().exists("resourceHarvester")) {
            onHarvesterClicked(e);
        } else {
            collectResource(e, +1);
        }
    }
    public static void collectResource(Entity e, double increment){
        switch ((Type)e.getType()) {
            case TREE -> inc(WOOD, increment);
            case STONE -> inc(STONE, increment);
        }
    }

    private void onHarvesterClicked(Entity e) {//upgrade harvester

    }
    public void onCellClicked(Entity cell) {
        // if we already have a tower on this tower base, ignore call
        if (cell.getProperties().exists("tower"))
            return;

//        towerSelectionBox.setCell(cell);
//        towerSelectionBox.setVisible(true);

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
//                        towerSelectionBox.setVisible(false);
                    });
                });
    }
    public void removeEntity(Entity object){
        object.removeFromWorld();
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
