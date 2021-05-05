package com.modolo.healthyplus.main;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import com.modolo.healthyplus.R;

public class MainFragmentDirections {
  private MainFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionNavMain() {
    return new ActionOnlyNavDirections(R.id.action_nav_main);
  }
}
