package org.tasks.ui;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.todoroo.astrid.data.Task;

import org.tasks.R;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;

import static com.todoroo.andlib.utility.AndroidUtilities.preLollipop;

public class PriorityControlSet extends TaskEditControlFragment {

    public static final int TAG = R.string.TEA_ctrl_importance_pref;

    public interface OnPriorityChanged {
        void onPriorityChange(int priority);
    }

    private static final String EXTRA_PRIORITY = "extra_priority";

    @Inject CheckBoxes checkBoxes;

    @Bind(R.id.priority_high) AppCompatRadioButton priorityHigh;
    @Bind(R.id.priority_medium) AppCompatRadioButton priorityMedium;
    @Bind(R.id.priority_low) AppCompatRadioButton priorityLow;
    @Bind(R.id.priority_none) AppCompatRadioButton priorityNone;

    private OnPriorityChanged callback;
    private int priority;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        callback = (OnPriorityChanged) activity;
    }

    @OnClick({R.id.priority_high, R.id.priority_medium, R.id.priority_low, R.id.priority_none})
    void onImportanceChanged(CompoundButton button) {
        priority = getPriority();
        callback.onPriorityChange(priority);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);
        if (savedInstanceState != null) {
            priority = savedInstanceState.getInt(EXTRA_PRIORITY);
        }
        if (priority == 0) {
            priorityHigh.setChecked(true);
        } else if(priority == 1) {
            priorityMedium.setChecked(true);
        } else if(priority == 2) {
            priorityLow.setChecked(true);
        } else {
            priorityNone.setChecked(true);
        }
        if (preLollipop()) {
            tintRadioButton(priorityHigh, 0);
            tintRadioButton(priorityMedium, 1);
            tintRadioButton(priorityLow, 2);
            tintRadioButton(priorityNone, 3);
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(EXTRA_PRIORITY, priority);
    }

    @Override
    protected int getLayout() {
        return R.layout.control_set_priority;
    }

    @Override
    protected int getIcon() {
        return R.drawable.ic_flag_24dp;
    }

    @Override
    public int controlId() {
        return TAG;
    }

    @Override
    public void initialize(boolean isNewTask, Task task) {
        priority = task.getImportance();
    }

    @Override
    public void apply(Task task) {
        task.setImportance(priority);
    }

    @Override
    public boolean hasChanges(Task original) {
        return original.getImportance() != priority;
    }

    private void tintRadioButton(AppCompatRadioButton radioButton, int priority) {
        int color = checkBoxes.getPriorityColors().get(priority);
        radioButton.setSupportButtonTintList(new ColorStateList(new int[][]{
                new int[]{-android.R.attr.state_checked}, new int[]{android.R.attr.state_checked}},
                new int[]{color, color}));
    }

    private int getPriority() {
        if (priorityHigh.isChecked()) {
            return 0;
        }
        if (priorityMedium.isChecked()) {
            return 1;
        }
        if (priorityLow.isChecked()) {
            return 2;
        }
        return 3;
    }
}
