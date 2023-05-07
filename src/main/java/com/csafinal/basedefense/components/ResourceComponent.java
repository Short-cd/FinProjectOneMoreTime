package com.csafinal.basedefense.components;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.LocalTimer;
import com.csafinal.basedefense.data.ResourceData;

public class ResourceComponent extends Component {
    private LocalTimer gatherTimer;
    private ResourceData data;

    public ResourceComponent(ResourceData data) {
        this.data = data;
    }

    public int getGatherAmt() {
        return data.gatherAmt();
    }

}
