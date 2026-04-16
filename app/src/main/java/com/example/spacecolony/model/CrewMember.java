package com.example.spacecolony.model;

import java.io.Serializable;

/**
 * Abstract base class for all crew member specializations.
 * Implements encapsulation, inheritance, and polymorphism.
 */
public abstract class CrewMember implements Serializable {

    // ── Locations a crew member can occupy ──────────────────────────────
    public enum Location { QUARTERS, SIMULATOR, MISSION_CONTROL, MEDBAY }

    // ── Static ID counter (persisted via DataManager) ────────────────────
    private static int idCounter = 1;

    // ── Fields ────────────────────────────────────────────────────────────
    private final int id;
    private String name;
    private int baseSkill;          // base skill (never changes after recruit)
    private int resilience;
    private int experience;
    private int energy;
    private int maxEnergy;
    private Location location;
    private String specialization;

    // ── Statistics fields ─────────────────────────────────────────────────
    private int missionsCompleted;
    private int missionsWon;
    private int trainingSessions;
    private int totalDamageDealt;

    // ── Constructor ───────────────────────────────────────────────────────
    public CrewMember(String name, String specialization,
                      int baseSkill, int resilience, int maxEnergy) {
        this.id = idCounter++;
        this.name = name;
        this.specialization = specialization;
        this.baseSkill = baseSkill;
        this.resilience = resilience;
        this.maxEnergy = maxEnergy;
        this.energy = maxEnergy;
        this.experience = 0;
        this.location = Location.QUARTERS;
    }

    // ── Combat Methods ────────────────────────────────────────────────────

    /**
     * Calculates attack damage: (baseSkill + experience) with optional randomness.
     * Subclasses override this for specialization bonuses.
     */
    public int act() {
        int skill = getEffectiveSkill();
        // Randomness bonus (+0 to +2)
        int randomBonus = (int)(Math.random() * 3);
        return skill + randomBonus;
    }

    /**
     * Calculates attack damage for a specific mission type (specialization bonus).
     */
    public int act(MissionType missionType) {
        return act(); // Base: no bonus. Subclasses override.
    }

    /**
     * Special ability attack (tactical combat). Subclasses define their own.
     */
    public abstract int specialAbility();

    /**
     * Returns a description of the special ability for UI display.
     */
    public abstract String specialAbilityName();

    /**
     * Defends against incoming damage: damage - resilience (min 1).
     */
    public void defend(int incomingDamage) {
        int dmgTaken = Math.max(1, incomingDamage - resilience);
        energy = Math.max(0, energy - dmgTaken);
    }

    /**
     * Returns effective skill = baseSkill + experience.
     */
    public int getEffectiveSkill() {
        return baseSkill + experience;
    }

    // ── State helpers ─────────────────────────────────────────────────────

    public boolean isAlive() {
        return energy > 0;
    }

    /** Fully restores energy to maxEnergy (used when returning to Quarters). */
    public void restoreEnergy() {
        this.energy = this.maxEnergy;
    }

    /** Awards 1 XP from training. */
    public void train() {
        this.experience++;
        this.trainingSessions++;
    }

    /** Awards XP after a successful mission. */
    public void gainExperience(int amount) {
        this.experience += amount;
        this.missionsCompleted++;
        this.missionsWon++;
    }

    /** Called when a mission is lost (crew member sent to Medbay). */
    public void sendToMedbay() {
        this.location = Location.MEDBAY;
        this.missionsCompleted++;
        // Reset to initial stats as penalty
        this.energy = maxEnergy;
        this.experience = Math.max(0, this.experience - 1); // Lose 1 XP as penalty
    }

    // ── Getters & Setters ─────────────────────────────────────────────────

    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getBaseSkill() { return baseSkill; }
    public int getResilience() { return resilience; }
    public int getExperience() { return experience; }
    public void setExperience(int experience) { this.experience = experience; }
    public int getEnergy() { return energy; }
    public void setEnergy(int energy) { this.energy = energy; }
    public int getMaxEnergy() { return maxEnergy; }
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
    public String getSpecialization() { return specialization; }
    public int getMissionsCompleted() { return missionsCompleted; }
    public int getMissionsWon() { return missionsWon; }
    public int getTrainingSessions() { return trainingSessions; }
    public int getTotalDamageDealt() { return totalDamageDealt; }
    public void addDamageDealt(int dmg) { this.totalDamageDealt += dmg; }
    public void setMissionsCompleted(int v) { this.missionsCompleted = v; }
    public void setMissionsWon(int v) { this.missionsWon = v; }
    public void setTrainingSessions(int v) { this.trainingSessions = v; }
    public void setTotalDamageDealt(int v) { this.totalDamageDealt = v; }

    /** Static counter management for save/load. */
    public static void setIdCounter(int val) { idCounter = val; }
    public static int getIdCounter() { return idCounter; }

    @Override
    public String toString() {
        return specialization + "(" + name + ") skill:" + getEffectiveSkill()
                + " res:" + resilience + " exp:" + experience
                + " energy:" + energy + "/" + maxEnergy;
    }
}