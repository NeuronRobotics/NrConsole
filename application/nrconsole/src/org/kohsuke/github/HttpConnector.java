package org.kohsuke.github;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Pluggability for customizing HTTP request behaviors or using altogether different library.
 *
 * <p>
 * For example, you can implement this to st custom timeouts.
 *
 * @author Kohsuke Kawaguchi
 */
public interface HttpConnector {
    /**
     * Opens a connection to the given URL.
     */
    HttpURLConnection connect(URL url) throws IOException;

    /**
     * Default implementation that uses {@link URL#openConnection()}.
     */
    HttpConnector DEFAULT = new HttpConnector() {
        public HttpURLConnection connect(URL url) throws IOException {
            return (HttpURLConnection) url.openConnection();
        }
    };
}
