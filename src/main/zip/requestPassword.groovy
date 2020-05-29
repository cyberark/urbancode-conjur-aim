import com.urbancode.air.AirPluginTool
import com.urbancode.air.plugin.cyberark.AIMRestClient
import com.urbancode.air.plugin.cyberark.UCDRestHelper

AirPluginTool apTool = new AirPluginTool(this.args[0], this.args[1])
def props = apTool.getStepProperties()
String serverUrl = props['serverUrl']
String appId = props['appId']
String safe = props['safe']
String folder = props['folder']
String object = props['object']
String requestId = props['requestId']
String propPrefix = props['propPrefix']
boolean isComponent = props['processId'] ? false : true // Existing property for generic processes

String keyStore = props['keyFile']
String keyPass = props['keyPass']
String keyType = props['keyType']
boolean trustCerts = Boolean.valueOf(props['trustCerts'])
String jsseDebugLevel = props['jsseDebugLevel']?.trim()

String isNtlm = Boolean.valueOf(props['isNtlm'])
String domain = props['domain']
String username = props['username']
String pass = props['pass']

/* Configure JSSE debugging if specified */
if (jsseDebugLevel) {
    System.setProperty("javax.net.debug", jsseDebugLevel)
}

AIMRestClient aimClient = new AIMRestClient(serverUrl, trustCerts, keyType, keyStore, keyPass, isNtlm, domain, username, pass)
UCDRestHelper ucdHelper = new UCDRestHelper()

try {
    def response = aimClient.requestPass(appId, safe, folder, object)
    String password = response.get("Content")
    String username = response.get("UserName")
    String address = response.get("Address")

    /* Set secure password property, but insecure username and address properties */
    ucdHelper.setProcessRequestProp(requestId, propPrefix + "/password", password, true, isComponent)

    if (username) {
        ucdHelper.setProcessRequestProp(requestId, propPrefix + "/username", username, false, isComponent)
    }
    if (address) {
        ucdHelper.setProcessRequestProp(requestId, propPrefix + "/address", address, false, isComponent)
    }
}
finally {
    aimClient.cleanUp()
}