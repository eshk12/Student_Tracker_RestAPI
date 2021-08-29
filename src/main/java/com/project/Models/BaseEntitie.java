package com.project.Models;

import org.apache.commons.lang.ArrayUtils;

import java.lang.reflect.Field;

public abstract class BaseEntitie {
    private int id;
    private boolean deleted;

    public BaseEntitie(int id, boolean deleted) {
        this.id = id;
        this.deleted = deleted;
    }

    public BaseEntitie(boolean deleted) {
        this.deleted = deleted;
    }

    public BaseEntitie() {
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setObject(Object other) {
        try {
            Field[] fields = new Field[0];
            Class clazz = this.getClass();
            do {
                fields = (Field[]) ArrayUtils.addAll(fields, clazz.getDeclaredFields());
                clazz = clazz.getSuperclass();

            } while (!clazz.equals(Object.class));
            for (Field f : fields) {
                f.setAccessible(true);
                Object value = f.get(other);
                if (value != null) {
                    f.set(this, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
