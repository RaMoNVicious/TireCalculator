package com.android.tire.calc.smart.core;

import android.util.Log;

/**
 * Created by ramon on 15.04.16.
 */
public class aTirePreset {
    private aTire Size;
    private boolean Selected;

    public aTirePreset(String size, boolean selected) {
        Log.d(TiresCore.DS, "aTirePreset:size = " + size + ", selected = " + selected);

        this.Size = new aTire(size);
        Selected = selected;

        Log.d(TiresCore.DS, "aTirePreset:init = " + Size.getTireLabel());
    }

    public String toDisplay() {
        return Size.getTireLabel().replace(" ", "") + " " + Size.getRimLabel();
    }

    public boolean isSelected() {
        return Selected;
    }
}
