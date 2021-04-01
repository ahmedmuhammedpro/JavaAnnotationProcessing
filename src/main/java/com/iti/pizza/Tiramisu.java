package com.iti.pizza;

import com.iti.pizzafactory.annotations.Factory;

@Factory(type = Meal.class, id = "Tiramisu")
public class Tiramisu implements Meal {
    @Override
    public float getPrice() {
        return 4.5f;
    }
}
