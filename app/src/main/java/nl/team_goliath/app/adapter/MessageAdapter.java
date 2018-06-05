package nl.team_goliath.app.adapter;

import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.Objects;

import androidx.databinding.DataBindingUtil;
import nl.team_goliath.app.R;
import nl.team_goliath.app.databinding.MessageItemBinding;
import nl.team_goliath.app.ui.common.DataBoundListAdapter;

public class MessageAdapter extends DataBoundListAdapter<SpannableStringBuilder, MessageItemBinding> {

    @Override
    protected MessageItemBinding createBinding(ViewGroup parent) {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.message_item, parent, false);
    }

    @Override
    protected void bind(MessageItemBinding binding, SpannableStringBuilder item) {
        binding.setMessage(item);
    }

    @Override
    protected boolean areItemsTheSame(SpannableStringBuilder oldItem, SpannableStringBuilder newItem) {
        return Objects.equals(oldItem, newItem);
    }

    @Override
    protected boolean areContentsTheSame(SpannableStringBuilder oldItem, SpannableStringBuilder newItem) {
        return Objects.equals(oldItem, newItem);
    }
}
