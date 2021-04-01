package com.iti.pizza;

import com.iti.pizzafactory.annotations.Factory;

@Factory(type = Meal.class, id = "Margherita")
public class MargheritaPizza implements Meal {
    @Override
    public float getPrice() {
        return 6.0f;
    }
}
