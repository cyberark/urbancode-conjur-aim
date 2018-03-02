import com.urbancode.air.AirPluginTool;
import com.urbancode.air.plugin.cyberark.UCDRestHelper;

final airTool = new AirPluginTool(args[0], args[1])
final def props = airTool.getStepProperties()

def theAccount = props['account'] ?: ""
def theLogin = props['login'] ?: ""
def theApikey = props['apikey'] ?: ""
def theUrl = props['url'] ?: ""
def theProxy = props['proxy'] ?: ""
def theOutputToken = props['outputtoken'] ?: ""

theAccount = theAccount.trim()
theAccount= URLEncoder.encode(theAccount, "UTF-8")

theLogin = theLogin.trim()
theLogin= URLEncoder.encode(theLogin, "UTF-8")

theApikey = theApikey.trim()
theUrl = theUrl.trim()
theProxy = theProxy.trim()

def command="curl "
if (!theProxy.equals("")) {
	command = command + " -x " + theProxy + " "
}
command = command + " -k -s --request POST --data "+theApikey+" "+ theUrl +"/authn/"+theAccount+"/host%2F"+theLogin+"/authenticate"

Process process = command.execute()
def out = new StringBuffer()
def err = new StringBuffer()
process.consumeProcessOutput( out, err )
process.waitFor()

if( err.size() > 0 ) println("Error: "+err)

theToken = out.toString().trim()
theToken = theToken.bytes.encodeBase64().toString().trim()

UCDRestHelper ucdHelper = new UCDRestHelper()
def requestId = props['requestId']
boolean isComponent = props['processId'] ? false : true

ucdHelper.setProcessRequestProp(requestId, theOutputToken, theToken.trim(), true, isComponent)

exitValue = process.exitValue();

if(!exitValue)
	System.exit(exitValue);
