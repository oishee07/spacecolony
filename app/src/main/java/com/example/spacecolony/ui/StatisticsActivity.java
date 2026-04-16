package com.example.spacecolony.ui;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.spacecolony.R;
import com.example.spacecolony.data.Storage;
import com.example.spacecolony.model.CrewMember;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        TextView tvStats = findViewById(R.id.tv_statistics);
        tvStats.setText(buildStats());
    }

    private String buildStats() {
        Storage s = Storage.getInstance();
        List<CrewMember> crewList = s.listCrewMembers();

        int totalMissions  = 0;
        int totalWins      = 0;
        int totalLosses    = 0;
        int totalTrainings = 0;

        StringBuilder sb = new StringBuilder();
        sb.append("=== CREW STATISTICS ===\n\n");

        for (CrewMember c : crewList) {
            sb.append("👤 ").append(c.getName()).append("\n");
            sb.append("  Role: ").append(c.getSpecialization()).append("\n");
            sb.append("  Missions: ").append(c.getMissionsCompleted()).append("\n");
            sb.append("  Wins: ").append(c.getMissionsWon()).append("\n");
            sb.append("  Losses: ")
                    .append(c.getMissionsCompleted() - c.getMissionsWon()).append("\n");
            sb.append("  Trainings: ").append(c.getTrainingSessions()).append("\n");
            sb.append("  Total Damage: ").append(c.getTotalDamageDealt()).append("\n");
            sb.append("  XP: ").append(c.getExperience()).append("\n");
            sb.append("--------------------------\n");

            totalMissions  += c.getMissionsCompleted();
            totalWins      += c.getMissionsWon();
            totalLosses    += (c.getMissionsCompleted() - c.getMissionsWon());
            totalTrainings += c.getTrainingSessions();
        }

        sb.append("\n=== COLONY SUMMARY ===\n");
        sb.append("Total Crew: ").append(crewList.size()).append("\n");
        sb.append("Total Missions: ").append(totalMissions).append("\n");
        sb.append("Total Wins: ").append(totalWins).append("\n");
        sb.append("Total Losses: ").append(totalLosses).append("\n");
        sb.append("Total Trainings: ").append(totalTrainings).append("\n");
        sb.append("Missions Launched: ").append(s.getTotalMissionsLaunched()).append("\n");

        return sb.toString();
    }
}