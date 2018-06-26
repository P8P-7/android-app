package nl.team_goliath.app.ui;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.View;

import com.google.protobuf.Message;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import nl.team_goliath.app.R;
import nl.team_goliath.app.formatter.ConfigRepositoryFormatter;
import nl.team_goliath.app.model.CommandSender;
import nl.team_goliath.app.model.Status;
import nl.team_goliath.app.proto.CommandMessageProto.CommandMessage;
import nl.team_goliath.app.proto.SetWingPositionCommandProto.SetWingPositionCommand;
import nl.team_goliath.app.proto.SetWingPositionCommandProto.SetWingPositionCommand.Position;
import nl.team_goliath.app.proto.ZmqConfigRepositoryProto.ConfigRepository;
import nl.team_goliath.app.viewmodel.RepositoryViewModel;

/**
 * Main UI for the preferences screen.
 */
public class PreferenceFragment extends PreferenceFragmentCompat {
    private PreferenceScreen goliathConfig;

    private final CommandSender moveCallback = (commandMessage) -> {
        if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            ((CommandSender) getActivity()).sendCommand(commandMessage);
        }
    };


    static PreferenceFragment newInstance() {
        return new PreferenceFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set the default white background in the view so as to avoid transparency
        view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.background_material_light));
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.preferences, rootKey);

        // Bind the summaries of EditText/List preferences
        // to their values. When their values change, their summaries are
        // updated to reflect the new value, per the Android Design
        // guidelines.

        if (findPreference("address") != null) {
            bindPreferenceSummaryToValue(findPreference("address"));
            bindPreferenceSummaryToValue(findPreference("sub_port"));
            bindPreferenceSummaryToValue(findPreference("pub_port"));
        }

        goliathConfig = (PreferenceScreen) findPreference("goliath_config");

        RepositoryViewModel repositoryViewModel = ViewModelProviders.of(getActivity()).get(RepositoryViewModel.class);

        findPreference("calibrate_wings").setOnPreferenceClickListener(preference -> {
            moveCallback.sendCommand(CommandMessage.newBuilder()
                    .setSetWingPositionCommand(SetWingPositionCommand.newBuilder()
                            .setWingPosition(Position.DEFAULT)
                            .build())
                    .build());

            return true;
        });

        initList(repositoryViewModel);
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = (preference, value) -> {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list.
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);
            // Set the summary to reflect the new value.
            preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);

        } else {
            // For all other preferences, set the summary to the value's
            // simple string representation.
            preference.setSummary(stringValue);
        }
        return true;
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    private void initList(RepositoryViewModel viewModel) {
        TypedValue themeTypedValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.preferenceTheme, themeTypedValue, true);
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(getActivity(), themeTypedValue.resourceId);

        ConfigRepositoryFormatter configRepositoryFormatter = new ConfigRepositoryFormatter(contextThemeWrapper, goliathConfig);

        viewModel.getMessages().observe(this, resource -> {
            if (resource.status == Status.SUCCESS && !resource.data.isEmpty() && goliathConfig != null) {
                for (Message message : resource.data) {
                    if (message instanceof ConfigRepository) {
                        configRepositoryFormatter.parseToPreferences((ConfigRepository) message);
                        break;
                    }
                }
            }
        });
    }
}