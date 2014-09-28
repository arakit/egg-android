package jp.egg.android.util;

/*
 * Copyright (C) 2010 Michael Pardo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.lang.reflect.Field;
import java.util.*;


public final class ReflectionUtils {

    public interface FieldFilter {

        /**
         *
         * @param field The abstract field to be tested
         * @return <code>true</code> if and only if <code>pathname</code>
         * should be included
         */
        boolean accept(Field field);
    }

    public interface ClassFilter {

        /**
         *
         * @param clazz The abstract class to be tested
         * @return <code>true</code> if and only if <code>pathname</code>
         * should be included
         */
        boolean accept(Class<?> clazz);
    }


    //////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    //////////////////////////////////////////////////////////////////////////////////////

//    public static boolean isModel(Class<?> type) {
//        return isSubclassOf(type, Model.class) && (!Modifier.isAbstract(type.getModifiers()));
//    }

//    public static boolean isTypeSerializer(Class<?> type) {
//        return isSubclassOf(type, TypeSerializer.class);
//    }

    public static Map<String, Object> getDeclaredFieldNameAndValues(Object instance, Set<Field> fields) {
        Map<String, Object> map = new HashMap();
        for(Field f : fields){
            try {
                f.setAccessible(true);
                Object v = f.get(instance);
                map.put(f.getName(), v);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return map;
    }

    public static Map<Field, Object> getDeclaredFieldValues(Object instance, Set<Field> fields) {
        Map<Field, Object> map = new HashMap();
        for(Field f : fields){
            try {
                f.setAccessible(true);
                Object v = f.get(instance);
                map.put(f, v);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return map;
    }

    public static Map<Field, Object> getDeclaredFieldValues(Object instance, FieldFilter fieldFilter, Class<?> baseType) {
        return getDeclaredFieldValues(instance, getDeclaredFields(instance.getClass(), fieldFilter, baseType));
    }

    public static Set<Field> getDeclaredFields(Class<?> type, FieldFilter fieldFilter, Class<?> baseType) {
        Set<Field> declaredColumnFields = Collections.emptySet();

        if( baseType == null || isSubclassOf(type, baseType) ) {
            declaredColumnFields = new LinkedHashSet<Field>();

            Field[] fields = type.getDeclaredFields();
            Arrays.sort(fields, new Comparator<Field>() {
                @Override
                public int compare(Field field1, Field field2) {
                    return field2.getName().compareTo(field1.getName());
                }
            });
            for (Field field : fields) {
                //if (field.isAnnotationPresent(Column.class)) {
                if (fieldFilter == null || fieldFilter.accept(field)) {
                    declaredColumnFields.add(field);
                }
            }

            Class<?> parentType = type.getSuperclass();
            if (parentType != null) {
                declaredColumnFields.addAll(getDeclaredFields(parentType, fieldFilter, baseType));
            }
        }

        return declaredColumnFields;
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    //////////////////////////////////////////////////////////////////////////////////////

    public static boolean isSubclassOf(Class<?> type, Class<?> superClass) {
        if (type.getSuperclass() != null) {
            if (type.getSuperclass().equals(superClass)) {
                return true;
            }

            return isSubclassOf(type.getSuperclass(), superClass);
        }

        return false;
    }

    public static boolean isClassOrSubclassOf(Class<?> type, Class<?> superClass) {
        if (ReflectionUtils.isSubclassOf(type, superClass) || superClass.equals(type)) {
            return true;
        }
        return false;
    }


    ////


    public static boolean setFieldValue(Object instance, Field field, Object value){
        try {
            field.setAccessible(true);
            field.set(instance, value);
            return true;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }
}