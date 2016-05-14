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

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Model;

/**
 * ClearCache.
 */
public class ClearCache implements Interceptor {
    public static final Log log = Log.getLog(ClearCache.class);

    final public void intercept(Invocation inv) {
        inv.invoke();
        String cacheName = buildCacheName(inv);
        if (cacheName != null) {
            after(cacheName);
        } else {
            log.warn(inv.getMethod().getName() + "未找到对应的缓存类型，无法清除.");
        }
    }

    //clear the cache
    private void after(String cacheName) {
        J2CacheService.clear(cacheName);
    }

    private String buildCacheName(Invocation inv) {
        J2CacheName cacheName = inv.getMethod().getAnnotation(J2CacheName.class);
        if (cacheName == null) {
            Class invClass = J2CacheInterceptor.getUsefulClass(inv.getTarget().getClass());
            if (inv.getTarget() instanceof Model) {
                return invClass.getCanonicalName();
            }
        }
        return cacheName.value().getCanonicalName();
    }

}





