package com.csafinal.basedefense.components;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.LocalTimer;
import com.csafinal.basedefense.DropApp;
import com.csafinal.basedefense.data.CollectionData;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGLForKtKt.newLocalTimer;

public class CollectionComponent extends Component{
    private LocalTimer collectTimer;

    private CollectionData data;

    public CollectionComponent(CollectionData data, Entity resource){
        this.data = data;
        entity = resource;
    }
    public void onAdded(){
        collectTimer = newLocalTimer();
        collectTimer.capture();
    }

    @Override
    public void onUpdate(double tpf){
        if(collectTimer.elapsed(data.collectionInterval())){
            DropApp.collectResource(entity, data.collectionAmount());
        }
    }
}
