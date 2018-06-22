package nl.team_goliath.app.ui;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerTabStrip;
import androidx.viewpager.widget.ViewPager;
import nl.team_goliath.app.R;
import nl.team_goliath.app.adapter.TabsPagerAdapter;

/**
 * Main UI for the control screen.
 */
public class ControlFragment extends Fragment {
    static ControlFragment newInstance() {
        return new ControlFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.control_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager viewPager = view.findViewById(R.id.view_pager);
        PagerTabStrip pagerTabStrip = view.findViewById(R.id.pager_header);
        pagerTabStrip.setTabIndicatorColor(ContextCompat.getColor(getContext(), R.color.background_material_light));

        FragmentPagerAdapter adapterViewPager = new TabsPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapterViewPager);
    }
}
