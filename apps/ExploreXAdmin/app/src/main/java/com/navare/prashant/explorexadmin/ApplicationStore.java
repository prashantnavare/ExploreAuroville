package com.navare.prashant.explorexadmin;

import android.app.Application;

/**
 * Created by prashant on 30-Apr-17.
 */

public class ApplicationStore extends Application {
    public static final String BASE_URL = "http://192.168.1.104:5678";
    // API URLs
    public static final String VERSION_URL = ApplicationStore.BASE_URL + "/api/explorex/v1/admin/version?type=android";
    public static final String LOGIN_URL = ApplicationStore.BASE_URL + "/api/explorex/v1/admin/login";
    public static final String SIGNUP_URL = ApplicationStore.BASE_URL + "/api/explorex/v1/admin/signup";
    public static final String FORGOT_PASSWORD_URL = ApplicationStore.BASE_URL + "/api/explorex/v1/admin/forgot";
    public static final String RESET_PASSWORD_URL = ApplicationStore.BASE_URL + "/api/explorex/v1/admin/reset";
}
