package com.csafinal.basedefense.data;

import java.util.Collections;
import java.util.List;

public record ResourceData(
    String name,
    String imageName,
    int gatherAmt,
    double gatherRate,
    int cost
    ) {
}
