package ru.nbakaev.cityguide.poi;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nikita on 10/24/2016.
 */

public class LocationDiff {

    private List<Poi> newPoi = new ArrayList<>();
    private List<Poi> removePoi = new ArrayList<>();

    public static LocationDiff of(List<Poi> old, List<Poi> current){
        LocationDiff diff = new LocationDiff();

        if (old.isEmpty()){
            diff.setNewPoi(current);
            return diff;
        }

        if (current.isEmpty()){
            diff.setRemovePoi(old);
            return diff;
        }


        for (Poi oldPoi : old){
            for (Poi  currentPoi : current){
                if (old.contains(currentPoi)){
                    // not changed
                }else{
                    diff.getNewPoi().add(currentPoi);
                }

                if (current.contains(oldPoi)){
                }else{
                    diff.getRemovePoi().add(oldPoi);
                }

            }

        }


        return diff;
    }

    public List<Poi> getRemovePoi() {
        return removePoi;
    }

    public void setRemovePoi(List<Poi> removePoi) {
        this.removePoi = removePoi;
    }

    public List<Poi> getNewPoi() {
        return newPoi;
    }

    public void setNewPoi(List<Poi> newPoi) {
        this.newPoi = newPoi;
    }
}
