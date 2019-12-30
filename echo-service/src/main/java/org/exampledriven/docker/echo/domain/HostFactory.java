package org.exampledriven.docker.echo.domain;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Peter_Szanto on 3/2/2017.
 */
public class HostFactory {

    public static HostInfo create(HttpServletRequest request, String message, String url) {
        return new HostInfo(request.getLocalAddr(), request.getLocalPort(), getHostName(), message, url);
    }

    private static String getHostName() {
        InetAddress addr;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return addr.getHostName();
    }

}
