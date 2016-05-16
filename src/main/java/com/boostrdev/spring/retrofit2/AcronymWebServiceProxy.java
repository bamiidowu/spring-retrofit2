package com.boostrdev.spring.retrofit2;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

import java.util.List;


/**
 * Retrofit Service to send requests to Acronym web service and
 * convert the Json response to POJO class.
 */
public interface AcronymWebServiceProxy {

    /**
     * Query Parameter.
     */
    public static final String SHORT_FORM_QUERY_PARAMETER =
            "sf";

    /**
     * Get List of LongForm associated with acronym from Acronym Web
     * service.  Asynchronous execution requires the last parameter of
     * the method be a Callback.
     *
     * @param shortForm
     * @return List of JsonAcronym
     */
    @Headers("Cache-Control: public, max-stale=10")
    @GET("/software/acromine/dictionary.py")
    public Call<List<AcronymData>> getAcronymResults(@Query(SHORT_FORM_QUERY_PARAMETER) String shortForm);
}
