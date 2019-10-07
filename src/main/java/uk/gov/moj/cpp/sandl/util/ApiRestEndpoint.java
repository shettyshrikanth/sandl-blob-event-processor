package uk.gov.moj.cpp.sandl.util;

public enum ApiRestEndpoint {

    TIMELINESS_SJP_CASE_PENDING(
            "/misystemdata-query-api/query/api/rest/misystemdata/timeliness/cases-pending",
                    "application/vnd.misystemdata.timeliness.sjp-cases-pending+json");

    private final String uri;
    private final String mediaType;

    ApiRestEndpoint(final String uri, final String mediaType) {
        this.uri = uri;
        this.mediaType = mediaType;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getUri() {
        return uri;
    }
}