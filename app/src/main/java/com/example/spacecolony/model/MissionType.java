package com.example.spacecolony.model;

/**
 * Enum representing different mission types.
 * Used for specialization bonuses (polymorphism).
 */
public enum MissionType {
    ASTEROID_FIELD("Asteroid Field Navigation"),
    REPAIR_STATION("Repair Station"),
    ALIEN_ATTACK("Alien Attack"),
    SOLAR_FLARE("Solar Flare Response"),
    FUEL_LEAK("Fuel Leak Control"),
    FIRE("Fire in the Kitchen"),
    BROKEN_HEATING("Broken Heating System"),
    ALIEN_DISEASE("Alien Disease Outbreak");

    private final String displayName;

    MissionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /** Returns a random mission type. */
    public static MissionType random() {
        MissionType[] values = values();
        return values[(int)(Math.random() * values.length)];
    }
}
