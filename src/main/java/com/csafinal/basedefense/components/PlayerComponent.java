package com.csafinal.basedefense.components;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.csafinal.basedefense.data.LivingThingData;

public class PlayerComponent extends Component {
    Entity entity;
    private LivingThingData data;
    public PlayerComponent(Entity player, LivingThingData data){
        entity = player;
        this.data = data;
    }
    public Entity getPlayer(){
        return entity;
    }
    public LivingThingData getData(){
        return data;
    }
}
