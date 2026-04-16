package com.example.spacecolony.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spacecolony.R;
import com.example.spacecolony.ui.adapters.MissionLogAdapter;
import com.example.spacecolony.data.DataManager;
import com.example.spacecolony.data.Storage;
import com.example.spacecolony.mission.MissionControl;
import com.example.spacecolony.model.CrewMember;
import com.example.spacecolony.model.Threat;

import java.util.ArrayList;
import java.util.List;

/**
 * Full-screen mission activity.
 * Shows energy bars, threat info, and tactical action buttons.
 * Tactical Combat bonus: player chooses Attack / Defend / Special each turn.
 */
public class MissionActivity extends AppCompatActivity {

    // Intent extras key
    public static final String EXTRA_CREW_IDS = "crew_ids";

    // ── Mission state ─────────────────────────────────────────────────────
    private MissionControl missionControl;
    private MissionLogAdapter logAdapter;

    // ── Views ─────────────────────────────────────────────────────────────
    private TextView tvMissionTitle, tvThreatName, tvThreatEnergy, tvCurrentActor;
    private ProgressBar pbThreatEnergy;
    // Dynamic crew bars (up to 3)
    private TextView[] tvCrewName = new TextView[3];
    private TextView[] tvCrewEnergy = new TextView[3];
    private ProgressBar[] pbCrewEnergy = new ProgressBar[3];
    private ImageView[] ivCrewSpec = new ImageView[3];
    private View[] crewCard = new View[3];

