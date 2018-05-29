package nl.team_goliath.app.viewmodel;

import androidx.lifecycle.ViewModel;
import nl.team_goliath.app.GoliathApp;
import nl.team_goliath.app.livedata.BatteryLiveData;

public class BatteryViewModel extends ViewModel {
    private final BatteryLiveData observableBatteryRepo = new BatteryLiveData(GoliathApp.getEventDispatcher());

    /**
     * Expose the LiveData Battery query so the UI can observe it.
     */
    public BatteryLiveData getObservableBatteryRepo() {
        return observableBatteryRepo;
    }
}