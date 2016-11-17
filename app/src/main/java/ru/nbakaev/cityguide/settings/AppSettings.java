package ru.nbakaev.cityguide.settings;

import com.orm.dsl.Table;

/**
 * Created by ya on 11/17/2016.
 */

@Table
public class AppSettings {

    private Long id;
    private boolean offline;

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
}
