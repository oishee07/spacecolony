package com.example.spacecolony.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spacecolony.R;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for the scrollable mission log display.
 * Color-codes different log line types for better readability.
 */
public class MissionLogAdapter extends RecyclerView.Adapter<MissionLogAdapter.LogViewHolder> {

    private List<String> logLines;
    private Context context;

    public MissionLogAdapter(Context context) {
        this.context = context;
        this.logLines = new ArrayList<>();
    }

    /** Append a new line to the log. */
    public void addLine(String line) {
        logLines.add(line);
        notifyItemInserted(logLines.size() - 1);
    }

    /** Add multiple lines. */
    public void addLines(List<String> lines) {
        int start = logLines.size();
        logLines.addAll(lines);
        notifyItemRangeInserted(start, lines.size());
    }

    public void clearLog() {
        logLines.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_log_line, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        String line = logLines.get(position);
        holder.tvLine.setText(line);
        // Color-code lines
        holder.tvLine.setTextColor(getLineColor(line));
    }

    @Override
    public int getItemCount() { return logLines.size(); }

    private int getLineColor(String line) {
        if (line.startsWith("===")) return Color.parseColor("#FFD700"); // Gold for headers
        if (line.startsWith("---")) return Color.parseColor("#AAAAAA"); // Gray for rounds
        if (line.contains("neutralized") || line.contains("COMPLETE"))
            return Color.parseColor("#00FF88");  // Green for victory
        if (line.contains("failed") || line.contains("incapacitated"))
            return Color.parseColor("#FF4444");  // Red for failure/damage
        if (line.contains("gains") || line.contains("XP"))
            return Color.parseColor("#66CCFF");  // Blue for XP gain
        if (line.contains("Damage dealt")) return Color.parseColor("#FF8C00"); // Orange for damage
        return Color.WHITE; // Default
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView tvLine;
        LogViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLine = itemView.findViewById(R.id.tv_log_line);
        }
    }
}
