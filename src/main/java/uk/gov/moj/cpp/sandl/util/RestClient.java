package uk.gov.moj.cpp.sandl.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.justice.services.test.utils.core.http.BaseUriProvider.getBaseUri;

import uk.gov.justice.services.common.http.HeaderConstants;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import com.microsoft.azure.functions.ExecutionContext;

public class RestClient {

    private static final String USER_ID = "a9448185-672e-4aea-94d6-5988355ed459";

    public static Response doGet(final ApiRestEndpoint apiRestEndpoint, final Map<String, String> queryParams, final ExecutionContext context) {

        final String url = createUrlWithParam(getBaseUri() + apiRestEndpoint.getUri(), queryParams);
        context.getLogger().info("url details ********** " + url + " MediaType " + apiRestEndpoint.getMediaType());

        //public api testing working
        //return new RestClient().query("http://dummy.restapiexample.com/api/v1/employees", "*/*");
        //Rest api exposed by Azure working..getApi tested
        return new uk.gov.justice.services.test.utils.core.rest.RestClient().query("http://10.87.12.18:8080/misystemdata-query-api/query/api/rest/misystemdata/timeliness/cases-pending", "application/vnd.misystemdata.timeliness.sjp-cases-pending+json", newHeadersMap());

        //return new uk.gov.justice.services.test.utils.core.rest.RestClient().query("https://spiketestblobeventprocessor.azurewebsites.net/api/HttpTrigger-Java?code=yyd27DiGHaadu4Ww8OblAha21FiaYasAt2rCIPprgHFQLeJpambO/w==&name=refere", "*/*");

        //This is actual cal to reference data and it has limitaion at the moment

        //192.168.3.253
        //return new RestClient().query(url, apiRestEndpoint.getMediaType(), newHeadersMap());
    }

    private static String createUrlWithParam(final String url, final Map<String, String> queryParam) {
        if (queryParam.isEmpty()) {
            return url;
        }

        final StringBuilder sb = new StringBuilder();
        for (final Map.Entry<String, String> e : queryParam.entrySet()) {
            if (sb.length() > 0) {
                sb.append('&');
            }
            try {
                sb.append(URLEncoder.encode(e.getKey(), "UTF-8")).append('=').append(URLEncoder.encode(e.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e1) {
                return null;
            }
        }
        return url.concat("?" + sb.toString());
    }

    private static MultivaluedMap<String, Object> newHeadersMap() {
        final MultivaluedMap<String, Object> map = new MultivaluedHashMap<>();
        map.add(HeaderConstants.USER_ID, USER_ID);
        return map;
    }

    public static SimpleResponse getRequest(final ApiRestEndpoint apiRestEndpoint, final Map<String, String> queryParams, final ExecutionContext context) {
        final Response response = doGet(apiRestEndpoint, queryParams, context);
        assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));
        return SimpleResponse.of(response.readEntity(String.class));
    }
}
