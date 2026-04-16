package com.example.spacecolony.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spacecolony.R;
import com.example.spacecolony.ui.MissionActivity;
import com.example.spacecolony.ui.adapters.CrewAdapter;
import com.example.spacecolony.data.Storage;
import com.example.spacecolony.model.CrewMember;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for launching missions.
 * Supports squads of 2 or 3 (Larger Squads bonus).
 * Crew members must be in Mission Control to be selectable.
 */
public class MissionControlFragment extends Fragment {

    private RecyclerView recyclerView;
    private CrewAdapter adapter;
    private Button btnLaunch, btnToQuarters;
    private RadioGroup rgSquadSize;
    private TextView tvEmpty, tvMissionCount;
    private int maxSquadSize = 2; // default

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mission_control, container, false);

        recyclerView   = view.findViewById(R.id.rv_mission_crew);
        btnLaunch      = view.findViewById(R.id.btn_launch_mission);
        btnToQuarters  = view.findViewById(R.id.btn_mc_to_quarters);
        rgSquadSize    = view.findViewById(R.id.rg_squad_size);
        tvEmpty        = view.findViewById(R.id.tv_empty_mc);
        tvMissionCount = view.findViewById(R.id.tv_mission_count);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Squad size toggle (2 or 3 — Larger Squads bonus)
        rgSquadSize.setOnCheckedChangeListener((group, checkedId) -> {
            maxSquadSize = (checkedId == R.id.rb_squad3) ? 3 : 2;
            if (adapter != null) {
                adapter.clearSelection();
            }
        });

        loadData();

        btnLaunch.setOnClickListener(v -> launchMission());
        btnToQuarters.setOnClickListener(v -> returnSelected());

        return view;
    }

    private void loadData() {
        Storage storage = Storage.getInstance();
        List<CrewMember> missionCrew = storage.getCrewByLocation(CrewMember.Location.MISSION_CONTROL);

        tvEmpty.setVisibility(missionCrew.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(missionCrew.isEmpty() ? View.GONE : View.VISIBLE);

        tvMissionCount.setText("Missions completed: " + storage.getMissionCount()
                + "  |  Launched: " + storage.getTotalMissionsLaunched());

        adapter = new CrewAdapter(getContext(), missionCrew, true, maxSquadSize, null);
        recyclerView.setAdapter(adapter);
    }

    private void launchMission() {
        List<Integer> selectedIds = adapter.getSelectedIds();

        // Check minimum selection based on chosen squad size
        if (selectedIds.size() < maxSquadSize) {
            if (maxSquadSize == 3) {
                Toast.makeText(getContext(),
                        "Select exactly 3 crew members for a squad of 3",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(),
                        "Select at least 2 crew members",
                        Toast.LENGTH_SHORT).show();
            }
            return;
        }

        // Check they haven't selected too many
        if (selectedIds.size() > maxSquadSize) {
            Toast.makeText(getContext(),
                    "You selected too many! Max squad size is " + maxSquadSize,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // All good - launch mission
        Intent intent = new Intent(getContext(), MissionActivity.class);
        intent.putIntegerArrayListExtra(MissionActivity.EXTRA_CREW_IDS,
                new ArrayList<>(selectedIds));
        startActivity(intent);
    }

    private void returnSelected() {
        List<Integer> selectedIds = adapter.getSelectedIds();
        if (selectedIds.isEmpty()) {
            Toast.makeText(getContext(), "Select crew to send home", Toast.LENGTH_SHORT).show();
            return;
        }
        for (int id : selectedIds) {
            CrewMember cm = Storage.getInstance().getCrewMember(id);
            if (cm != null) {
                cm.setLocation(CrewMember.Location.QUARTERS);
                cm.restoreEnergy();
            }
        }
        Toast.makeText(getContext(), "Crew returned to Quarters (energy restored)", Toast.LENGTH_SHORT).show();
        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }
}
