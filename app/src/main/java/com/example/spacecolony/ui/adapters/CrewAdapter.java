package com.example.spacecolony.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spacecolony.R;
import com.example.spacecolony.model.CrewMember;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for displaying crew members with stats, images, and checkboxes.
 * Supports single and multi-select modes.
 */
public class CrewAdapter extends RecyclerView.Adapter<CrewAdapter.CrewViewHolder> {

    public interface OnCrewClickListener {
        void onCrewClick(CrewMember crew, int position);
        void onCheckChanged(CrewMember crew, boolean isChecked);
    }

    private List<CrewMember> crewList;
    private List<Integer> selectedIds = new ArrayList<>();
    private boolean showCheckboxes;
    private int maxSelectable; // 0 = unlimited
    private OnCrewClickListener listener;
    private Context context;

    public CrewAdapter(Context context, List<CrewMember> crewList,
                       boolean showCheckboxes, int maxSelectable,
                       OnCrewClickListener listener) {
        this.context = context;
        this.crewList = new ArrayList<>(crewList);
        this.showCheckboxes = showCheckboxes;
        this.maxSelectable = maxSelectable;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CrewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_crew_member, parent, false);
        return new CrewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CrewViewHolder holder, int position) {
        CrewMember crew = crewList.get(position);

        holder.tvName.setText(crew.getName());
        holder.tvSpec.setText(crew.getSpecialization());
        holder.tvStats.setText("SKL:" + crew.getEffectiveSkill()
                + "  RES:" + crew.getResilience()
                + "  XP:" + crew.getExperience());
        holder.tvEnergy.setText(crew.getEnergy() + "/" + crew.getMaxEnergy());
        holder.pbEnergy.setMax(crew.getMaxEnergy());
        holder.pbEnergy.setProgress(crew.getEnergy());

        // ── Specialization image ─────────────────────────────────────────
        holder.ivSpec.setImageResource(getSpecImage(crew.getSpecialization()));

        // ── Specialization color accent ──────────────────────────────────
        holder.tvSpec.setTextColor(getSpecColor(context, crew.getSpecialization()));

        // ── Checkbox ─────────────────────────────────────────────────────
        if (showCheckboxes) {
            holder.checkbox.setVisibility(View.VISIBLE);
            holder.checkbox.setOnCheckedChangeListener(null); // avoid spurious callbacks
            holder.checkbox.setChecked(selectedIds.contains(crew.getId()));
            holder.checkbox.setOnCheckedChangeListener((btn, isChecked) -> {
                if (isChecked) {
                    if (maxSelectable > 0 && selectedIds.size() >= maxSelectable) {
                        btn.setChecked(false);
                        return;
                    }
                    selectedIds.add(crew.getId());
                } else {
                    selectedIds.remove(Integer.valueOf(crew.getId()));
                }
                if (listener != null) listener.onCheckChanged(crew, isChecked);
            });
        } else {
            holder.checkbox.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onCrewClick(crew, position);
        });
    }

    @Override
    public int getItemCount() {
        return crewList.size();
    }

    /** Update the displayed list. */
    public void updateList(List<CrewMember> newList) {
        crewList = new ArrayList<>(newList);
        selectedIds.clear();
        notifyDataSetChanged();
    }

    /** Returns IDs of all checked crew members. */
    public List<Integer> getSelectedIds() {
        return new ArrayList<>(selectedIds);
    }

    /** Clear selection. */
    public void clearSelection() {
        selectedIds.clear();
        notifyDataSetChanged();
    }

    // ── Helper: specialization image ──────────────────────────────────────
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

    // ── Helper: specialization color ──────────────────────────────────────
    private int getSpecColor(Context ctx, String spec) {
        switch (spec) {
            case "Pilot":       return ctx.getResources().getColor(R.color.pilot_color, null);
            case "Engineer":    return ctx.getResources().getColor(R.color.engineer_color, null);
            case "Medic":       return ctx.getResources().getColor(R.color.medic_color, null);
            case "Scientist":   return ctx.getResources().getColor(R.color.scientist_color, null);
            case "Soldier":     return ctx.getResources().getColor(R.color.soldier_color, null);
            default:            return ctx.getResources().getColor(R.color.pilot_color, null);
        }
    }

    // ── ViewHolder ─────────────────────────────────────────────────────────
    static class CrewViewHolder extends RecyclerView.ViewHolder {
        ImageView ivSpec;
        TextView tvName, tvSpec, tvStats, tvEnergy;
        ProgressBar pbEnergy;
        CheckBox checkbox;

        CrewViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSpec    = itemView.findViewById(R.id.iv_crew_spec);
            tvName    = itemView.findViewById(R.id.tv_crew_name);
            tvSpec    = itemView.findViewById(R.id.tv_crew_spec);
            tvStats   = itemView.findViewById(R.id.tv_crew_stats);
            tvEnergy  = itemView.findViewById(R.id.tv_crew_energy);
            pbEnergy  = itemView.findViewById(R.id.pb_energy);
            checkbox  = itemView.findViewById(R.id.cb_select);
        }
    }
}