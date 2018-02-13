package com.urbancode.air.plugin.cyberark

import com.urbancode.ud.client.ComponentClient
import com.urbancode.ud.client.ProcessClient

class UCDRestHelper {
    ComponentClient componentClient
    ProcessClient processClient

    public UCDRestHelper() {
        String username = "PasswordIsAuthToken"
        String password = String.format("{\"token\": \"%s\"}", System.getenv("AUTH_TOKEN"))
        String webUrl = System.getenv("AH_WEB_URL")
        URI url = new URI(webUrl)

        componentClient = new ComponentClient(url, username, password)
        processClient = new ProcessClient(url, username, password)
    }

    /**
     * Set the password as a secure property at the component process level
     * @param password
     */
    public void setProcessRequestProp (
        String requestId,
        String propName,
        String propVal,
        boolean isSecure,
        boolean isComponent)
    {
        if (isComponent) {
            println("[Action] Set property '${propName}' on component process request '${requestId}'.")
            componentClient.setComponentProcessRequestProperty(requestId, propName, propVal, isSecure)
        }
        else {
            println("[Action] Set property '${propName}' on process request '${requestId}'.")
            processClient.setProcessRequestProperty(requestId, propName, propVal, isSecure)
        }
    }
}