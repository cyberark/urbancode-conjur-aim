import com.urbancode.air.AirPluginTool;
import com.urbancode.air.plugin.cyberark.UCDRestHelper;

final airTool = new AirPluginTool(args[0], args[1])

final def props = airTool.getStepProperties()

def theAccount = props['account'] ?: ""
def theUrl = props['url'] ?: ""
def theVarID = props['varid'] ?: ""
def theAccessToken = props['accesstoken'] ?: ""
def theProxy = props['proxy'] ?: ""
def apiVer = props['apiVer'] ?: "v5"

theAccount = theAccount.trim()
theAccount= URLEncoder.encode(theAccount, "UTF-8")

theUrl = theUrl.trim()
theVarID = theVarID.trim()
theAccessToken = theAccessToken.trim()
theProxy = theProxy.trim()
apiVer = apiVer.trim()

def theOutputVar = props['outputvar'] ?: ""

def command = ["curl"]
command+="-H"
command+="Authorization: Token token=\""+theAccessToken+"\""
if (!theProxy.equals("")) {
        command += "-x"
	command += theProxy
}
command += "-k"
command += "-s"
command += "--request"
command += "GET"

if (apiVer == "v4") {
	theVarIDEncoded = URLEncoder.encode(theVarID, "UTF-8")
	command += theUrl + '/api/variables/' + theVarIDEncoded + '/value'
}
else {
	command += theUrl + '/secrets/' + theAccount + '/variable/' + theVarID
}
Process process = command.execute()
def out = new StringBuffer()
def err = new StringBuffer()
process.consumeProcessOutput( out, err )
process.waitFor()

if( err.size() > 0 ) println("Error: "+err)

UCDRestHelper ucdHelper = new UCDRestHelper()
def requestId = props['requestId']
boolean isComponent = props['processId'] ? false : true

ucdHelper.setProcessRequestProp(requestId, theOutputVar, out.toString().trim(), true, isComponent)

exitValue = process.exitValue();

if(!exitValue)
	System.exit(exitValue);
