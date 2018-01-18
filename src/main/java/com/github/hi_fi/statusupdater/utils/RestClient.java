package com.github.hi_fi.statusupdater.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.Header;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;

public class RestClient {

	private static CloseableHttpClient httpclient;
	private static HttpClientContext context;
	private static CookieStore cookieStore;

	public static CloseableHttpClient getHttpclient() {
		if (httpclient == null) {
			createHttpClient();
		}
		return httpclient;
	}

	public static HttpClientContext getContext() {
		return getContext(Configuration.url, Configuration.username, Configuration.password);
	}

	public static HttpClientContext getContext(String url, String userName, String password) {
		if (context == null) {
			createClientContext(url, userName, password);
		}
		return context;
	}
	
	public static String urlEncodeString(String encodeable) {
		try {
			//0 -> %20 replacement needed because of QC
			return URLEncoder.encode(encodeable, "UTF8").replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Encoding of string was not successfull. Error message: "+e.getMessage());
		}
	}

	public static HttpResponse makeGetCall(String URL, Header...headers ) {
		HttpGet request = new HttpGet(URL);
		for (Header header : headers) {
			request.addHeader(header);
		}
		return makeCall(request);
	}

	public static HttpResponse makePostCall(String URL, StringEntity payload, Header...headers) {
		HttpPost httpPostRequest = new HttpPost(URL);
		if (payload != null) {
			httpPostRequest.setEntity(payload);
		}
		for (Header header : headers) {
			httpPostRequest.addHeader(header);
		}
		return makeCall(httpPostRequest);
	}

	public static HttpResponse makePostUploadCall(String URL, HttpEntity payload) {
		HttpPost httpPostRequest = new HttpPost(URL);
		httpPostRequest.setEntity(payload);
		httpPostRequest.setHeader("X-Atlassian-Token", "nocheck");

		return makeCall(httpPostRequest);
	}

	public static HttpResponse makePutCall(String URL, StringEntity payload, Header...headers) {
		HttpPut httpPutRequest = new HttpPut(URL);
		if (payload != null) {
			httpPutRequest.setEntity(payload);
		}
		for (Header header : headers) {
			httpPutRequest.addHeader(header);
		}

		return makeCall(httpPutRequest);
	}

	private static HttpResponse makeCall(HttpUriRequest request) {
		try {
			HttpResponse response = getHttpclient().execute(request, getContext());
			int responseCode = response.getStatusLine().getStatusCode();
			if (responseCode >= 400) {
				String responseString = ResponseParser.parseResponseToString(response);
				throw new RuntimeException(String.format("Error in REST call. Error code %s. Page returned: %s",
						responseCode, responseString));
			}
			return response;
		} catch (ClientProtocolException e) {
			throw new RuntimeException("Protocol error in request, " + e.getMessage());
		} catch (IOException e) {
			throw new RuntimeException("IO Exception, " + e.getMessage());
		}
	}

	private static CookieStore getCookieStore() {
		if (cookieStore == null) {
			cookieStore = new BasicCookieStore();
		}
		return cookieStore;
	}

	private static HttpClientContext createClientContext(String hostAddressWithProtocol, String userName,
			String password) {
		URL url;
		try {
			url = new URL(hostAddressWithProtocol);
			HttpHost targetHost = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());

			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(AuthScope.ANY, new NTCredentials(userName, password, null, null));
			AuthCache authCache = new BasicAuthCache();
			authCache.put(targetHost, new BasicScheme());

			context = HttpClientContext.create();
			context.setCredentialsProvider(credsProvider);
			context.setAuthCache(authCache);
			context.setCookieStore(getCookieStore());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return context;
	}

	private static void createHttpClient() {
		try {
			SSLContextBuilder builder = new SSLContextBuilder();
			builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build(),
					NoopHostnameVerifier.INSTANCE);
			httpclient = HttpClients.custom().setRedirectStrategy(new LaxRedirectStrategy()).setSSLSocketFactory(sslsf)
					.setMaxConnTotal(20).setMaxConnPerRoute(10).setDefaultCookieStore(getCookieStore())
					.setDefaultRequestConfig(requestConfigWithTimeout(20000)).build();
		} catch (KeyManagementException e1) {
			e1.printStackTrace();
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (KeyStoreException e1) {
			e1.printStackTrace();
		}
	}

	private static RequestConfig requestConfigWithTimeout(int timeoutInMilliseconds) {
		return RequestConfig.copy(RequestConfig.DEFAULT).setSocketTimeout(timeoutInMilliseconds)
				.setConnectTimeout(timeoutInMilliseconds).setConnectionRequestTimeout(timeoutInMilliseconds).build();
	}
}
