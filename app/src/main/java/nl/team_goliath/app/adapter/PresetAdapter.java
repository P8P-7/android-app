package nl.team_goliath.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;
import nl.team_goliath.app.R;
import nl.team_goliath.app.model.Preset;

public class PresetAdapter extends RecyclerView.Adapter<PresetAdapter.PresetViewHolder> {
    private List<Preset> presets;
    private Context context;

    public PresetAdapter(Context context) {
        this.presets = new ArrayList<>();
        presets.add(new Preset(context.getResources().getString(R.string.preset_entering)));
        presets.add(new Preset(context.getResources().getString(R.string.preset_dance)));
        presets.add(new Preset(context.getResources().getString(R.string.preset_line_dance)));
        presets.add(new Preset(context.getResources().getString(R.string.preset_obstacle_course)));
        presets.add(new Preset(context.getResources().getString(R.string.preset_wunderhorn)));
        presets.add(new Preset(context.getResources().getString(R.string.preset_transport_rebuild)));

        presets.get(3).setActive(true);

        this.notifyDataSetChanged();

        this.context = context;
    }

    @Override
    public PresetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.preset_item, parent, false);
        return new PresetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PresetViewHolder holder, int position) {
        Preset preset = presets.get(position);

        holder.textView.setText(preset.getTitle());
        holder.presetSwitch.setChecked(preset.isActive());
    }

    @Override
    public int getItemCount() {
        return presets.size();
    }

    class PresetViewHolder extends RecyclerView.ViewHolder {
        protected TextView textView;
        protected SwitchCompat presetSwitch;

        PresetViewHolder(View view) {
            super(view);
            this.textView = view.findViewById(R.id.presetName);
            this.presetSwitch = view.findViewById(R.id.presetSwitch);
        }

    }
}
