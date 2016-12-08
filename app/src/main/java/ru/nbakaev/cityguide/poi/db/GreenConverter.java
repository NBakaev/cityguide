package ru.nbakaev.cityguide.poi.db;

/**
 * Created by ya on 12/8/2016.
 */

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.Arrays;
import java.util.List;

/**
 * DOLE BREEE SQLITE BREEEEEE!!!**
 * i choosed to convert List into one string
 * that is going to be saved in database, and vice versa
 */

public class GreenConverter implements PropertyConverter<List<String>, String> {
    @Override
    public List convertToEntityProperty(String databaseValue) {
        if (databaseValue == null) {
            return null;
        } else {
            List<String> lista = Arrays.asList(databaseValue.split(","));
            return lista;
        }
    }

    @Override
    public String convertToDatabaseValue(List<String> entityProperty) {
        if (entityProperty == null) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder();
            for (String link : entityProperty) {
                sb.append(link);
                sb.append(",");
            }
            return sb.toString();
        }
    }
}