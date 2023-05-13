package com.csafinal.basedefense.components;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.csafinal.basedefense.data.LivingThingData;

public class PlayerComponent extends Component {
    Entity entity;
    private LivingThingData data;
    public PlayerComponent(LivingThingData data){
        this.data = data;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public LivingThingData getData(){
        return data;
    }
}
