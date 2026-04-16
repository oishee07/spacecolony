package com.example.spacecolony.data;

import android.content.Context;
import android.util.Log;

import com.example.spacecolony.model.CrewMember;
import com.example.spacecolony.model.Engineer;
import com.example.spacecolony.model.Medic;
import com.example.spacecolony.model.Pilot;
import com.example.spacecolony.model.Scientist;
import com.example.spacecolony.model.Soldier;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Handles persistent save/load of game state to internal storage as JSON.
 * Uses Android's openFileOutput / openFileInput (no permissions needed).
 */
public class DataManager {

    private static final String TAG = "DataManager";
    private static final String SAVE_FILE = "space_colony_save.json";

    /**
     * Saves entire game state to a JSON file on internal storage.
     */
    public static void saveGame(Context context) {
        try {
            Storage storage = Storage.getInstance();
            JSONObject root = new JSONObject();

            // ── Colony meta ──────────────────────────────────────────────
            root.put("colonyName", storage.getColonyName());
            root.put("missionCount", storage.getMissionCount());
            root.put("totalRecruited", storage.getTotalRecruited());
            root.put("totalMissionsLaunched", storage.getTotalMissionsLaunched());
            root.put("idCounter", CrewMember.getIdCounter());

            // ── Crew array ───────────────────────────────────────────────
            JSONArray crewArray = new JSONArray();
            for (CrewMember cm : storage.listCrewMembers()) {
                JSONObject obj = new JSONObject();
                obj.put("id", cm.getId());
                obj.put("name", cm.getName());
                obj.put("specialization", cm.getSpecialization());
                obj.put("baseSkill", cm.getBaseSkill());
                obj.put("resilience", cm.getResilience());
                obj.put("maxEnergy", cm.getMaxEnergy());
                obj.put("energy", cm.getEnergy());
                obj.put("experience", cm.getExperience());
                obj.put("location", cm.getLocation().name());
                obj.put("missionsCompleted", cm.getMissionsCompleted());
                obj.put("missionsWon", cm.getMissionsWon());
                obj.put("trainingSessions", cm.getTrainingSessions());
                obj.put("totalDamageDealt", cm.getTotalDamageDealt());
                crewArray.put(obj);
            }
            root.put("crew", crewArray);

            // ── Write to file ────────────────────────────────────────────
            FileOutputStream fos = context.openFileOutput(SAVE_FILE, Context.MODE_PRIVATE);
            fos.write(root.toString(2).getBytes());
            fos.close();
            Log.d(TAG, "Game saved successfully.");

        } catch (Exception e) {
            Log.e(TAG, "Save failed: " + e.getMessage());
        }
    }

    /**
     * Loads game state from JSON file. Returns true if successful.
     */
    public static boolean loadGame(Context context) {
        try {
            FileInputStream fis = context.openFileInput(SAVE_FILE);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            fis.close();

            JSONObject root = new JSONObject(sb.toString());
            Storage storage = Storage.getInstance();

            // ── Colony meta ──────────────────────────────────────────────
            storage.setColonyName(root.optString("colonyName", "Alpha Base"));
            storage.setMissionCount(root.optInt("missionCount", 0));
            storage.setTotalRecruited(root.optInt("totalRecruited", 0));
            storage.setTotalMissionsLaunched(root.optInt("totalMissionsLaunched", 0));
            CrewMember.setIdCounter(root.optInt("idCounter", 1));

            // ── Crew array ───────────────────────────────────────────────
            HashMap<Integer, CrewMember> map = new HashMap<>();
            JSONArray crewArray = root.getJSONArray("crew");
            for (int i = 0; i < crewArray.length(); i++) {
                JSONObject obj = crewArray.getJSONObject(i);
                String spec = obj.getString("specialization");
                String name = obj.getString("name");

                // Reconstruct correct subclass via polymorphism
                CrewMember cm;
                switch (spec) {
                    case "Pilot":       cm = new Pilot(name); break;
                    case "Engineer":    cm = new Engineer(name); break;
                    case "Medic":       cm = new Medic(name); break;
                    case "Scientist":   cm = new Scientist(name); break;
                    case "Soldier":     cm = new Soldier(name); break;
                    default:            cm = new Pilot(name); break;
                }

                cm.setEnergy(obj.getInt("energy"));
                cm.setExperience(obj.getInt("experience"));
                cm.setLocation(CrewMember.Location.valueOf(obj.getString("location")));
                cm.setMissionsCompleted(obj.optInt("missionsCompleted", 0));
                cm.setMissionsWon(obj.optInt("missionsWon", 0));
                cm.setTrainingSessions(obj.optInt("trainingSessions", 0));
                cm.setTotalDamageDealt(obj.optInt("totalDamageDealt", 0));
                map.put(cm.getId(), cm);
            }
            storage.loadCrewMap(map);
            Log.d(TAG, "Game loaded successfully. " + map.size() + " crew members.");
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Load failed: " + e.getMessage());
            return false;
        }
    }

    /** Returns true if a save file exists. */
    public static boolean hasSaveFile(Context context) {
        String[] files = context.fileList();
        for (String f : files) {
            if (f.equals(SAVE_FILE)) return true;
        }
        return false;
    }
}