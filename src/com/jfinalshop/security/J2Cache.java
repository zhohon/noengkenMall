package com.jfinalshop.security;

import com.jfinal.plugin.cache.J2CacheService;
import net.oschina.j2cache.CacheManager;
import net.oschina.j2cache.RedisCacheChannel;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class J2Cache<K, V> implements Cache<K, V> {
    private static final Logger log = LoggerFactory.getLogger(J2Cache.class);

    private String regionName = "session";

    public J2Cache(String regionName) {
        this.regionName = regionName;
    }

    public J2Cache() {
    }

    public V get(K key) throws CacheException {
        if (key == null) {
            return null;
        } else {
            return (V) J2CacheService.get(regionName, key);
        }
    }

    public V put(K key, V value) {
        J2CacheService.set(regionName, key, value);
        return value;
    }

    public V remove(K key) throws CacheException {
        V value = get(key);
        J2CacheService.evict(regionName, key);
        return value;
    }

    public void clear() throws CacheException {
        J2CacheService.clear(regionName);
    }

    public int size() {
        int level1 = CacheManager.keys(RedisCacheChannel.LEVEL_1, regionName).size();
        int level2 = CacheManager.keys(RedisCacheChannel.LEVEL_2, regionName).size();
        return level1 > level2 ? level1 : level2;
    }

    public Set<K> keys() {
        List t1 = CacheManager.keys(RedisCacheChannel.LEVEL_1, regionName);
        Set<K> level1 = !CollectionUtils.isEmpty(t1) ? Collections.unmodifiableSet(new LinkedHashSet(t1)) : Collections.emptySet();
        List t2 = CacheManager.keys(RedisCacheChannel.LEVEL_1, regionName);
        Set<K> level2 = !CollectionUtils.isEmpty(t2) ? Collections.unmodifiableSet(new LinkedHashSet(t2)) : Collections.emptySet();
        level1.addAll(level2);
        return level1;
    }

    public Collection<V> values() {
        Set<K> t = keys();
        if (!CollectionUtils.isEmpty(t)) {
            ArrayList values = new ArrayList(t.size());
            Iterator i = t.iterator();
            while (i.hasNext()) {
                K key = (K) i.next();
                V value = this.get(key);
                if (value != null) {
                    values.add(value);
                }
            }
            return Collections.unmodifiableList(values);
        } else {
            return Collections.emptyList();
        }
    }

}
