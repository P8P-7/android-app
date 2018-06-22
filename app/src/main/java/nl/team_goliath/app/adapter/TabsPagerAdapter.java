package nl.team_goliath.app.adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import nl.team_goliath.app.ui.ArmFragment;
import nl.team_goliath.app.ui.MotorFragment;
import nl.team_goliath.app.ui.WingFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MotorFragment.newInstance();
            case 1:
                return WingFragment.newInstance();
            case 2:
                return ArmFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Control motors";
            case 1:
                return "Control wings";
            case 2:
                return "Control arms";
            default:
                return null;
        }
    }
}