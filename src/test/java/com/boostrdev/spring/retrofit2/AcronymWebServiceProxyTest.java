package com.boostrdev.spring.retrofit2;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/config/spring-retrofit2-config.xml")
public class AcronymWebServiceProxyTest {

    private static final Logger LOG = LoggerFactory.getLogger(AcronymWebServiceProxyTest.class);

    @Autowired
    private AcronymWebServiceProxy acronymWebServiceProxy;

    private Call<List<AcronymData>> call;

    @Before
    public void setUp() {
        //The Call object can be used to make synchronous and asynchronous HTTP requests
        call = acronymWebServiceProxy.getAcronymResults("HMM");
    }

    @Test
    public void getAcronymResultsSynchronously() throws Exception {
        // Execute the call synchronously
        Response<List<AcronymData>> response = call.execute();
        verifyResponse(response);
    }

    @Test
    public void getAcronymResultsAsynchronously() throws Exception {
        // The count down latch allows the main thread to be notified once the callback has been executed
        final CountDownLatch callbackLatch = new CountDownLatch(1);
        
        // Execute the call asynchronously
        call.enqueue(new Callback<List<AcronymData>>() {
            public void onResponse(Call<List<AcronymData>> call, Response<List<AcronymData>> response) {
                verifyResponse(response);
                // Notify the main thread
                callbackLatch.countDown();
            }

            public void onFailure(Call<List<AcronymData>> call, Throwable t) {
                t.printStackTrace();
            }
        });
        // Wait 2 minutes to be notified by the call back thread
        callbackLatch.await(2000, TimeUnit.MILLISECONDS);
    }

    private void verifyResponse(Response<List<AcronymData>> response) {

        assertNotNull(response);

        // Request is successful if response code begins with 2XX
        if (response.isSuccessful()) {

            List<AcronymData> acronymDataList = response.body();

            // Check the body isn't empty
            assertNotNull(acronymDataList);

            // The response should have only one item
            assertEquals(acronymDataList.size(), 1);

            for (AcronymData acronymData : acronymDataList) {
                LOG.debug("Acronym short form: " + acronymData.toString());
                List<AcronymData.AcronymExpansion> acronymExpansionList = acronymData.getLfs();

                // The acronym data should have 8 acronym expansions
                assertEquals(acronymExpansionList.size(), 8);

                for (AcronymData.AcronymExpansion acronymExpansion : acronymExpansionList) {
                    LOG.debug("Acronym long form: " + acronymExpansion.toString());
                }
            }
        } else {
            fail();
        }
    }

}