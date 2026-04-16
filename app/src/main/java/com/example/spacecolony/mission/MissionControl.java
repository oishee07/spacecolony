package com.example.spacecolony.mission;

import com.example.spacecolony.data.Storage;
import com.example.spacecolony.model.CrewMember;
import com.example.spacecolony.model.Threat;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles mission logic: creates threats, runs turn-based combat.
 * Supports squads of 2 or 3 crew members (Larger Squads bonus feature).
 */
public class MissionControl {

    // ── Action types for tactical combat ─────────────────────────────────
    public enum ActionType { ATTACK, DEFEND, SPECIAL }

    // ── Mission result ────────────────────────────────────────────────────
    public static class MissionResult {
        public boolean victory;
        public List<String> log = new ArrayList<>();
        public List<CrewMember> survivors = new ArrayList<>();
        public List<CrewMember> casualties = new ArrayList<>();

        public MissionResult(boolean victory) {
            this.victory = victory;
        }
    }

    // ── State ─────────────────────────────────────────────────────────────
    private List<CrewMember> squad;
    private Threat threat;
    private int currentMemberIndex;
    private int roundNumber;
    private List<String> missionLog;
    private boolean missionOver;
    private boolean playerVictory;

    // ── Factory: start a new mission ──────────────────────────────────────
    public MissionControl(List<CrewMember> squad) {
        this.squad = new ArrayList<>(squad);
        this.threat = new Threat(Storage.getInstance().getMissionCount());
        this.currentMemberIndex = 0;
        this.roundNumber = 1;
        this.missionLog = new ArrayList<>();
        this.missionOver = false;
        this.playerVictory = false;

        Storage.getInstance().incrementMissionsLaunched();

        // Opening log
        missionLog.add("=== MISSION: " + threat.getMissionType().getDisplayName() + " ===");
        missionLog.add("Threat: " + threat.toString());
        for (CrewMember cm : squad) {
            missionLog.add(cm.toString());
        }
        missionLog.add("--- Round " + roundNumber + " ---");
    }

    // ── Query ─────────────────────────────────────────────────────────────

    public boolean isMissionOver() { return missionOver; }
    public boolean isPlayerVictory() { return playerVictory; }
    public Threat getThreat() { return threat; }
    public List<String> getMissionLog() { return missionLog; }
    public List<CrewMember> getSquad() { return squad; }

    /** Returns the crew member whose turn it is. */
    public CrewMember getCurrentMember() {
        if (squad.isEmpty()) return null;
        return squad.get(currentMemberIndex % squad.size());
    }

    // ── Tactical step: player chooses action ──────────────────────────────

    /**
     * Executes one player action for the current crew member.
     * Then the threat retaliates.
     * Advances to next alive member.
     * Returns a log of what happened this step.
     */
    public List<String> executeTurn(ActionType action) {
        List<String> stepLog = new ArrayList<>();
        if (missionOver) return stepLog;

        CrewMember actor = getCurrentMember();
        if (actor == null) return stepLog;

        // ── Crew member acts ─────────────────────────────────────────────
        int rawDamage;
        String actionLabel;
        if (action == ActionType.SPECIAL) {
            rawDamage = actor.specialAbility();
            actionLabel = actor.specialAbilityName();
        } else if (action == ActionType.DEFEND) {
            // Defend: reduce incoming damage next hit (simulate by boosting resilience temporarily)
            // For simplicity: defend grants double resilience this turn (stored as flag in damage calc)
            rawDamage = actor.act(threat.getMissionType()) / 2; // half damage when defending
            actionLabel = "Defend + Counter";
        } else {
            rawDamage = actor.act(threat.getMissionType());
            actionLabel = "Attack";
        }

        int dmgDealt = threat.takeDamage(rawDamage);
        actor.addDamageDealt(dmgDealt);
        stepLog.add(actor.getSpecialization() + "(" + actor.getName() + ") uses " + actionLabel);
        stepLog.add("  Damage dealt: " + rawDamage + " - " + threat.getResilience() + " = " + dmgDealt);
        stepLog.add("  " + threat.getName() + " energy: " + threat.getEnergy() + "/" + threat.getMaxEnergy());

        // ── Check threat defeated ────────────────────────────────────────
        if (threat.isDefeated()) {
            stepLog.add("=== MISSION COMPLETE ===");
            stepLog.add("The " + threat.getName() + " has been neutralized!");
            // Award XP
            for (CrewMember cm : squad) {
                cm.gainExperience(1);
                stepLog.add(cm.getSpecialization() + "(" + cm.getName() + ") gains 1 XP. (exp:" + cm.getExperience() + ")");
                cm.setLocation(CrewMember.Location.MISSION_CONTROL); // returned to MC
            }
            Storage.getInstance().incrementMissionCount();
            missionOver = true;
            playerVictory = true;
            missionLog.addAll(stepLog);
            return stepLog;
        }

        // ── Threat retaliates ────────────────────────────────────────────
        int threatDmg = threat.attack();
        int resilience = (action == ActionType.DEFEND) ? actor.getResilience() * 2 : actor.getResilience();
        int dmgReceived = Math.max(1, threatDmg - resilience);
        actor.setEnergy(Math.max(0, actor.getEnergy() - dmgReceived));
        stepLog.add(threat.getName() + " retaliates against " + actor.getName());
        stepLog.add("  Damage dealt: " + threatDmg + " - " + resilience + " = " + dmgReceived);
        stepLog.add("  " + actor.getSpecialization() + "(" + actor.getName() + ") energy: "
                + actor.getEnergy() + "/" + actor.getMaxEnergy());

        // ── Check crew member defeated ───────────────────────────────────
        if (!actor.isAlive()) {
            stepLog.add(actor.getName() + " has been incapacitated! → Sent to Medbay.");
            actor.sendToMedbay(); // No death feature: go to Medbay
            squad.remove(actor);
            if (currentMemberIndex >= squad.size()) currentMemberIndex = 0;
        } else {
            // Advance to next member
            currentMemberIndex = (currentMemberIndex + 1) % squad.size();
        }

        // ── Check all crew defeated ──────────────────────────────────────
        if (squad.isEmpty()) {
            stepLog.add("=== MISSION FAILED ===");
            stepLog.add("Mission failed. All crew members lost.");
            missionOver = true;
            playerVictory = false;
            missionLog.addAll(stepLog);
            return stepLog;
        }

        // ── New round? ───────────────────────────────────────────────────
        if (currentMemberIndex == 0) {
            roundNumber++;
            stepLog.add("--- Round " + roundNumber + " ---");
        }

        missionLog.addAll(stepLog);
        return stepLog;
    }

    /**
     * Builds the final MissionResult after mission ends.
     */
    public MissionResult buildResult() {
        MissionResult result = new MissionResult(playerVictory);
        result.log = new ArrayList<>(missionLog);
        // All crew still in squad are survivors
        result.survivors = new ArrayList<>(squad);
        return result;
    }
}