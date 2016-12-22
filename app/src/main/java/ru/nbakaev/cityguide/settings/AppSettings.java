package ru.nbakaev.cityguide.settings;

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
    private boolean tracked;
    private boolean enableExperimentalFeature;
    private boolean firstRun = true;

    @Generated(hash = 1109753121)
    public AppSettings(Long id, boolean offline, boolean tracked,
            boolean enableExperimentalFeature, boolean firstRun) {
        this.id = id;
        this.offline = offline;
        this.tracked = tracked;
        this.enableExperimentalFeature = enableExperimentalFeature;
        this.firstRun = firstRun;
    }

    @Generated(hash = 93977203)
    public AppSettings() {
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

    public boolean getTracked() {
        return this.tracked;
    }

    public void setTracked(boolean tracked) {
        this.tracked = tracked;
    }


}
