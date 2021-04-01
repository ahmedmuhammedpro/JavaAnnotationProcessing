package com.iti.pizzafactory.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to indicate classes that are part of a certain factory
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Factory {

    /**
     * The Class Type
     *
     * @return the class type
     */
    Class<?> type();

    /**
     * The identifier for determining which item should be instantiated
     *
     * @return the id
     */
    String id();

}
