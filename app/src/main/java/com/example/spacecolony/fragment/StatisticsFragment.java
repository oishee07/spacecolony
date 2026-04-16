package com.example.spacecolony.fragment;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.example.spacecolony.R;
import com.example.spacecolony.data.Storage;
import com.example.spacecolony.model.CrewMember;
import java.util.List;

/** Statistics screen: per-crew and colony-wide stats. */
public class StatisticsFragment extends Fragment {
    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        TextView tvColonyStats = view.findViewById(R.id.tv_colony_stats);
        LinearLayout llCrewStats = view.findViewById(R.id.ll_crew_stats);

        Storage s = Storage.getInstance();
        tvColonyStats.setText(
                "Colony: " + s.getColonyName() + "\n" +
                        "Total recruited: " + s.getTotalRecruited() + "\n" +
                        "Crew alive: " + s.getTotalCrewAlive() + "\n" +
                        "Missions launched: " + s.getTotalMissionsLaunched() + "\n" +
                        "Missions completed (won): " + s.getMissionCount()
        );

        for (CrewMember cm : s.listCrewMembers()) {
            TextView tv = new TextView(getContext());
            tv.setPadding(16,16,16,16);
            tv.setTextColor(0xFFFFFFFF);
            tv.setText(
                    cm.getSpecialization() + " " + cm.getName() + "\n" +
                            "  Missions: " + cm.getMissionsCompleted() + "  Wins: " + cm.getMissionsWon() +
                            "  Training sessions: " + cm.getTrainingSessions() + "\n" +
                            "  Total dmg dealt: " + cm.getTotalDamageDealt() + "  XP: " + cm.getExperience()
            );
            llCrewStats.addView(tv);
            View divider = new View(getContext());
            divider.setBackgroundColor(0x33FFFFFF);
            divider.setMinimumHeight(1);
            llCrewStats.addView(divider);
        }
        return view;
    }
}
