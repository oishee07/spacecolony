package com.example.spacecolony.model;

/**
 * Represents a system-generated threat that crew members fight.
 * Stats scale with mission count for increasing difficulty.
 */
public class Threat {

    private String name;
    private int skill;
    private int resilience;
    private int energy;
    private int maxEnergy;
    private MissionType missionType;

    /**
     * Creates a threat scaled to the current mission number.
     *
     * @param missionCount number of missions completed so far
     */
    public Threat(int missionCount) {
        this.missionType = MissionType.random();
        this.name = generateName(missionType);
        // Scaling formula: base stats + mission count
        this.skill = 4 + missionCount;
        this.resilience = 1 + (missionCount / 3);
        this.maxEnergy = 20 + (missionCount * 3);
        this.energy = maxEnergy;
    }

    /**
     * Generates a thematic threat name based on mission type.
     */
    private String generateName(MissionType type) {
        switch (type) {
            case ASTEROID_FIELD:    return "Asteroid Storm";
            case REPAIR_STATION:    return "Critical System Failure";
            case ALIEN_ATTACK:      return "Alien Warband";
            case SOLAR_FLARE:       return "Solar Flare Surge";
            case FUEL_LEAK:         return "Fuel Line Rupture";
            case FIRE:              return "Kitchen Inferno";
            case BROKEN_HEATING:    return "Cryogenic Malfunction";
            case ALIEN_DISEASE:     return "Xeno-Pathogen Outbreak";
            default:                return "Unknown Threat";
        }
    }

    /**
     * Threat attacks a crew member.
     * Returns damage dealt after crew member defends.
     */
    public int attack() {
        // Threat has some randomness too
        int randomBonus = (int)(Math.random() * 3);
        return skill + randomBonus;
    }

    /**
     * Threat takes damage (crew member attacks).
     * @param incomingDamage raw damage from crew member
     * @return actual damage taken (after resilience)
     */
    public int takeDamage(int incomingDamage) {
        int actualDmg = Math.max(1, incomingDamage - resilience);
        energy = Math.max(0, energy - actualDmg);
        return actualDmg;
    }

    public boolean isDefeated() {
        return energy <= 0;
    }

    // ── Getters ───────────────────────────────────────────────────────────
    public String getName() { return name; }
    public int getSkill() { return skill; }
    public int getResilience() { return resilience; }
    public int getEnergy() { return energy; }
    public int getMaxEnergy() { return maxEnergy; }
    public MissionType getMissionType() { return missionType; }

    @Override
    public String toString() {
        return name + " (skill:" + skill + " res:" + resilience
                + " energy:" + energy + "/" + maxEnergy + ")";
    }
}