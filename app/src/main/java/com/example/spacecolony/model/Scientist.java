package com.example.spacecolony.model;

/**
 * Scientist specialization.
 * Default stats: skill=8, resilience=1, maxEnergy=17
 * Special ability: Critical Analysis - guaranteed high damage (skill + 5)
 * Bonus: +2 skill on SOLAR_FLARE and BROKEN_HEATING missions
 */
public class Scientist extends CrewMember {

    public Scientist(String name) {
        super(name, "Scientist", 8, 1, 17);
    }

    /**
     * Special ability: Critical Analysis - very high damage, no random penalty.
     */
    @Override
    public int specialAbility() {
        return getEffectiveSkill() + 5;
    }

    @Override
    public String specialAbilityName() {
        return "Critical Analysis";
    }

    /**
     * Specialization bonus: +2 on environment/physics-related missions.
     */
    @Override
    public int act(MissionType missionType) {
        int base = act();
        if (missionType == MissionType.SOLAR_FLARE
                || missionType == MissionType.BROKEN_HEATING) {
            return base + 2;
        }
        return base;
    }
}