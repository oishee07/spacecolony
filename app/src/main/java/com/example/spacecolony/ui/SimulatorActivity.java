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

public class SimulatorActivity extends AppCompatActivity {

    private ListView listView;
    private Button btnTrain, btnBackToQuarters;
    private TextView tvResult;
    private List<CrewMember> crewList;
    private List<String> displayList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulator);

        listView         = findViewById(R.id.list_simulator_crew);
        btnTrain         = findViewById(R.id.btn_train_selected);
        btnBackToQuarters = findViewById(R.id.btn_sim_to_quarters);
        tvResult         = findViewById(R.id.tv_simulator_result);

        loadList();

        // Train button
        btnTrain.setOnClickListener(v -> {
            SparseBooleanArray checked = listView.getCheckedItemPositions();
            StringBuilder result = new StringBuilder();
            boolean anySelected = false;

            for (int i = 0; i < crewList.size(); i++) {
                if (checked.get(i)) {
                    anySelected = true;
                    CrewMember c = crewList.get(i);
                    c.train(); // +1 XP
                    result.append(c.getName())
                            .append(" trained! XP: ")
                            .append(c.getExperience())
                            .append("\n");
                }
            }

            if (!anySelected) {
                tvResult.setText("Select crew to train.");
            } else {
                tvResult.setText(result.toString());
                loadList();
            }
        });

        // Back to Quarters button
        btnBackToQuarters.setOnClickListener(v -> {
            SparseBooleanArray checked = listView.getCheckedItemPositions();
            StringBuilder result = new StringBuilder();
            boolean anySelected = false;

            for (int i = 0; i < crewList.size(); i++) {
                if (checked.get(i)) {
                    anySelected = true;
                    CrewMember c = crewList.get(i);
                    c.setLocation(CrewMember.Location.QUARTERS);
                    c.restoreEnergy(); // Full energy restore
                    result.append(c.getName())
                            .append(" returned to Quarters (Energy Restored)\n");
                }
            }

            if (!anySelected) {
                tvResult.setText("Select crew to move.");
            } else {
                tvResult.setText(result.toString());
                loadList();
            }
        });
    }

    private void loadList() {
        crewList = Storage.getInstance().getCrewByLocation(CrewMember.Location.SIMULATOR);
        displayList = new ArrayList<>();
        for (CrewMember c : crewList) {
            displayList.add(
                    c.getName()
                            + " | " + c.getSpecialization()
                            + " | Skill: " + c.getEffectiveSkill()
                            + " | Res: " + c.getResilience()
                            + " | Energy: " + c.getEnergy()
            );
        }
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_multiple_choice,
                displayList);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);
    }
}