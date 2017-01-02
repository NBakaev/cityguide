package com.nbakaev.cityguide.settings;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by ya on 11/17/2016.
 */

@Entity
public class AppSettings {

    @Id
    private Long id;
    private boolean offline;
    private boolean enableExperimentalFeature = false;
    private boolean firstRun = true;
    private boolean trackMe = true;


    @Generated(hash = 1739082689)
    public AppSettings(Long id, boolean offline, boolean enableExperimentalFeature,
            boolean firstRun, boolean trackMe) {
        this.id = id;
        this.offline = offline;
        this.enableExperimentalFeature = enableExperimentalFeature;
        this.firstRun = firstRun;
        this.trackMe = trackMe;
    }

    @Generated(hash = 93977203)
    public AppSettings() {
    }

    public void setTrackMe(boolean trackMe) {
        this.trackMe = trackMe;
    }

    public boolean isFirstRun() {
        return firstRun;
    }

    public void setFirstRun(boolean firstRun) {
        this.firstRun = firstRun;
    }

    public boolean isEnableExperimentalFeature() {
        return enableExperimentalFeature;
    }

    public void setEnableExperimentalFeature(boolean enableExperimentalFeature) {
        this.enableExperimentalFeature = enableExperimentalFeature;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isOffline() {
        return offline;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    public boolean getOffline() {
        return this.offline;
    }

    public boolean getEnableExperimentalFeature() {
        return this.enableExperimentalFeature;
    }

    public boolean getFirstRun() {
        return this.firstRun;
    }

    public boolean getTrackMe() {
        return this.trackMe;
    }

}
