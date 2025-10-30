package com.RKTechSolutions.rationtracker.navigation;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.RKTechSolutions.rationtracker.InventoryFragment;
import com.RKTechSolutions.rationtracker.R;
import com.RKTechSolutions.rationtracker.customerFragment;
import com.RKTechSolutions.rationtracker.databinding.ActivityNavigationViewBinding;
import com.RKTechSolutions.rationtracker.messageFragment;

public class NavigationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityNavigationViewBinding binding = ActivityNavigationViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Load default fragment (Customer/Home)
        if (savedInstanceState == null) {
            loadFragment(new customerFragment());
        }

        // Handle bottom navigation item clicks
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.nav_home:
                    selectedFragment = new customerFragment();
                    break;
                case R.id.nav_inventory:
                    selectedFragment = new InventoryFragment();
                    break;
//                case R.id.nav_customers:
//                    selectedFragment = new messageFragment();
//                    break;
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }

            return false;
        });
    }

    // Helper method to replace fragments
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.frame_layout, fragment)
                .commit();
    }
}