    private Button btnAttack, btnDefend, btnSpecial, btnClose;
    private RecyclerView rvLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);

        // ── Wire views ────────────────────────────────────────────────────
        tvMissionTitle  = findViewById(R.id.tv_mission_title);
        tvThreatName    = findViewById(R.id.tv_threat_name);
        tvThreatEnergy  = findViewById(R.id.tv_threat_energy);
        pbThreatEnergy  = findViewById(R.id.pb_threat_energy);
        tvCurrentActor  = findViewById(R.id.tv_current_actor);
        btnAttack       = findViewById(R.id.btn_attack);
        btnDefend       = findViewById(R.id.btn_defend);
        btnSpecial      = findViewById(R.id.btn_special);
        btnClose        = findViewById(R.id.btn_close_mission);
        rvLog           = findViewById(R.id.rv_mission_log);

        // Crew card arrays
        tvCrewName[0]    = findViewById(R.id.tv_crew1_name);
        tvCrewName[1]    = findViewById(R.id.tv_crew2_name);
        tvCrewName[2]    = findViewById(R.id.tv_crew3_name);
        tvCrewEnergy[0]  = findViewById(R.id.tv_crew1_energy);
        tvCrewEnergy[1]  = findViewById(R.id.tv_crew2_energy);
        tvCrewEnergy[2]  = findViewById(R.id.tv_crew3_energy);
        pbCrewEnergy[0]  = findViewById(R.id.pb_crew1_energy);
        pbCrewEnergy[1]  = findViewById(R.id.pb_crew2_energy);
        pbCrewEnergy[2]  = findViewById(R.id.pb_crew3_energy);
        ivCrewSpec[0]    = findViewById(R.id.iv_crew1_spec);
        ivCrewSpec[1]    = findViewById(R.id.iv_crew2_spec);
        ivCrewSpec[2]    = findViewById(R.id.iv_crew3_spec);
        crewCard[0]      = findViewById(R.id.card_crew1);
        crewCard[1]      = findViewById(R.id.card_crew2);
        crewCard[2]      = findViewById(R.id.card_crew3);

        // ── Mission log RecyclerView ───────────────────────────────────────
        logAdapter = new MissionLogAdapter(this);
        rvLog.setLayoutManager(new LinearLayoutManager(this));
        rvLog.setAdapter(logAdapter);

        // ── Build squad from intent ───────────────────────────────────────
        ArrayList<Integer> ids = getIntent().getIntegerArrayListExtra(EXTRA_CREW_IDS);
        List<CrewMember> squad = new ArrayList<>();
        if (ids != null) {
            for (int id : ids) {
                CrewMember cm = Storage.getInstance().getCrewMember(id);
                if (cm != null) squad.add(cm);
            }
        }

        if (squad.size() < 2) {
            Toast.makeText(this, "Need at least 2 crew members!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // ── Start mission ─────────────────────────────────────────────────
        missionControl = new MissionControl(squad);
        Threat threat = missionControl.getThreat();

        // Mission title
        tvMissionTitle.setText("MISSION: " + threat.getMissionType().getDisplayName());

        // Threat bar
        pbThreatEnergy.setMax(threat.getMaxEnergy());
        updateThreatUI();

        // Crew bars (hide unused cards)
        for (int i = 0; i < 3; i++) {
            if (i < squad.size()) {
                crewCard[i].setVisibility(View.VISIBLE);
                CrewMember cm = squad.get(i);
                tvCrewName[i].setText(cm.getSpecialization() + "\n" + cm.getName());
                pbCrewEnergy[i].setMax(cm.getMaxEnergy());
                ivCrewSpec[i].setImageResource(getSpecImage(cm.getSpecialization()));
                updateCrewCard(i, cm);
            } else {
                crewCard[i].setVisibility(View.GONE);
            }
        }

        // Add opening log
        logAdapter.addLines(missionControl.getMissionLog());
        scrollLogToBottom();

        updateCurrentActorUI();

        // ── Action buttons ────────────────────────────────────────────────
        btnAttack.setOnClickListener(v -> executeAction(MissionControl.ActionType.ATTACK));
        btnDefend.setOnClickListener(v -> executeAction(MissionControl.ActionType.DEFEND));
        btnSpecial.setOnClickListener(v -> executeAction(MissionControl.ActionType.SPECIAL));
        btnClose.setOnClickListener(v -> {
            DataManager.saveGame(this);
            finish();
        });
        btnClose.setVisibility(View.GONE);

        // Update special button label
        updateSpecialButton();
    }

    private void executeAction(MissionControl.ActionType action) {
        if (missionControl.isMissionOver()) return;

        List<String> stepLog = missionControl.executeTurn(action);
        logAdapter.addLines(stepLog);
        scrollLogToBottom();

        // Update all UI
        updateThreatUI();
        List<CrewMember> squad = missionControl.getSquad();
        // Re-map squad to cards
        for (int i = 0; i < 3; i++) {
            if (i < squad.size()) {
                crewCard[i].setVisibility(View.VISIBLE);
                updateCrewCard(i, squad.get(i));
            }
        }

        if (missionControl.isMissionOver()) {
            // Disable action buttons
            btnAttack.setEnabled(false);
            btnDefend.setEnabled(false);
            btnSpecial.setEnabled(false);
            btnClose.setVisibility(View.VISIBLE);

            if (missionControl.isPlayerVictory()) {
                tvMissionTitle.setText("✅ MISSION COMPLETE!");
                tvMissionTitle.setTextColor(Color.parseColor("#00FF88"));
            } else {
                tvMissionTitle.setText("❌ MISSION FAILED");
                tvMissionTitle.setTextColor(Color.parseColor("#FF4444"));
            }
        } else {
            updateCurrentActorUI();
            updateSpecialButton();
        }
    }

    private void updateThreatUI() {
        Threat threat = missionControl.getThreat();
        tvThreatName.setText(threat.getName());
        tvThreatEnergy.setText(threat.getEnergy() + "/" + threat.getMaxEnergy());
        pbThreatEnergy.setProgress(threat.getEnergy());
    }

    private void updateCrewCard(int index, CrewMember cm) {
        tvCrewName[index].setText(cm.getSpecialization() + "\n" + cm.getName());
        tvCrewEnergy[index].setText(cm.getEnergy() + "/" + cm.getMaxEnergy());
        pbCrewEnergy[index].setProgress(cm.getEnergy());
    }

    private void updateCurrentActorUI() {
        CrewMember actor = missionControl.getCurrentMember();
        if (actor != null) {
            tvCurrentActor.setText("▶ " + actor.getName() + "'s turn");
        }
    }

    private void updateSpecialButton() {
        CrewMember actor = missionControl.getCurrentMember();
        if (actor != null) {
            btnSpecial.setText(actor.specialAbilityName());
        }
    }

    private void scrollLogToBottom() {
        rvLog.post(() -> rvLog.scrollToPosition(logAdapter.getItemCount() - 1));
    }

    private int getSpecImage(String spec) {
        switch (spec) {
            case "Pilot":       return R.drawable.ic_pilot;
            case "Engineer":    return R.drawable.ic_engineer;
            case "Medic":       return R.drawable.ic_medic;
            case "Scientist":   return R.drawable.ic_scientist;
            case "Soldier":     return R.drawable.ic_soldier;
            default:            return R.drawable.ic_pilot;
        }
    }
}