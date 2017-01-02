package com.nbakaev.cityguide.poi.db;

/**
 * Created by ya on 12/8/2016.
 */

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.nbakaev.cityguide.util.StringUtils;


public class GreenConverter implements PropertyConverter<List<String>, String> {
    @Override
    public List convertToEntityProperty(String databaseValue) {
        if (databaseValue == null) {
            return null;
        } else {
            if (StringUtils.isEmpty(databaseValue)) {
                return Collections.emptyList();
            }

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