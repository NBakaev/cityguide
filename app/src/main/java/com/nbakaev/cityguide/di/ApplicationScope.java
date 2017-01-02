package com.nbakaev.cityguide.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by Nikita on 10/12/2016.
 */

@Retention(RetentionPolicy.RUNTIME)
@Scope
public @interface ApplicationScope {
}