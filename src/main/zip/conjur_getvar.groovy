
import com.urbancode.air.AirPluginTool;

final airTool = new AirPluginTool(args[0], args[1])

final def props = airTool.getStepProperties()

def theAccount = props['account'] ?: ""
def theUrl = props['url'] ?: ""
def theVarID = props['varid'] ?: ""
def theAccessToken = props['accesstoken'] ?: ""
def theProxy = props['proxy'] ?: ""

theAccount = theAccount.trim()
theUrl = theUrl.trim()
theVarID = theVarID.trim()
theAccessToken = theAccessToken.trim()
theProxy = theProxy.trim()


println("Conjur Get Var ID - Start")


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

command += theUrl+'/secrets/'+theAccount+'/variable/'+theVarID

Process process = command.execute()
def out = new StringBuffer()
def err = new StringBuffer()
process.consumeProcessOutput( out, err )
process.waitFor()



if( out.size() > 0 ) println("Variable OK")
if( err.size() > 0 ) println("Error: "+err)


println("Conjur Get Var - Stop")

airTool.setOutputProperty("Variable", out.toString());
airTool.storeOutputProperties();


exitValue = process.exitValue();


if(!exitValue)
	System.exit(exitValue);

