package nl.team_goliath.app.livedata;

import nl.team_goliath.app.manager.EventDispatcher;
import nl.team_goliath.app.protos.BatteryRepositoryProtos.BatteryRepository;

public class BatteryLiveData extends SynchronizeLiveData<BatteryRepository> {
    public BatteryLiveData(EventDispatcher dispatcher) {
        super(dispatcher, BatteryRepository.class);
    }
}