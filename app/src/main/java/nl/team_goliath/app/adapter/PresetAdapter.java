package nl.team_goliath.app.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.Objects;

import androidx.databinding.DataBindingUtil;
import nl.team_goliath.app.R;
import nl.team_goliath.app.databinding.PresetItemBinding;
import nl.team_goliath.app.model.Preset;
import nl.team_goliath.app.ui.common.DataBoundListAdapter;

public class PresetAdapter extends DataBoundListAdapter<Preset, PresetItemBinding> {

    @Override
    protected PresetItemBinding createBinding(ViewGroup parent) {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.preset_item, parent, false);
    }

    @Override
    protected void bind(PresetItemBinding binding, Preset item) {
        binding.setPreset(item);
    }

    @Override
    protected boolean areItemsTheSame(Preset oldItem, Preset newItem) {
        return Objects.equals(oldItem, newItem);
    }

    @Override
    protected boolean areContentsTheSame(Preset oldItem, Preset newItem) {
        return Objects.equals(oldItem, newItem);
    }
}
