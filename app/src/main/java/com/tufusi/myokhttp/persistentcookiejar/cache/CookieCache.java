package com.tufusi.myokhttp.persistentcookiejar.cache;

import java.util.Collection;

import okhttp3.Cookie;

/**
 * Created by LeoCheung4ever on 2018/7/11.
 *
 * @See
 * @Description A CookieCache handles the volatile cookie session storage.
 */

public interface CookieCache extends Iterable<Cookie> {

    /**
     * Add all the new cookies to the session, existing cookies will be overwritten.
     *
     * @param cookies
     */
    void addAll(Collection<Cookie> cookies);

    /**
     * Clear all the cookies from the session.
     */
    void clear();

}
