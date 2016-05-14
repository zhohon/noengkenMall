package com.jfinal.plugin.cache;

import net.oschina.j2cache.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Jack.Zhou on 2015/5/18.
 */
public class J2CacheService {
    private static final Logger logger = LoggerFactory
            .getLogger(J2CacheService.class);
    public static final String default_cache = "system";
    private static J2CacheService instance;
    private CacheChannel cacheChannel;

    private J2CacheService() {
        System.setProperty("java.net.preferIPv4Stack", "true"); //Disable IPv6 in JVM
        cacheChannel = J2Cache.getChannel();
    }

    public static J2CacheService getInstance() {
        synchronized (J2CacheService.class) {
            if (instance == null) {
                instance = new J2CacheService();
            }
        }
        return instance;
    }

    public static Object get(Object key) {
        return getInstance().get(default_cache, key);
    }

    public static void set(Object key, Object value) {
        getInstance().set(default_cache, key, value);
    }


    public static Object get(String region, Object key) {
        CacheObject obj = getInstance().cacheChannel.get(region, key);
        logger.trace("get[{},{},L{}]=>{}", new Object[]{obj.getRegion(), obj.getKey(), obj.getLevel(), obj.getValue()});
        return obj.getValue();
    }

    public static void set(String region, Object key, Object value) {
        logger.trace("set[{},{}]<={}", new Object[]{region, key, value});
        getInstance().cacheChannel.set(region, key, value);
    }

    public static List<?> keys(String region) {
        return getInstance().cacheChannel.keys(region);
    }

    public static void evict(String region, Object key) {
        getInstance().cacheChannel.evict(region, key);
    }

    public static void evict( Object key) {
        getInstance().cacheChannel.evict(default_cache, key);
    }

    public static void clear(String region) {
        List level1 = CacheManager.keys(RedisCacheChannel.LEVEL_1, region);
        List level2 = CacheManager.keys(RedisCacheChannel.LEVEL_2, region);
        if (level1 != null && level1.size() > 0) {
            CacheManager.clear(RedisCacheChannel.LEVEL_1,region);
        }
        if (level2 != null && level2.size() > 0) {
            CacheManager.clear(RedisCacheChannel.LEVEL_2,region);
        }
    }
}
