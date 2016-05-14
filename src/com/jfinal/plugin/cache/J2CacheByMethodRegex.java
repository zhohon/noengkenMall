/**
 * Copyright (c) 2011-2016, James Zhan 詹波 (jfinal@126.com).
 * <p/>
 * <p/>
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * <p/>
 * you may not use this file except in compliance with the License.
 * <p/>
 * You may obtain a copy of the License at
 * <p/>
 * <p/>
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * <p/>
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * <p/>
 * distributed under the License is distributed on an "AS IS" BASIS,
 * <p/>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * <p/>
 * See the License for the specific language governing permissions and
 * <p/>
 * limitations under the License.
 */

package com.jfinal.plugin.cache;

import com.jfinal.aop.Clear;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.StrKit;

import java.util.regex.Pattern;

/**
 * J2CacheByMethodRegex.
 * <p/>
 * The regular expression match the method name of the target.
 */
public class J2CacheByMethodRegex implements Interceptor {

    private Pattern addPattern;
    private Pattern removePattern;

    public J2CacheByMethodRegex(String addPatten, String removeRegex) {
        this(addPatten, removeRegex, true);
    }

    public J2CacheByMethodRegex(String addRegex, String removeRegex, boolean caseSensitive) {
        if (StrKit.isBlank(addRegex))
            throw new IllegalArgumentException("addRegex can not be blank.");
        if (StrKit.isBlank(removeRegex))
            throw new IllegalArgumentException("removeRegex can not be blank.");
        addPattern = caseSensitive ? Pattern.compile(addRegex) : Pattern.compile(addRegex, Pattern.CASE_INSENSITIVE);
        removePattern = caseSensitive ? Pattern.compile(removeRegex) : Pattern.compile(removeRegex, Pattern.CASE_INSENSITIVE);
    }

    public void intercept(Invocation inv) {
        if (addPattern.matcher(inv.getMethodName()).matches() && inv.getMethod().getAnnotation(Clear.class) == null) {
            new J2CacheInterceptor().intercept(inv);
        } else if (removePattern.matcher(inv.getMethodName()).matches()) {
            new ClearCache().intercept(inv);
        } else {
            inv.invoke();
        }
    }

}