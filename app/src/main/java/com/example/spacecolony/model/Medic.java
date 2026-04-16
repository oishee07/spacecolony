package com.example.spacecolony.model;

/**
 * Medic specialization.
 * Default stats: skill=7, resilience=2, maxEnergy=18
 * Special ability: Heal - restores 5 energy to self instead of attacking
 * Bonus: +3 skill on ALIEN_DISEASE missions
 */
public class Medic extends CrewMember {

    public Medic(String name) {
        super(name, "Medic", 7, 2, 18);
    }

    /**
     * Special ability: Field Medic - heals 5 energy and deals reduced damage.
     */
    @Override
    public int specialAbility() {
        // Heal self
        int healed = Math.min(5, getMaxEnergy() - getEnergy());
        setEnergy(getEnergy() + healed);
        int randomBonus = (int)(Math.random() * 3);
        // Still deals some damage with medical tools
        return (getEffectiveSkill() / 2) + randomBonus;
    }

    @Override
    public String specialAbilityName() {
        return "Field Medic (Heal)";
    }

    /**
     * Specialization bonus: +3 skill on alien disease outbreak.
     */
    @Override
    public int act(MissionType missionType) {
        int base = act();
        if (missionType == MissionType.ALIEN_DISEASE) {
            return base + 3;
        }
        return base;
    }
}