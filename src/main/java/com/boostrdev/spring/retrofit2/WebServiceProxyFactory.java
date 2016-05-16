package com.boostrdev.spring.retrofit2;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;

public final class WebServiceProxyFactory {

    private static final Logger LOG = LoggerFactory.getLogger(WebServiceProxyFactory.class);

    private WebServiceProxyFactory() {

    }

    /**
     * Create an implementation of the API endpoints defined by the {@code webServiceProxy} interface.
     *
     * @param webServiceProxyClazz
     * @param converterFactory
     * @param baseUrl
     * @param proxyHost
     * @param proxyPort
     * @param proxy
     * @param logging
     * @return
     */
    public static <T> T create(Class<T> webServiceProxyClazz, Converter.Factory converterFactory, String baseUrl, String proxyHost, int proxyPort, boolean proxy, boolean logging) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (logging) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
            LOG.debug("Configured HTTP logging");
        }
        if (proxy) {
            SocketAddress proxyAddress = new InetSocketAddress(proxyHost, proxyPort);
            Proxy httpProxy = new Proxy(Proxy.Type.HTTP, proxyAddress);
            builder.proxy(httpProxy);
            LOG.debug("Configured HTTP proxy {}:{}", proxyHost, proxyPort);
        }
        OkHttpClient client = builder.build();
        Retrofit retrofit = new Retrofit.Builder().client(client).baseUrl(baseUrl).addConverterFactory(converterFactory).build();
        return retrofit.create(webServiceProxyClazz);
    }
}
