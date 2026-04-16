package com.example.spacecolony.model;

/**
 * Engineer specialization.
 * Default stats: skill=6, resilience=3, maxEnergy=19
 * Special ability: System Overload (2x skill damage, costs energy)
 * Bonus: +2 skill on REPAIR_STATION and FUEL_LEAK missions
 */
public class Engineer extends CrewMember {

    public Engineer(String name) {
        super(name, "Engineer", 6, 3, 19);
    }

    /**
     * Special ability: System Overload - deals 2x skill damage but costs 3 energy.
     */
    @Override
    public int specialAbility() {
        int skill = getEffectiveSkill();
        int randomBonus = (int)(Math.random() * 3);
        // Costs 3 energy to use
        int currentEnergy = getEnergy();
        setEnergy(Math.max(1, currentEnergy - 3));
        return (skill * 2) + randomBonus;
    }

    @Override
    public String specialAbilityName() {
        return "System Overload";
    }

    /**
     * Specialization bonus: +2 skill on repair/fuel missions.
     */
    @Override
    public int act(MissionType missionType) {
        int base = act();
        if (missionType == MissionType.REPAIR_STATION
                || missionType == MissionType.FUEL_LEAK) {
            return base + 2;
        }
        return base;
    }
}