package com.algonquincollege.hurdleg.planets.retrofit;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Interceptor for Basic Authentication
 *
 * @author Gerald.Hurdle@AlgonquinCollege.com
 */
// TODO #3 - class BasicAuthenticationInterceptor
public class BasicAuthenticationInterceptor implements Interceptor {
    private String credentials;

    public BasicAuthenticationInterceptor(String user, String password) {
        this.credentials = Credentials.basic(user, password);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request authenticatedRequest = request.newBuilder()
                .header("Authorization", credentials).build();
        return chain.proceed(authenticatedRequest);
    }
}
