package com.csafinal.basedefense.components;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.csafinal.basedefense.data.LivingThingData;

public class MovementComponent extends Component {
    double speed;
    String name;
    public MovementComponent(double speed, String name){
        this.speed = speed;
        this.name=name;
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

    @Override
    public void onUpdate(double tpf){//debug
        System.out.println(name + "  " + movement);
    }
}
