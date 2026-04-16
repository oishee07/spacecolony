package com.example.spacecolony.model;

/**
 * Pilot specialization.
 * Default stats: skill=5, resilience=4, maxEnergy=20
 * Special ability: Evasive Maneuver (high resilience burst)
 * Bonus: +3 skill on ASTEROID_FIELD missions (polymorphism)
 */
public class Pilot extends CrewMember {

    public Pilot(String name) {
        super(name, "Pilot", 5, 4, 20);
    }

    /**
     * Special ability: Evasive Maneuver - deals 1.5x skill damage
     */
    @Override
    public int specialAbility() {
        int skill = getEffectiveSkill();
        int randomBonus = (int)(Math.random() * 3);
        return (int)(skill * 1.5) + randomBonus;
    }

    @Override
    public String specialAbilityName() {
        return "Evasive Maneuver";
    }

    /**
     * Specialization bonus: +3 skill on Asteroid Field missions.
     */
    @Override
    public int act(MissionType missionType) {
        int base = act();
        if (missionType == MissionType.ASTEROID_FIELD) {
            return base + 3; // Pilot excels at asteroid navigation
        }
        return base;
    }
}