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

    @Generated(hash = 1129666464)
    public AppSettings(Long id, boolean offline) {
        this.id = id;
        this.offline = offline;
    }

    @Generated(hash = 93977203)
    public AppSettings() {
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
}
