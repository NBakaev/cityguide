package ru.nbakaev.cityguide.ui.cityselector;


public interface OnItemSelectedListener<T> {
    public void onSelect(T item, boolean selected);
    public void onSelectorActivated(boolean activated);
    public void onClear();
}
