package com.example.spacecolony.model;

/**
 * Soldier specialization.
 * Default stats: skill=9, resilience=0, maxEnergy=16
 * Special ability: Berserker - deals 3x skill but with more randomness
 * Bonus: +3 skill on ALIEN_ATTACK missions
 */
public class Soldier extends CrewMember {

    public Soldier(String name) {
        super(name, "Soldier", 9, 0, 16);
    }

    /**
     * Special ability: Berserker - massive damage but high variance.
     */
    @Override
    public int specialAbility() {
        int skill = getEffectiveSkill();
        int randomBonus = (int)(Math.random() * 6); // higher variance
        return (int)(skill * 2.5) + randomBonus;
    }

    @Override
    public String specialAbilityName() {
        return "Berserker Charge";
    }

    /**
     * Specialization bonus: +3 skill on alien attack missions.
     */
    @Override
    public int act(MissionType missionType) {
        int base = act();
        if (missionType == MissionType.ALIEN_ATTACK
                || missionType == MissionType.FIRE) {
            return base + 3;
        }
        return base;
    }
}