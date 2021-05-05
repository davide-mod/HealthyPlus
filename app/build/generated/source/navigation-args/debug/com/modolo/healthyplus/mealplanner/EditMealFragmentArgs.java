package com.modolo.healthyplus.mealplanner;

import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.navigation.NavArgs;
import java.io.Serializable;
import java.lang.IllegalArgumentException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.HashMap;

public class EditMealFragmentArgs implements NavArgs {
  private final HashMap arguments = new HashMap();

  private EditMealFragmentArgs() {
  }

  @SuppressWarnings("unchecked")
  private EditMealFragmentArgs(HashMap argumentsMap) {
    this.arguments.putAll(argumentsMap);
  }

  @NonNull
  @SuppressWarnings("unchecked")
  public static EditMealFragmentArgs fromBundle(@NonNull Bundle bundle) {
    EditMealFragmentArgs __result = new EditMealFragmentArgs();
    bundle.setClassLoader(EditMealFragmentArgs.class.getClassLoader());
    if (bundle.containsKey("meal")) {
      Meal meal;
      if (Parcelable.class.isAssignableFrom(Meal.class) || Serializable.class.isAssignableFrom(Meal.class)) {
        meal = (Meal) bundle.get("meal");
      } else {
        throw new UnsupportedOperationException(Meal.class.getName() + " must implement Parcelable or Serializable or must be an Enum.");
      }
      if (meal == null) {
        throw new IllegalArgumentException("Argument \"meal\" is marked as non-null but was passed a null value.");
      }
      __result.arguments.put("meal", meal);
    } else {
      throw new IllegalArgumentException("Required argument \"meal\" is missing and does not have an android:defaultValue");
    }
    return __result;
  }

  @SuppressWarnings("unchecked")
  @NonNull
  public Meal getMeal() {
    return (Meal) arguments.get("meal");
  }

  @SuppressWarnings("unchecked")
  @NonNull
  public Bundle toBundle() {
    Bundle __result = new Bundle();
    if (arguments.containsKey("meal")) {
      Meal meal = (Meal) arguments.get("meal");
      if (Parcelable.class.isAssignableFrom(Meal.class) || meal == null) {
        __result.putParcelable("meal", Parcelable.class.cast(meal));
      } else if (Serializable.class.isAssignableFrom(Meal.class)) {
        __result.putSerializable("meal", Serializable.class.cast(meal));
      } else {
        throw new UnsupportedOperationException(Meal.class.getName() + " must implement Parcelable or Serializable or must be an Enum.");
      }
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
    EditMealFragmentArgs that = (EditMealFragmentArgs) object;
    if (arguments.containsKey("meal") != that.arguments.containsKey("meal")) {
      return false;
    }
    if (getMeal() != null ? !getMeal().equals(that.getMeal()) : that.getMeal() != null) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = 1;
    result = 31 * result + (getMeal() != null ? getMeal().hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "EditMealFragmentArgs{"
        + "meal=" + getMeal()
        + "}";
  }

  public static class Builder {
    private final HashMap arguments = new HashMap();

    @SuppressWarnings("unchecked")
    public Builder(EditMealFragmentArgs original) {
      this.arguments.putAll(original.arguments);
    }

    @SuppressWarnings("unchecked")
    public Builder(@NonNull Meal meal) {
      if (meal == null) {
        throw new IllegalArgumentException("Argument \"meal\" is marked as non-null but was passed a null value.");
      }
      this.arguments.put("meal", meal);
    }

    @NonNull
    public EditMealFragmentArgs build() {
      EditMealFragmentArgs result = new EditMealFragmentArgs(arguments);
      return result;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public Builder setMeal(@NonNull Meal meal) {
      if (meal == null) {
        throw new IllegalArgumentException("Argument \"meal\" is marked as non-null but was passed a null value.");
      }
      this.arguments.put("meal", meal);
      return this;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    public Meal getMeal() {
      return (Meal) arguments.get("meal");
    }
  }
}
