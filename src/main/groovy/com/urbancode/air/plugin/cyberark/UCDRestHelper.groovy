package com.urbancode.air.plugin.cyberark

import com.urbancode.ud.client.ComponentClient

class UCDRestHelper {
    ComponentClient componentClient

    public UCDRestHelper() {
        String username = "PasswordIsAuthToken"
        String password = String.format("{\"token\": \"%s\"}", System.getenv("AUTH_TOKEN"))
        String webUrl = System.getenv("AH_WEB_URL")
        URI url = new URI(webUrl)

        componentClient = new ComponentClient(url, username, password)
    }

    /**
     * Set the password as a secure property at the component process level
     * @param password
     */
    public void setProcessProperty(String propVal, String propName, String componentProcessId, boolean isSecure) {
        componentClient.setComponentProcessRequestProperty(componentProcessId, propName, propVal, isSecure)
    }
}