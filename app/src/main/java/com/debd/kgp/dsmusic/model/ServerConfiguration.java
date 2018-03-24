package com.debd.kgp.dsmusic.model;

import java.io.Serializable;

public class ServerConfiguration implements Serializable {

    private String URL;
    private int port;

    public ServerConfiguration() {}

    public ServerConfiguration(String URL, int port) {
        this.URL = URL;
        this.port = port;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
