/**
 * Copyright (c) 2011-2015, James Zhan 詹波 (jfinal@126.com).
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jfinal.plugin.cache;

import com.alibaba.fastjson.JSONArray;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.TableMapping;

/**
 * J2CacheInterceptor.
 */
public class J2CacheInterceptor implements Interceptor {


    final public void intercept(Invocation inv) {
        String cacheName = buildCacheName(inv);
        String cacheKey = buildCacheKey(inv);
        Object cacheData = before(cacheName, cacheKey);
        if (cacheData == null) {
            inv.invoke();
        } else {
            inv.setReturnValue(cacheData);
        }
        if (inv.getReturnValue() != null) {//刷新缓存，可以保持对象存活时间
            after(cacheName, cacheKey);
        }
    }


    //get data from cache
    private Object before(String cacheName, String cacheKey) {
        Object cacheData = J2CacheService.get(cacheName, cacheKey);
        return cacheData;
    }

    //set data to cache
    //update cache to add live time.
    private void after(String cacheName, String cacheKey) {
        Object cacheData = J2CacheService.get(cacheName, cacheKey);
        if (cacheData != null) {
            J2CacheService.set(cacheName, cacheKey, cacheData);
        }
    }

    private String buildCacheName(Invocation inv) {
        J2CacheName cacheName = inv.getMethod().getAnnotation(J2CacheName.class);
        if (cacheName != null)
            return cacheName.value().getCanonicalName();
        return getUsefulClass(inv.getTarget().getClass()).getCanonicalName();
    }

    private String buildCacheKey(Invocation inv) {
        JSONArray jsonArray = new JSONArray();
        jsonArray.add("M:" + inv.getMethodName());
        for (Object key : inv.getArgs()) {
            if (key instanceof Model) {
                Table table = TableMapping.me().getTable(getUsefulClass(key.getClass()));
                String[] pks = table.getPrimaryKey();
                for (String pk : pks) {
                    jsonArray.add(((Model) key).get(pk));
                }
            }
            jsonArray.add(key);
        }
        return JsonKit.toJson(jsonArray);
    }

    public static Class<? extends Model> getUsefulClass(Class c) {
        return c.getName().indexOf("EnhancerByCGLIB") == -1 ? c : c.getSuperclass();    // com.demo.blog.Blog$$EnhancerByCGLIB$$69a17158
    }

}





