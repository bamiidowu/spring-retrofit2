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
        call = acronymWebServiceProxy.getAcronymResults("HMM");
    }

    @Test
    public void getAcronymResultsSynchronously() throws Exception {
        Response<List<AcronymData>> response = call.execute();
        verifyResponse(response);
    }

    @Test
    public void getAcronymResultsAsynchronously() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        call.enqueue(new Callback<List<AcronymData>>() {
            public void onResponse(Call<List<AcronymData>> call, Response<List<AcronymData>> response) {
                verifyResponse(response);
                lock.countDown();
            }

            public void onFailure(Call<List<AcronymData>> call, Throwable t) {
                t.printStackTrace();
            }
        });

        lock.await(2000, TimeUnit.MILLISECONDS);
    }

    public void verifyResponse(Response<List<AcronymData>> response) {

        // Request is successful if response code begins with 2XX
        assertTrue(response.isSuccessful());

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
    }

}