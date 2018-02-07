
import com.sun.org.apache.xalan.internal.xsltc.compiler.Import
import com.urbancode.air.AirPluginTool;
import com.urbancode.air.plugin.cyberark.UCDRestHelper;

final airTool = new AirPluginTool(args[0], args[1])

final def props = airTool.getStepProperties()

def thePath = props['path'] ?: "/opt/CARKaim/sdk/clipasswordsdk"
def theAppID = props['appid'] ?: ""
def theSafe = props['safe'] ?: ""
def theFolder = props['folder'] ?: ""
def theObject = props['object'] ?: ""
def isSecure = Boolean.valueOf(props['isSecure'])

thePath = thePath.trim()
theAppID = theAppID.trim()
theSafe = theSafe.trim()
theFolder = theFolder.trim()
theObject = theObject.trim()

println("Get Password from Vault - Start")

def command="${thePath} getPassword -p AppDescs.AppID=${theAppID} -p Query=Safe=${theSafe};Folder=${theFolder};Object=${theObject} -o Password,PassProps.Address,PassProps.UserName"

Process process = command.execute()
def out = new StringBuffer()
def err = new StringBuffer()
process.consumeProcessOutput( out, err )
process.waitFor()

exitValue = process.exitValue();

if(!exitValue)
	System.exit(exitValue);

if( out.size() > 0 ) println("Credential retrieved")
if( err.size() > 0 ) println("Error: "+err)

println("Get Password from Vault - Stop")

String[] results = out.toString().split(",")

if (isSecure) {
    UCDRestHelper ucdHelper = new UCDRestHelper()
    def componentProcessId = props['componentProcessId']
    ucdHelper.setProcessProperty(results[0], "CyberArk/password", componentProcessId, true)
    ucdHelper.setProcessProperty(results[1], "CyberArk/address", componentProcessId, false)
    ucdHelper.setProcessProperty(results[2], "CyberArk/username", componentProcessId, false)
}
else {
    airTool.setOutputProperty("Password", results[0].trim())
    airTool.setOutputProperty("Address", results[1].trim())
    airTool.setOutputProperty("Username", results[2].trim())

    airTool.storeOutputProperties()
}