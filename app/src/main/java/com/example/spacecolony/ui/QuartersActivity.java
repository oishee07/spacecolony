package com.example.spacecolony.ui;

import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.spacecolony.R;
import com.example.spacecolony.data.Storage;
import com.example.spacecolony.model.CrewMember;
import java.util.ArrayList;
import java.util.List;

public class QuartersActivity extends AppCompatActivity {

    private ListView listView;
    private Button btnToSim, btnToMission;
    private TextView tvResult;
    private List<CrewMember> crewList;
    private List<String> displayList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quarters);

        listView   = findViewById(R.id.list_quarters_crew);
        btnToSim   = findViewById(R.id.btn_quarters_to_sim);
        btnToMission = findViewById(R.id.btn_quarters_to_mission);
        tvResult   = findViewById(R.id.tv_quarters_result);

        loadList();

        // Move to Simulator
        btnToSim.setOnClickListener(v -> moveSelected(CrewMember.Location.SIMULATOR));

        // Move to Mission Control
        btnToMission.setOnClickListener(v -> moveSelected(CrewMember.Location.MISSION_CONTROL));
    }

    private void loadList() {
        crewList = Storage.getInstance().getCrewByLocation(CrewMember.Location.QUARTERS);
        displayList = new ArrayList<>();
        for (CrewMember c : crewList) {
            displayList.add(
                    c.getName()
                            + " | " + c.getSpecialization()
                            + " | Skill: " + c.getEffectiveSkill()
                            + " | Res: " + c.getResilience()
                            + " | Energy: " + c.getEnergy()
                            + " | XP: " + c.getExperience()
            );
        }
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_multiple_choice,
                displayList);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);
    }

    private void moveSelected(CrewMember.Location target) {
        SparseBooleanArray checked = listView.getCheckedItemPositions();
        StringBuilder result = new StringBuilder();
        boolean anySelected = false;

        for (int i = 0; i < crewList.size(); i++) {
            if (checked.get(i)) {
                anySelected = true;
                CrewMember c = crewList.get(i);
                c.setLocation(target);
                String dest = (target == CrewMember.Location.SIMULATOR)
                        ? "Simulator" : "Mission Control";
                result.append(c.getName()).append(" → ").append(dest).append("\n");
            }
        }

        if (!anySelected) {
            tvResult.setText("Select at least one crew member.");
        } else {
            tvResult.setText(result.toString());
            loadList();
        }
    }
}