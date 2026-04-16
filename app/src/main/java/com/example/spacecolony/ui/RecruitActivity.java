package com.example.spacecolony.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spacecolony.R;
import com.example.spacecolony.data.Storage;
import com.example.spacecolony.model.CrewMember;
import com.example.spacecolony.model.Engineer;
import com.example.spacecolony.model.Medic;
import com.example.spacecolony.model.Pilot;
import com.example.spacecolony.model.Scientist;
import com.example.spacecolony.model.Soldier;

/**
 * Screen for recruiting a new crew member.
 * Shows name input, specialization radio buttons with stats preview.
 */
public class RecruitActivity extends AppCompatActivity {

    private EditText etName;
    private RadioGroup rgSpec;
    private TextView tvStatPreview;
    private Button btnCreate, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruit);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Recruit Crew Member");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etName       = findViewById(R.id.et_name);
        rgSpec       = findViewById(R.id.rg_specialization);
        tvStatPreview = findViewById(R.id.tv_stat_preview);
        btnCreate    = findViewById(R.id.btn_create);
        btnCancel    = findViewById(R.id.btn_cancel);

        // Show stats when a specialization is selected
        rgSpec.setOnCheckedChangeListener((group, checkedId) -> updateStatPreview(checkedId));

        // Select Pilot by default and show its stats
        ((RadioButton) findViewById(R.id.rb_pilot)).setChecked(true);
        updateStatPreview(R.id.rb_pilot);

        btnCreate.setOnClickListener(v -> createCrew());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void updateStatPreview(int checkedId) {
        String spec, stats;
        if (checkedId == R.id.rb_pilot) {
            spec = "Pilot"; stats = "Skill: 5 | Resilience: 4 | Energy: 20\nSpecial: Evasive Maneuver\nBonus: +3 on Asteroid missions";
        } else if (checkedId == R.id.rb_engineer) {
            spec = "Engineer"; stats = "Skill: 6 | Resilience: 3 | Energy: 19\nSpecial: System Overload\nBonus: +2 on Repair/Fuel missions";
        } else if (checkedId == R.id.rb_medic) {
            spec = "Medic"; stats = "Skill: 7 | Resilience: 2 | Energy: 18\nSpecial: Field Medic (Heal)\nBonus: +3 on Disease missions";
        } else if (checkedId == R.id.rb_scientist) {
            spec = "Scientist"; stats = "Skill: 8 | Resilience: 1 | Energy: 17\nSpecial: Critical Analysis\nBonus: +2 on Solar/Heating missions";
        } else {
            spec = "Soldier"; stats = "Skill: 9 | Resilience: 0 | Energy: 16\nSpecial: Berserker Charge\nBonus: +3 on Alien/Fire missions";
        }
        tvStatPreview.setText(spec + "\n" + stats);
    }

    private void createCrew() {
        String name = etName.getText().toString().trim();
        if (name.isEmpty()) {
            etName.setError("Name is required");
            return;
        }

        int checkedId = rgSpec.getCheckedRadioButtonId();
        CrewMember newMember;

        if (checkedId == R.id.rb_pilot)          newMember = new Pilot(name);
        else if (checkedId == R.id.rb_engineer)  newMember = new Engineer(name);
        else if (checkedId == R.id.rb_medic)     newMember = new Medic(name);
        else if (checkedId == R.id.rb_scientist) newMember = new Scientist(name);
        else                                      newMember = new Soldier(name);

        Storage.getInstance().addCrewMember(newMember);
        Toast.makeText(this,
                newMember.getSpecialization() + " " + name + " recruited!",
                Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}