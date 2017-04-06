package com.nbakaev.cityguide.poi.db;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.Date;

/**
 * Created by Наташа on 22.12.2016.
 */

public class DateGreenConverter implements PropertyConverter<Date, Long> {
    @Override
    public Date convertToEntityProperty(Long databaseValue) {
        return new Date(databaseValue);
    }

    @Override
    public Long convertToDatabaseValue(Date entityProperty) {
        return entityProperty.getTime();
    }
}
