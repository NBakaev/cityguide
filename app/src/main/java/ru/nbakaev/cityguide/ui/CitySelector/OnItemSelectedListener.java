package ru.nbakaev.cityguide.ui.CitySelector;

import java.util.Objects;



public interface OnItemSelectedListener<T> {
    public void onSelect(T item, boolean selected);
    public void onSelectorActivated(boolean activated);
}
