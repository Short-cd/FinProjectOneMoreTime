package com.csafinal.basedefense.components;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.csafinal.basedefense.data.LivingThingData;

public class MovementComponent extends Component {

    LivingThingData data;
    public MovementComponent(LivingThingData data, Entity e){
        this.data = data;
        entity = e;
    }

    public void setMovement(Vec2 direction){
        movement = direction;
    }
    Vec2 movement = new Vec2(0, 0);

    public void changeMovement(Vec2 newDirection){
        movement = movement.add(newDirection);
    }

    public void translate(){
        entity.translate(movement);
    }
}
