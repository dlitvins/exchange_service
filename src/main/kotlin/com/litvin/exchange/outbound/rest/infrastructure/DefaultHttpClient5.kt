package com.litvin.exchange.outbound.rest.infrastructure

import feign.Client
import feign.hc5.ApacheHttp5Client
import org.apache.hc.client5.http.config.ConnectionConfig
import org.apache.hc.client5.http.config.RequestConfig
import org.apache.hc.client5.http.cookie.StandardCookieSpec
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder
import org.apache.hc.core5.http.ssl.TLS
import org.apache.hc.core5.pool.PoolConcurrencyPolicy
import org.apache.hc.core5.pool.PoolReusePolicy
import org.apache.hc.core5.ssl.SSLContexts
import org.apache.hc.core5.util.Timeout

fun defaultFeignApacheClient5(
    maxConnectionPerRoute: Int = 5,
    maxConnectionTotal: Int = 10,
): Client = ApacheHttp5Client(defaultApacheHttpClient5(maxConnectionPerRoute, maxConnectionTotal))

fun defaultApacheHttpClient5(
    maxConnectionPerRoute: Int,
    maxConnectionTotal: Int,
): CloseableHttpClient = preConfiguredApacheClient5Builder(maxConnectionPerRoute, maxConnectionTotal).build()

fun preConfiguredApacheClient5Builder(
    maxConnectionPerRoute: Int = 5,
    maxConnectionTotal: Int = 10,
): HttpClientBuilder {
    val clientConnectionManager: PoolingHttpClientConnectionManager =
        PoolingHttpClientConnectionManagerBuilder
            .create()
            .setMaxConnPerRoute(maxConnectionPerRoute)
            .setMaxConnTotal(maxConnectionTotal)
            .setPoolConcurrencyPolicy(PoolConcurrencyPolicy.STRICT)
            .setConnPoolPolicy(PoolReusePolicy.FIFO)
            .setSSLSocketFactory(
                SSLConnectionSocketFactoryBuilder
                    .create()
                    .setSslContext(SSLContexts.createSystemDefault())
                    .setTlsVersions(TLS.V_1_3)
                    .build(),
            ).setDefaultConnectionConfig(
                ConnectionConfig
                    .custom()
                    .setSocketTimeout(Timeout.ofSeconds(10))
                    .setConnectTimeout(Timeout.ofSeconds(10))
                    // see https://docs.cloudfoundry.org/adminguide/routing-keepalive.html#app-idle-timeout
                    .setTimeToLive(Timeout.ofSeconds(90))
                    .build(),
            ).build()

    return HttpClientBuilder
        .create()
        .useSystemProperties() // enable Java system properties support like https.proxyHost
        .setDefaultRequestConfig(
            RequestConfig
                .custom()
                .setCookieSpec(StandardCookieSpec.STRICT) // https://stackoverflow.com/a/40697322/273446
                .build(),
        ).disableCookieManagement()
        .disableRedirectHandling()
        .setConnectionManager(clientConnectionManager)
}
