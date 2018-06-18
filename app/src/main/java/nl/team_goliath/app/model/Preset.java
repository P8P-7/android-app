package nl.team_goliath.app.model;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.MutableLiveData;

public class Preset {
    private final int id;

    @NonNull
    private final String title;

    private boolean active;

    @NonNull
    private MutableLiveData<Boolean> commandActive;

    public Preset(int id, @NonNull String title, boolean active) {
        this.id = id;
        this.title = title;
        this.active = active;
        this.commandActive = new MutableLiveData<>();
        this.commandActive.setValue(active);
    }

    public Preset(int id, @NonNull String title) {
        this(id, title, false);
    }

    public void update(View view) {
        SwitchCompat switchCompat = (SwitchCompat) view;
        commandActive.setValue(switchCompat.isChecked());
        switchCompat.setChecked(!switchCompat.isChecked());
    }

    public int getId() {
        return id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public boolean isActive() {
        return active;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    @NonNull
    public MutableLiveData<Boolean> getCommandActive() {
        return commandActive;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Preset) {
            Preset other = (Preset) obj;

            return this.id == other.getId() &&
                    this.title.equals(other.getTitle()) &&
                    this.active == other.isActive();
        } else {
            return false;
        }
    }
}
