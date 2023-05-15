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
import com.csafinal.basedefense.data.ResourceData;
import com.csafinal.basedefense.data.TowerData;
import com.csafinal.basedefense.ui.MoneyLivesView;
import com.csafinal.basedefense.ui.ResourceSelectionBox;
import com.csafinal.basedefense.ui.TowerSelectionBox;
import javafx.animation.Interpolator;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import java.util.List;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.csafinal.basedefense.DropApp.Type.TREE;
import static com.csafinal.basedefense.data.Vars.*;
// NOTE: this import above is crucial, it pulls in many useful methods

public class DropApp extends GameApplication {


    public enum Type {
        BUILDING, ENEMY, PLAYER, TREE, STONE, BULLET, TOWER_BASE

    }
    List<String> levelNames = List.of(
            "level1.json"
    );
    boolean downPress, upPress, leftPress, rightPress;
//    private SelectBuildingBox buildingSelectionBox;
    private Entity player;

    private boolean playerAlive;

    public static int cobblestone = 0;
    public static int wood = 0;

    public static int getCobblestone() {
        return cobblestone;
    }

    public static int getWood() {
        return wood;
    }

    long startTime;

    public int lives = 3;

    private int numTowers = 0;

    static MoneyLivesView stats;

    private static List<TowerData> towerData;
    private static List<ResourceData> treeData;
    private static List<ResourceData> stoneData;

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
        vars.put(MONEY, 1000);
        vars.put(STONE, 0);
        vars.put(WOOD, 0);
        vars.put(SCORE, 0);
        vars.put(DAY_NUMBER, 0);
    }
    @Override
    protected void initGame() {
        startTime = System.nanoTime();
        System.out.println(startTime);

        getGameWorld().addEntityFactory(new gameFactory());
        loadCurrentLevel();
        loadTowerData();
        loadTreeData();
        loadStoneData();

        LivingThingData playerData = getAssetLoader().loadJSON("playerTypes/player1.json", LivingThingData.class).get();
//        spawn("player");
        player = spawn("player", new SpawnData().put("playerData", playerData));
        playerAlive = true;


        run(() -> createEnemy(), Duration.seconds(2));
        towerSelectionBox = new TowerSelectionBox(towerData);
        treeBox = new ResourceSelectionBox(treeData);
        stoneBox = new ResourceSelectionBox(stoneData);
    }

    public void createEnemy(){
        if ((System.nanoTime() - startTime)/1000000000<30){
            return;
        }
        LivingThingData enemyData;
        int numEnemy = (int) (Math.random() * 1000000);
        if (numEnemy < 300000){
            enemyData = getAssetLoader().loadJSON("enemies/enemy1.json", LivingThingData.class).get();
        } else if (numEnemy < 500000){
            enemyData = getAssetLoader().loadJSON("enemies/enemy2.json", LivingThingData.class).get();
        } else if (numEnemy < 800000){
            enemyData = getAssetLoader().loadJSON("enemies/enemy3.json", LivingThingData.class).get();
        } else if (numEnemy < 999800){
            enemyData = getAssetLoader().loadJSON("enemies/enemy4.json", LivingThingData.class).get();
        } else {
            enemyData = getAssetLoader().loadJSON("enemies/enemy5.json", LivingThingData.class).get();
        }

        spawnWithScale("enemy",
                new SpawnData()
                        .put("eData", enemyData),
                Duration.seconds(0.1),
                Interpolator.LINEAR);
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
                System.out.println("Stone count: " + cobblestone + "\n Wood count: " + wood);
                setStats();
            }
        }, KeyCode.I);

        getInput().addAction(new UserAction("closeSelection") {
            @Override
            protected void onAction() {
                towerSelectionBox.setVisible(false);
                treeBox.setVisible(false);
                stoneBox.setVisible(false);
            }
        }, KeyCode.Q);

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
        onCollisionBegin(Type.BUILDING, Type.ENEMY, (building, enemy) -> {
            var hp = building.getComponent(HealthIntComponent.class);
            if (hp.getValue() > 1){
                hp.damage(1);
                return;
            }
            building.removeFromWorld();
            numTowers--;
        });
        onCollisionBegin(Type.PLAYER, Type.ENEMY, (player, enemy) -> {
            var hp = player.getComponent(HealthIntComponent.class);
            if (hp.getValue() > 1){
                hp.damage(1);
                return;
            }
            player.removeFromWorld();
            playerAlive = false;
            if(lives<1){
                endGame();
            }
        });
        onCollision(Type.PLAYER, Type.BUILDING, (player, nonPlayer) -> {
            player.setPosition(checkCollisionLocation(player, nonPlayer));
        });
    }

    @Override
    protected void initUI() {
        towerSelectionBox.setVisible(false);
        treeBox.setVisible(false);
        stoneBox.setVisible(false);
        setStats();
        addUINode(towerSelectionBox);
        addUINode(treeBox);
        addUINode(stoneBox);
        addUINode(stats);
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

    private ResourceSelectionBox treeBox;

    private ResourceSelectionBox stoneBox;

    public void onResourceClicked(Entity e) {
        if (e.getProperties().exists("level")) {
            onHarvesterClicked(e);
        }

        collectResource(e, 1);
        System.out.println("cellclicked: (" + e.getX() +", " + e.getY() + ")");

        if (e.isType(TREE)){
            treeBox.setCell(e);
            treeBox.setVisible(true);

            var x = e.getX() > getAppWidth() / 2.0 ? e.getX() - 250 : e.getX();

            treeBox.setTranslateX(x);
            treeBox.setTranslateY(e.getY());
        } else {
            stoneBox.setCell(e);
            stoneBox.setVisible(true);

            var x = e.getX() > getAppWidth() / 2.0 ? e.getX() - 250 : e.getX();

            stoneBox.setTranslateX(x);
            stoneBox.setTranslateY(e.getY());
        }


    }

    public void onResourceSelected(Entity cell, ResourceData data) {
        if (geti(MONEY) - data.cost() >= 0) {
            treeBox.setVisible(false);
            stoneBox.setVisible(false);

            inc(MONEY, -data.cost());

            System.out.println(cell.getPosition());
            if (cell.getProperties().exists("level")) {
                cell.setProperty("level", ((int) cell.getProperties().getValue("level")) + 1);
            } else {
                cell.setProperty("level", 1);
            }
            run(() -> collectResource(cell, 1),Duration.seconds(3/(int) cell.getProperties().getValue("level")));

            setStats();
        }
    }

    private TowerSelectionBox towerSelectionBox;

    public void onTowerSelected(Entity cell, TowerData data) {
        if (geti(MONEY) - data.cost() >= 0) {
            towerSelectionBox.setVisible(false);

            inc(MONEY, -data.cost());

            System.out.println(cell.getPosition());
            var tower = spawnWithScale(
                    "building",
                    new SpawnData(cell.getPosition()).put("towerData", data),
                    Duration.seconds(0.85),
                    Interpolators.ELASTIC.EASE_OUT()
            );

            cell.setProperty("tower", tower);

            numTowers++;
            setStats();
        }
    }

    public static void collectResource(Entity e, double increment){
        if (e.isType(TREE)) {
//                inc(WOOD, increment);
            wood+=increment;
        } else if (e.isType(Type.STONE)) {
//                inc(STONE, increment);
            cobblestone+=increment;
        }
        setStats();
    }

    private void onHarvesterClicked(Entity e) {//upgrade harvester

    }
    public void onCellClicked(Entity cell) {
        // if we already have a tower on this tower base, ignore call
        if (cell.getProperties().exists("tower")) {
            if (cell.getProperties().getValue("tower")!=null) {
                System.out.println("towerExists");
                return;
            }
        }

        System.out.println("cellclicked: (" + cell.getX() +", " + cell.getY() + ")");

        towerSelectionBox.setCell(cell);
        towerSelectionBox.setVisible(true);

        var x = cell.getX() > getAppWidth() / 2.0 ? cell.getX() - 250 : cell.getX();

        towerSelectionBox.setTranslateX(x);
        towerSelectionBox.setTranslateY(cell.getY());
    }

    public static void setStats() {
        if (stats == null){
            stats = new MoneyLivesView();
        }
        stats.resetChildren();
        stats.setVisible(true);
    }

    @Override
    protected void onUpdate(double tpf) {
        setStats();
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

    private void loadTreeData() {
        List<String> treeName = List.of(
                "tree1.json"
        );

        treeData = treeName.stream()
                .map(name -> getAssetLoader().loadJSON("towers/" + name, ResourceData.class).get())
                .toList();
    }

    private void loadStoneData() {
        List<String> stoneName = List.of(
                "stone1.json"
        );

        stoneData = stoneName.stream()
                .map(name -> getAssetLoader().loadJSON("towers/" + name, ResourceData.class).get())
                .toList();
    }


    private void loadCurrentLevel() {
        setLevelFromMap("tmx/td1.tmx");

        getGameWorld().getEntitiesFiltered(e -> e.isType("TiledMapLayer"))
                .forEach(e -> {
                    e.getViewComponent().addOnClickHandler(event -> {
                        spawn("towerBase", new SpawnData(event.getX(),event.getY()));
                    });
                });
    }
    public void removeEntity(Entity object){
        object.removeFromWorld();
    }

    public void onEnemyKilled(Entity enemy) {
        inc(MONEY, enemy.getComponent(EnemyComponent.class).getData().reward());
        setStats();
    }

    public void revivePlayer(){
        if (!playerAlive) {
            LivingThingData playerData = getAssetLoader().loadJSON("playerTypes/player1.json", LivingThingData.class).get();
//        spawn("player");
            player = spawn("player", new SpawnData().put("playerData", playerData));
            playerAlive = true;
            lives--;
            if (lives<=0){
                endGame();
            }
        }
        setStats();
    }

    private void endGame(){
        long score = (System.nanoTime()-startTime)/1000;
        System.out.println("Score: " + score);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
