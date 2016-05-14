package com.jfinalshop.security;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

/**
 * Created by Jack.Zhou on 3/29/2016.
 */
public class J2CacheManager implements CacheManager {
    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        return new J2Cache<K, V>(name);
    }
}
