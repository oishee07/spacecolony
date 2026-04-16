package com.example.spacecolony.ui;

import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.spacecolony.R;
import com.example.spacecolony.data.Storage;
import com.example.spacecolony.model.CrewMember;
import java.util.ArrayList;
import java.util.List;

public class MedbayActivity extends AppCompatActivity {

    private ListView listCrew;
    private Button btnDischarge;
    private TextView tvResult;
    private List<CrewMember> crewList;
    private ArrayAdapter<String> adapter;
    private List<String> displayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medbay);

        listCrew    = findViewById(R.id.list_medbay_crew);
        btnDischarge = findViewById(R.id.btn_discharge_medbay);
        tvResult    = findViewById(R.id.tv_medbay_result);

        loadList();

        btnDischarge.setOnClickListener(v -> {
            SparseBooleanArray checked = listCrew.getCheckedItemPositions();
            StringBuilder result = new StringBuilder();

            for (int i = 0; i < crewList.size(); i++) {
                if (checked.get(i)) {
                    CrewMember c = crewList.get(i);
                    c.setLocation(CrewMember.Location.QUARTERS);
                    c.restoreEnergy();
                    result.append(c.getName()).append(" discharged to Quarters\n");
                }
            }

            if (result.length() == 0) {
                tvResult.setText("Select crew to discharge.");
            } else {
                tvResult.setText(result.toString());
                loadList();
            }
        });
    }

    private void loadList() {
        crewList = Storage.getInstance().getCrewByLocation(CrewMember.Location.MEDBAY);
        displayList = new java.util.ArrayList<>();
        for (CrewMember c : crewList) {
            displayList.add(
                    c.getName() + " [" + c.getSpecialization() + "]"
                            + " | HP: " + c.getEnergy()
                            + " | Skill: " + c.getEffectiveSkill()
                            + " | XP: " + c.getExperience()
                            + " ← recovering"
            );
        }
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_multiple_choice,
                displayList);
        listCrew.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listCrew.setAdapter(adapter);
    }
}