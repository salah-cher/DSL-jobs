//set Active Directory security realm

import jenkins.model.*
import hudson.security.*
import hudson.plugins.active_directory.*
import java.util.logging.Logger

def logger = Logger.getLogger("")
logger.info("Start of " + this.class.getName())

try {
    def instance = Jenkins.getInstance()

    String domain = 'credentials.io'
    String site = ''
    String server = 'srv01.credentials.io:3268,srv02.credentials.io:3268'
    String bindName = 'CN=svcacc,OU=Services,OU=Accounts,DC=credentials,DC=io'
    String bindPassword = 'dummypassword'

    ad_realm = new ActiveDirectorySecurityRealm(domain, site, bindName, bindPassword, server)
    instance.setSecurityRealm(ad_realm)
    instance.save()
} catch(Exception ex) {
    logger.info("Catching the exception: " + ex)
}

logger.info("End of " + this.class.getName())
