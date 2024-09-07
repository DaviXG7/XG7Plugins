package com.xg7plugins.xg7plugins.data.database;

import com.xg7plugins.xg7plugins.XG7Plugins;
import com.xg7plugins.xg7plugins.boot.Plugin;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
public class Query {

    private final Iterator<Map<String,Object>> results;

    private final DBManager dbManager;

    public static CompletableFuture<Query> create(Plugin plugin, String sql, Object... params) {
        return XG7Plugins.getInstance().getDatabaseManager().executeQuery(plugin, sql,params);
    }

    public boolean hasNextLine() {
        return results.hasNext();
    }
    public Map<String, Object> nextLine() {
        return results.next();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) results.next().get(key);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public <T> T get(Class<? super Entity> clazz) {
        Map<String, Object> values = results.next();

        T instance = (T) clazz.getDeclaredConstructor().newInstance();

        Object id = null;

        for (Field f : clazz.getDeclaredFields()) {
            f.setAccessible(true);
            Object value = values.get(f.getName());

            if (value == null) continue;

            Entity.PKey pKey = f.getAnnotation(Entity.PKey.class);
            if (pKey != null) {
                if (dbManager.getEntitiesCached().asMap().containsKey(value)) return (T) dbManager.getEntitiesCached().asMap().get(f.get(instance));
                id = value;
            }

            if (f.getType() == List.class) {
                ParameterizedType parameterizedType = (ParameterizedType) f.getGenericType();
                Type tipoGenerico = parameterizedType.getActualTypeArguments()[0];

                List<Object> tList = new ArrayList<>();
                Object listInstance = ((Class<?>) tipoGenerico).getDeclaredConstructor().newInstance();

                for (Field fListf : ((Class<?>) tipoGenerico).getDeclaredFields()) {
                        fListf.setAccessible(true);
                        if (values.get(fListf.getName()) == null) continue;
                        fListf.set(listInstance, values.get(fListf.getName()));
                }
                tList.add(listInstance);
                tList.addAll(getResultList((Class<? super Entity>) tipoGenerico));

                f.set(instance, tList);

                continue;
            }
            f.set(instance, value);
        }

        dbManager.cacheEntity(id, (Entity) instance);

        return instance;
    }

    public <T> List<T> getResultList(Class<? super Entity> clazz) {
        List<T> tList = new ArrayList<>();
        while (results.hasNext()) {
            tList.add(get(clazz));
        }
        return tList;
    }
}

