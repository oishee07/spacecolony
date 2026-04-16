package com.example.spacecolony.data;

import com.example.spacecolony.model.CrewMember;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton storage class for all crew members.
 * Uses HashMap<Integer, CrewMember> as the primary data structure.
 */
public class Storage {

    // ── Singleton ─────────────────────────────────────────────────────────
    private static Storage instance;

    public static Storage getInstance() {
        if (instance == null) {
            instance = new Storage();
        }
        return instance;
    }

    // ── Data ──────────────────────────────────────────────────────────────
    private final HashMap<Integer, CrewMember> crewMap = new HashMap<>();
    private String colonyName = "Alpha Base";
    private int missionCount = 0;           // total missions completed
    private int totalRecruited = 0;         // total crew ever recruited
    private int totalMissionsLaunched = 0;  // missions launched (including failures)

    private Storage() {}

    // ── CRUD ──────────────────────────────────────────────────────────────

    /** Adds a new crew member to storage. */
    public void addCrewMember(CrewMember cm) {
        crewMap.put(cm.getId(), cm);
        totalRecruited++;
    }

    /** Retrieves a crew member by ID. Returns null if not found. */
    public CrewMember getCrewMember(int id) {
        return crewMap.get(id);
    }

    /** Removes a crew member by ID (death). */
    public void removeCrewMember(int id) {
        crewMap.remove(id);
    }

    /** Returns all crew members as an ArrayList (for RecyclerView). */
    public List<CrewMember> listCrewMembers() {
        return new ArrayList<>(crewMap.values());
    }

    /** Returns crew members filtered by location. */
    public List<CrewMember> getCrewByLocation(CrewMember.Location location) {
        List<CrewMember> result = new ArrayList<>();
        for (CrewMember cm : crewMap.values()) {
            if (cm.getLocation() == location) {
                result.add(cm);
            }
        }
        return result;
    }

    /** Returns count of crew in each location. */
    public int getCountByLocation(CrewMember.Location location) {
        int count = 0;
        for (CrewMember cm : crewMap.values()) {
            if (cm.getLocation() == location) count++;
        }
        return count;
    }

    /** Raw map access (needed for DataManager save/load). */
    public HashMap<Integer, CrewMember> getCrewMap() {
        return crewMap;
    }

    /** Replaces entire map (used during load). */
    public void loadCrewMap(Map<Integer, CrewMember> map) {
        crewMap.clear();
        crewMap.putAll(map);
    }

    // ── Colony statistics ─────────────────────────────────────────────────

    public void incrementMissionCount() { missionCount++; }
    public void incrementMissionsLaunched() { totalMissionsLaunched++; }
    public int getMissionCount() { return missionCount; }
    public int getTotalRecruited() { return totalRecruited; }
    public int getTotalMissionsLaunched() { return totalMissionsLaunched; }
    public String getColonyName() { return colonyName; }
    public void setColonyName(String name) { this.colonyName = name; }
    public void setMissionCount(int v) { this.missionCount = v; }
    public void setTotalRecruited(int v) { this.totalRecruited = v; }
    public void setTotalMissionsLaunched(int v) { this.totalMissionsLaunched = v; }

    public int getTotalCrewAlive() {
        return crewMap.size();
    }
}