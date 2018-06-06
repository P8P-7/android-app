package nl.team_goliath.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import me.angrybyte.circularslider.CircularSlider;
import nl.team_goliath.app.R;
import nl.team_goliath.app.model.CommandSender;
import nl.team_goliath.app.proto.CommandMessageProto.CommandMessage;
import nl.team_goliath.app.proto.MoveWingCommandProto.MoveWingCommand;
import nl.team_goliath.app.proto.MoveWingCommandProto.ServoCommand;

/**
 * Main UI for the wing control screen.
 */
public class WingFragment extends Fragment {
    private final CommandSender moveCallback = (commandMessage) -> {
        if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            ((CommandSender) getActivity()).sendCommand(commandMessage);
        }
    };

    public static WingFragment newInstance() {
        return new WingFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.wing_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final CircularSlider circularTopLeft = view.findViewById(R.id.circular_tl);
        circularTopLeft.setOnSliderMovedListener(pos -> moveCallback.sendCommand(buildMoveWingCommand(pos, ServoCommand.Motor.LEFT_FRONT)));

        final CircularSlider circularBottomLeft = view.findViewById(R.id.circular_bl);
        circularBottomLeft.setOnSliderMovedListener(pos -> moveCallback.sendCommand(buildMoveWingCommand(pos, ServoCommand.Motor.LEFT_BACK)));

        final CircularSlider circularTopRight = view.findViewById(R.id.circular_tr);
        circularTopRight.setOnSliderMovedListener(pos -> moveCallback.sendCommand(buildMoveWingCommand(pos, ServoCommand.Motor.RIGHT_FRONT)));

        final CircularSlider circularBottomRight = view.findViewById(R.id.circular_br);
        circularBottomRight.setOnSliderMovedListener(pos -> moveCallback.sendCommand(buildMoveWingCommand(pos, ServoCommand.Motor.RIGHT_BACK)));
    }

    private CommandMessage buildMoveWingCommand(double position, ServoCommand.Motor motor) {
        // TODO: Let users define the servo speed.
        int speedPercentage = 50;

        MoveWingCommand moveCommand = MoveWingCommand.newBuilder()
                .addCommands(ServoCommand.newBuilder()
                        .setSpeed((int) Math.round(speedPercentage * 10.23))
                        .setDirection(position < 180 ? ServoCommand.Direction.UP : ServoCommand.Direction.DOWN)
                        .setMotor(motor)
                        .build())
                .build();

        return CommandMessage.newBuilder()
                .setMoveWingCommand(moveCommand)
                .build();
    }
}
