package org.xbee.project.util;

public class ErrorInfo {
    private final String url;
    private final String type;
    private final String[] details;

    public ErrorInfo(CharSequence url, String type, String... details) {
        this.url = url.toString();
        this.type = type;
        this.details = details;
    }
}
