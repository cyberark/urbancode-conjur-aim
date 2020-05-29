package com.urbancode.air.plugin.cyberark

import org.apache.http.impl.client.CloseableHttpClient
import com.urbancode.commons.httpcomponentsutil.CloseableHttpClientBuilder
import com.urbancode.air.XTrustProvider

import org.apache.commons.lang3.StringUtils
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils

import org.codehaus.jettison.json.JSONObject

import java.net.URLEncoder
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import groovy.json.JsonSlurper

class AIMRestClient {
    String serverUrl
    CloseableHttpClient client

    public AIMRestClient(
        String serverUrl,
        boolean trustAllCerts,
        String keyType,
        String keyStore,
        String keyPass,
		boolean isNtlm,
		String domain,
		String username,
		Stirng pass)
    {
        if (!serverUrl.substring(0, 7).equalsIgnoreCase("http://") && !serverUrl.substring(0, 8).equalsIgnoreCase("https://")){
            println("[Error] An HTTP protocol (http:// or https://) must be prepended to server URL: ${serverUrl}")
            throw new RuntimeException("Missing HTTP protocol in server URL: ${serverUrl}")
        }

        if (serverUrl.endsWith("/")) {
            serverUrl = serverUrl.substring(0, serverUrl.length() - 1)
        }

        this.serverUrl = serverUrl
        CloseableHttpClientBuilder clientBuilder = new CloseableHttpClientBuilder()

        /* Trust any certificate returned from the CyberArk server's key store */
        if (trustAllCerts) {
            XTrustProvider.install()
            clientBuilder.setTrustAllCerts(trustAllCerts)
        }

        /* Configure client-side keystore on the SSLContext for authenticating applications */
        if (!StringUtils.isEmpty(keyStore) && !StringUtils.isEmpty(keyPass)
            && !StringUtils.isEmpty(keyType))
        {
            File ksFile = new File(keyStore)

            if (!ksFile.exists() || !ksFile.isFile()) {
                println("[Error] Keystore file does not exist: " + ksFile.getAbsolutePath())
                throw new FileNotFoundException("Unable to find file: " + ksFile.getAbsolutePath())
            }

            KeyStore ks = KeyStore.getInstance(keyType)
            ks.load(new FileInputStream(ksFile), keyPass.toCharArray())
            KeyManagerFactory kmFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
            kmFactory.init(ks, keyPass.toCharArray())

            clientBuilder.setKeyManagers(kmFactory.getKeyManagers())
        }
        
        if(!StringUtils.isEmpty(username) && !StringUtils.isEmpty(pass)){
			clientBuilder.setNtlm(isNtlm);
			clientBuilder.setDomain(domain);
			clientBuilder.setUsername(username);
			clientBuilder.setPassword(pass);
		}

        client = clientBuilder.buildClient()
    }

    public def requestPass(String appId, String safe, String folder, String object) {
        String queryParameters = "?AppID=${appId}"

        if (safe) {
            queryParameters += "&Safe=${safe}"
        }
        if (folder) {
            queryParameters += "&Folder=${folder}"
        }
        if (object) {
            queryParameters += "&Object=${object}"
        }

        HttpGet request = new HttpGet(serverUrl + queryParameters)

        CloseableHttpResponse response = doGetRequest(request)

        return parseResponse(response)
    }

    public void cleanUp() {
        client.close()
    }

    //return an unparsed map of JSON properties from an http response
    private def parseResponse(CloseableHttpResponse response) {
        String json

        try {
            json = EntityUtils.toString(response.getEntity())
        }
        finally {
            response.close()
        }

        JsonSlurper slurper = new JsonSlurper()
        return slurper.parseText(json)
    }

    //return http response from an http get request. Will throw an exception if http return code isn't 200
    private CloseableHttpResponse doGetRequest(HttpGet request) {
        CloseableHttpResponse response = doRequest(request)
        def statusLine = response.getStatusLine()
        def statusCode = statusLine.getStatusCode()

        if (statusCode != 200) {
            def responseMessage = "Http Get request failed with an Http response code of ${statusCode}: ${statusLine.getReasonPhrase()}"
            println("[Error] ${responseMessage}")
            println("HTTP Response Body: ${response.entity?.content?.text}")
            throw new RuntimeException(responseMessage)
        }

        return response
    }

    private CloseableHttpResponse doRequest(HttpUriRequest request) {
        return doRequest(request, null)
    }

    //specify a json string to provide a string entity to the http request
    private CloseableHttpResponse doRequest(HttpUriRequest request, String contentString) {
        request.addHeader("Content-Type", "application/json")
        request.addHeader("Accept", "application/json")

        CloseableHttpResponse response = client.execute(request)

        return response
    }
}