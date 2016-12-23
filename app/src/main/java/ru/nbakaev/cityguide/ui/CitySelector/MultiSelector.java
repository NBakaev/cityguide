package ru.nbakaev.cityguide.ui.cityselector;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



public class MultiSelector<T> implements Serializable{
    protected List<T> selected = new ArrayList<>();
    protected OnItemSelectedListener<T> listener = null;
    boolean activated = false;

    public MultiSelector()
    {

    }
    public void select(T item)
    {
        boolean result = !selected.contains(item);
        if (result)
            selected.add(item);
        else
            selected.remove(item);

        if (listener!=null)
            listener.onSelect(item, result);
        if (activated!=selected.size()>0)
        {
            activated=!activated;
            if (listener!=null)
                listener.onSelectorActivated(activated);
        }
    }

    public void clear()
    {
        selected.clear();
        activated = false;
        if (listener!=null) {
            listener.onClear();
            listener.onSelectorActivated(activated);
        }
    }

    public List<T> getSelected()
    {
        return selected;
    }

    public OnItemSelectedListener<T> getListener() {
        return listener;
    }

    public void setListener(OnItemSelectedListener<T> listener) {
        this.listener = listener;
    }

    public boolean isSelected(T item)
    {
        return selected.contains(item);
    }

    public boolean isActivated()
    {
        return  isActivated();
    }

}
