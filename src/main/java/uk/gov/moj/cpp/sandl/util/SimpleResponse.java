package uk.gov.moj.cpp.sandl.util;

public class SimpleResponse {

    private final String string;

    private SimpleResponse(final String string) {
        this.string = string;
    }

    public static SimpleResponse of(final String string) {
        return new SimpleResponse(string);
    }

    public String asString() {
        return string;
    }


}
