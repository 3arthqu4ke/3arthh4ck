package me.earth.earthhack.api.module.util;

import me.earth.earthhack.api.util.interfaces.Nameable;

/*
 * This is such a weird pseudo enum because I want to keep compatibility with
 * old plugins.
 */
public class Category implements Nameable
{
    public static final Category Combat = new Category("Combat", 0);
    public static final Category Misc = new Category("Misc", 1);
    public static final Category Render = new Category("Render", 2);
    public static final Category Movement = new Category("Movement", 3);
    public static final Category Player = new Category("Player", 4);
    public static final Category Client = new Category("Client", 5);

    private static final Category[] VALUES = {
        Combat, Misc, Render, Movement, Player, Client
    };

    private final String name;
    private final int ordinal;

    public Category(String name, int ordinal) {
        this.name = name;
        this.ordinal = ordinal;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public String name() {
        return name;
    }

    public int ordinal() {
        return this.ordinal;
    }

    public static Category[] values() {
        return VALUES.clone();
    }

    public enum CategoryEnum {
        Combat(Category.Combat),
        Misc(Category.Misc),
        Render(Category.Render),
        Movement(Category.Movement),
        Player(Category.Player),
        Client(Category.Client);

        private final Category value;

        CategoryEnum(Category value) {
            this.value = value;
        }

        public Category toValue() {
            return value;
        }
    }

}
