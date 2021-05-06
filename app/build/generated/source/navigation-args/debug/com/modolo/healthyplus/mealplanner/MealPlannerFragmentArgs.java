package com.modolo.healthyplus.mealplanner;

import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavArgs;
import java.io.Serializable;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.HashMap;

public class MealPlannerFragmentArgs implements NavArgs {
  private final HashMap arguments = new HashMap();

  private MealPlannerFragmentArgs() {
  }

  @SuppressWarnings("unchecked")
  private MealPlannerFragmentArgs(HashMap argumentsMap) {
    this.arguments.putAll(argumentsMap);
  }

  @NonNull
  @SuppressWarnings("unchecked")
  public static MealPlannerFragmentArgs fromBundle(@NonNull Bundle bundle) {
    MealPlannerFragmentArgs __result = new MealPlannerFragmentArgs();
    bundle.setClassLoader(MealPlannerFragmentArgs.class.getClassLoader());
    if (bundle.containsKey("delete")) {
      boolean delete;
      delete = bundle.getBoolean("delete");
      __result.arguments.put("delete", delete);
    } else {
      __result.arguments.put("delete", false);
    }
    if (bundle.containsKey("meal")) {
      Meal meal;
      if (Parcelable.class.isAssignableFrom(Meal.class) || Serializable.class.isAssignableFrom(Meal.class)) {
        meal = (Meal) bundle.get("meal");
      } else {
        throw new UnsupportedOperationException(Meal.class.getName() + " must implement Parcelable or Serializable or must be an Enum.");
      }
      __result.arguments.put("meal", meal);
    } else {
      __result.arguments.put("meal", null);
    }
    if (bundle.containsKey("edit")) {
      boolean edit;
      edit = bundle.getBoolean("edit");
      __result.arguments.put("edit", edit);
    } else {
      __result.arguments.put("edit", false);
    }
    return __result;
  }

  @SuppressWarnings("unchecked")
  public boolean getDelete() {
    return (boolean) arguments.get("delete");
  }

  @SuppressWarnings("unchecked")
  @Nullable
  public Meal getMeal() {
    return (Meal) arguments.get("meal");
  }

  @SuppressWarnings("unchecked")
  public boolean getEdit() {
    return (boolean) arguments.get("edit");
  }

  @SuppressWarnings("unchecked")
  @NonNull
  public Bundle toBundle() {
    Bundle __result = new Bundle();
    if (arguments.containsKey("delete")) {
      boolean delete = (boolean) arguments.get("delete");
      __result.putBoolean("delete", delete);
    } else {
      __result.putBoolean("delete", false);
    }
    if (arguments.containsKey("meal")) {
      Meal meal = (Meal) arguments.get("meal");
      if (Parcelable.class.isAssignableFrom(Meal.class) || meal == null) {
        __result.putParcelable("meal", Parcelable.class.cast(meal));
      } else if (Serializable.class.isAssignableFrom(Meal.class)) {
        __result.putSerializable("meal", Serializable.class.cast(meal));
      } else {
        throw new UnsupportedOperationException(Meal.class.getName() + " must implement Parcelable or Serializable or must be an Enum.");
      }
    } else {
      __result.putSerializable("meal", null);
    }
    if (arguments.containsKey("edit")) {
      boolean edit = (boolean) arguments.get("edit");
      __result.putBoolean("edit", edit);
    } else {
      __result.putBoolean("edit", false);
    }
    return __result;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
        return true;
    }
    if (object == null || getClass() != object.getClass()) {
        return false;
    }
    MealPlannerFragmentArgs that = (MealPlannerFragmentArgs) object;
    if (arguments.containsKey("delete") != that.arguments.containsKey("delete")) {
      return false;
    }
    if (getDelete() != that.getDelete()) {
      return false;
    }
    if (arguments.containsKey("meal") != that.arguments.containsKey("meal")) {
      return false;
    }
    if (getMeal() != null ? !getMeal().equals(that.getMeal()) : that.getMeal() != null) {
      return false;
    }
    if (arguments.containsKey("edit") != that.arguments.containsKey("edit")) {
      return false;
    }
    if (getEdit() != that.getEdit()) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = 1;
    result = 31 * result + (getDelete() ? 1 : 0);
    result = 31 * result + (getMeal() != null ? getMeal().hashCode() : 0);
    result = 31 * result + (getEdit() ? 1 : 0);
    return result;
  }

  @Override
  public String toString() {
    return "MealPlannerFragmentArgs{"
        + "delete=" + getDelete()
        + ", meal=" + getMeal()
        + ", edit=" + getEdit()
        + "}";
  }

  public static class Builder {
    private final HashMap arguments = new HashMap();

    @SuppressWarnings("unchecked")
    public Builder(MealPlannerFragmentArgs original) {
      this.arguments.putAll(original.arguments);
    }

    public Builder() {
    }

    @NonNull
    public MealPlannerFragmentArgs build() {
      MealPlannerFragmentArgs result = new MealPlannerFragmentArgs(arguments);
      return result;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public Builder setDelete(boolean delete) {
      this.arguments.put("delete", delete);
      return this;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public Builder setMeal(@Nullable Meal meal) {
      this.arguments.put("meal", meal);
      return this;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public Builder setEdit(boolean edit) {
      this.arguments.put("edit", edit);
      return this;
    }

    @SuppressWarnings("unchecked")
    public boolean getDelete() {
      return (boolean) arguments.get("delete");
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public Meal getMeal() {
      return (Meal) arguments.get("meal");
    }

    @SuppressWarnings("unchecked")
    public boolean getEdit() {
      return (boolean) arguments.get("edit");
    }
  }
}
