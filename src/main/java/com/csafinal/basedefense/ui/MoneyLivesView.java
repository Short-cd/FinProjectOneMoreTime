package com.csafinal.basedefense.ui;

import com.almasb.fxgl.dsl.FXGL;
import com.csafinal.basedefense.DropApp;
import com.csafinal.basedefense.data.Vars;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGLForKtKt.geti;

public class MoneyLivesView extends Parent {
    public MoneyLivesView() {
        var bg = new Rectangle(150, 125, Color.color(0.5, 0.5, 0.5, 0.75));
        bg.setStroke(Color.color(0, 0, 0, 0.9));
        bg.setStrokeWidth(2.5);
        bg.setArcWidth(10);
        bg.setArcHeight(10);

        var textMoney = FXGL.getUIFactoryService().newText("$ " + geti(Vars.MONEY), Color.WHITE, 22.0);
        textMoney.setTranslateX(10);
        textMoney.setTranslateY(25);

        var textLives = FXGL.getUIFactoryService().newText("Lives: " + FXGL.<DropApp>getAppCast().lives, Color.WHITE, 22.0);
        textLives.setTranslateX(10);
        textLives.setTranslateY(50);

        var textWood = FXGL.getUIFactoryService().newText("Wood: " + FXGL.<DropApp>getAppCast().getWood(), Color.WHITE, 22.0);
        textWood.setTranslateX(10);
        textWood.setTranslateY(75);

        var textStone = FXGL.getUIFactoryService().newText("Stone: " + FXGL.<DropApp>getAppCast().getCobblestone(), Color.WHITE, 22.0);
        textStone.setTranslateX(10);
        textStone.setTranslateY(100);


        resetChildren();
    }

    public void resetChildren() {
        getChildren().clear();

        var bg = new Rectangle(150, 125, Color.color(0.5, 0.5, 0.5, 0.75));
        bg.setStroke(Color.color(0, 0, 0, 0.9));
        bg.setStrokeWidth(2.5);
        bg.setArcWidth(10);
        bg.setArcHeight(10);

        var textMoney = FXGL.getUIFactoryService().newText("$ " + geti(Vars.MONEY), Color.WHITE, 22.0);
        textMoney.setTranslateX(10);
        textMoney.setTranslateY(25);

        var textLives = FXGL.getUIFactoryService().newText("Lives: " + FXGL.<DropApp>getAppCast().lives, Color.WHITE, 22.0);
        textLives.setTranslateX(10);
        textLives.setTranslateY(50);

        var textWood = FXGL.getUIFactoryService().newText("Wood: " + FXGL.<DropApp>getAppCast().getWood(), Color.WHITE, 22.0);
        textWood.setTranslateX(10);
        textWood.setTranslateY(75);

        var textStone = FXGL.getUIFactoryService().newText("Stone: " + FXGL.<DropApp>getAppCast().getCobblestone(), Color.WHITE, 22.0);
        textStone.setTranslateX(10);
        textStone.setTranslateY(100);

        getChildren().addAll(bg, textMoney, textLives, textWood, textStone);
    }


}
