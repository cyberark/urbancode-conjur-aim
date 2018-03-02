
import com.urbancode.air.AirPluginTool;

final airTool = new AirPluginTool(args[0], args[1])

final def props = airTool.getStepProperties()

def theAccount = props['account'] ?: ""
def theLogin = props['login'] ?: ""
def theApikey = props['apikey'] ?: ""
def theUrl = props['url'] ?: ""
def theProxy = props['proxy'] ?: ""

theAccount = theAccount.trim()
theLogin = theLogin.trim()
theApikey = theApikey.trim()
theUrl = theUrl.trim()
theProxy = theProxy.trim()

println("Conjur Auth - Start")

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

if( out.size() > 0 ) println("Command 1 OK")
if( err.size() > 0 ) println("Command 1 Error: "+err)

theToken = out.toString().trim()
theToken = theToken.bytes.encodeBase64().toString().trim()

println("Conjur Auth - Stop")

airTool.setOutputProperty("AccessToken", theToken);
airTool.storeOutputProperties();

exitValue = process.exitValue();

if(!exitValue)
	System.exit(exitValue);
