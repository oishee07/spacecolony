package com.example.spacecolony.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spacecolony.R;
import com.example.spacecolony.ui.adapters.CrewAdapter;
import com.example.spacecolony.data.Storage;
import com.example.spacecolony.model.CrewMember;

import java.util.List;

/**
 * Fragment showing all crew members currently in Quarters.
 * Allows moving them to Simulator or Mission Control.
 */
public class QuartersFragment extends Fragment {

    private RecyclerView recyclerView;
    private CrewAdapter adapter;
    private Button btnToSimulator, btnToMission;
    private TextView tvEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quarters, container, false);

        recyclerView   = view.findViewById(R.id.rv_quarters_crew);
        btnToSimulator = view.findViewById(R.id.btn_to_simulator);
        btnToMission   = view.findViewById(R.id.btn_to_mission);
        tvEmpty        = view.findViewById(R.id.tv_empty);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadData();

        btnToSimulator.setOnClickListener(v -> moveSelected(CrewMember.Location.SIMULATOR));
        btnToMission.setOnClickListener(v -> moveSelected(CrewMember.Location.MISSION_CONTROL));

        return view;
    }

    private void loadData() {
        List<CrewMember> quartersCreW = Storage.getInstance()
                .getCrewByLocation(CrewMember.Location.QUARTERS);

        if (quartersCreW.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        adapter = new CrewAdapter(getContext(), quartersCreW, true, 0, null);
        recyclerView.setAdapter(adapter);
    }

    private void moveSelected(CrewMember.Location target) {
        List<Integer> selectedIds = adapter.getSelectedIds();
        if (selectedIds.isEmpty()) {
            Toast.makeText(getContext(), "Select at least one crew member", Toast.LENGTH_SHORT).show();
            return;
        }
        for (int id : selectedIds) {
            CrewMember cm = Storage.getInstance().getCrewMember(id);
            if (cm != null) cm.setLocation(target);
        }
        String dest = (target == CrewMember.Location.SIMULATOR) ? "Simulator" : "Mission Control";
        Toast.makeText(getContext(), selectedIds.size() + " crew moved to " + dest, Toast.LENGTH_SHORT).show();
        loadData(); // Refresh list
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(); // Refresh when returning to this fragment
    }
}
