import com.urbancode.air.AirPluginTool;
import com.urbancode.air.plugin.cyberark.UCDRestHelper;

final airTool = new AirPluginTool(args[0], args[1])
final def props = airTool.getStepProperties()

def thePath = props['path'] ?: "/opt/CARKaim/sdk/clipasswordsdk"
def theAppID = props['appid'] ?: ""
def theSafe = props['safe'] ?: ""
def theFolder = props['folder'] ?: ""
def theObject = props['object'] ?: ""

def theOutputPass = props['outputpass'] ?: ""
def theOutputUser = props['outputuser'] ?: ""
def theOutputAddr = props['outputaddr'] ?: ""

thePath = thePath.trim()
theAppID = theAppID.trim()
theSafe = theSafe.trim()
theFolder = theFolder.trim()
theObject = theObject.trim()

theOutputPass = theOutputPass.trim()
theOutputUser = theOutputUser.trim()
theOutputAddr = theOutputAddr.trim()

def command="${thePath} getPassword -p AppDescs.AppID=${theAppID} -p Query=Safe=${theSafe};Folder=${theFolder};Object=${theObject} -o Password,PassProps.Address,PassProps.UserName"

Process process = command.execute()
def out = new StringBuffer()
def err = new StringBuffer()
process.consumeProcessOutput( out, err )
process.waitFor()

exitValue = process.exitValue();

if( err.size() > 0 ) println("Error: "+err)

if(!exitValue && exitValue!=0)
	System.exit(exitValue);

String[] results = out.toString().split(",")

UCDRestHelper ucdHelper = new UCDRestHelper()
def requestId = props['requestId']
boolean isComponent = props['processId'] ? false : true

ucdHelper.setProcessRequestProp(requestId, theOutputPass, results[0].trim(), true, isComponent)
if (theOutputAddr?.trim()) {
	ucdHelper.setProcessRequestProp(requestId, theOutputAddr, results[1].trim(), false, isComponent)
}
if (theOutputUser?.trim()) {
	ucdHelper.setProcessRequestProp(requestId, theOutputUser, results[2].trim(), false, isComponent)
}
System.exit(exitValue);
