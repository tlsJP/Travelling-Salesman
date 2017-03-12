package com.jp.core;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Created by JP on 3/10/2017.
 */
public class AddressedCircle extends Circle {

    public AddressedCircle(int x, int y, int cityRadius, Color c) {
        super(x, y, cityRadius, c);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(" + getCenterX() + "," + getCenterY() + ")";
    }
}
