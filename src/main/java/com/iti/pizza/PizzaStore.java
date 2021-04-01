package com.iti.pizza;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PizzaStore {

    private final MealFactory mealFactory = new MealFactory();

    public Meal order(String mealName) {
        return mealFactory.create(mealName);
    }

    public static void main(String[] args) throws IOException {
        PizzaStore pizzaStore = new PizzaStore();
        Meal meal = pizzaStore.order(readConsole());
        System.out.println("Bill: $" + meal.getPrice());
    }

    public static String readConsole() throws IOException {
        System.out.print("What do you Like?: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        return reader.readLine();
    }
}
