# urbancode-conjur-aim
A plugin which allows UrbanCode Deploy to get credentials from EPV via AIM, and to get secrets from Conjur for setting up a CI/CD workflow


## Compiling Source
`gradle`

## Installation
1. Login to UrbanCode Deploy web portal and go to "Settings > Automation Plugins"
2. Click "Load Plugin".   Select the complied plugin file and click "Submit"
3. A new plugin named "CyberArk" is installed

## Usage
The plugin can be used in process designer of "Process" & "Components", by dragging from the left menu area under "Security > CyberArk

### AIM
#### Get Password from Vault
This function allows credentials to be retrieve from Vault server via AIM CP.

##### Input Fields
| Name | Description | Example |
| ---- | ----------- | ------- |
| Name | Name of the step | Get Password from Vault |
| Path | Absolute file path to clipasswordsdk |  /opt/CARKaim/sdk/clipasswordsdk |
| Safe | Safe of the credential stored | DevOps |
| Folder | Folder of the credential stored | Root | 
| Object | Name of the credential object | Website-Conjur-httpseval.conjur.org-cf-spring-app-01 |
| AppID  | AppID defined in PVWA  | UCD |
| Output Property - Password | Process Request Property for storing retrieved credential | CyberArk/Vault/Password |
| Output Property - User Name | Process Request Property for storing retrieved username | CyberArk/Vault/User |
| Output Property - Address | Process Request Property for storing retrieved address | CyberArk/Vault/Address |

##### Output Fields
| Name |  Description |
| ---- | ----------- | 
| <specified by "Output Property - Password", e.g. CyberArk/Vault/Password> 	| Value of the credential | 
| <specified by "Output Property - User Name", e.g. CyberArk/Vault/User>	| User Name of the credential | 
| <specified by "Output Property - Address", e.g. CyberArk/Vault/Address>	| Address of the credential |

### Conjur
#### Authenticate Conjur
This step gets a [short-lived access token](https://www.conjur.org/reference/cryptography.html#authentication-tokens), which can be used to authenticate requests to (most of) the rest of the Conjur API. A client can obtain an access token by presenting a valid login name and API key.

##### Input Fields
| Name | Description | Example |
| ---- | ----------- | ------- |
| Name | Name of the step | Authenticate Conjur |
| Account | Organization account name	|  | 
| Login	| Host name for authenicating Conjur | cf-spring-app-01 | 
| API Key | API Key for authenicating Conjur	|  | 
| Conjur URL | URL of Conjur cluster | https://eval.conjur.org |
| Proxy | Proxy address for calling Conjur REST API. Leave it blank if direct connection is allowed |  ipv4.124.244.113.228.hybrid-web.global.blackspider.com:80 |
| Output Property - Access Token | Process Request Property for storing the return access token | CyberArk/Conjur/AccessToken |


##### Output Fields
| Name |  Description |
| ---- | ----------- | 
| <specified by "Output Property - Access Token", e.g. CyberArk/Conjur/AccessToken>	| [Short-lived access token](https://www.conjur.org/reference/cryptography.html#authentication-tokens) | 

#### Get Variable from Conjur

##### Input Fields
| Name | Description | Example |
| ---- | ----------- | ------- |
| Name | Name of the step | Get Variable from Conjur |
| Account | Organization account name	 | |
| Access Token	| Short-lived access token	| |
| Variable ID	| ID of the variable |	db/prod/pws/db01/serviceA |
| Conjur URL |	URL of Conjur cluster |	https://eval.conjur.org |
| Proxy	| Proxy address for calling Conjur REST API. Leave it blank if direct connection is allowed | ipv4.124.244.113.228.hybrid-web.global.blackspider.com:80 |
| Output Property - Variable | Process Request Property for storing the value of the secret | CyberArk/Conjur/Variable |



##### Output Fields
| Name |  Description |
| ---- | ----------- | 
| <specified by "Output Property - Variable", e.g. CyberArk/Conjur/Variable>	| Value of the secret | 
