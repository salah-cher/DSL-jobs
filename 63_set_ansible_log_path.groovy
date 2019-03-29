// Configure Ansible environment variable for log path, see CSESRE-1055

import jenkins.model.*
import hudson.slaves.EnvironmentVariablesNodeProperty
import java.util.logging.Logger

def logger = Logger.getLogger("")
logger.info("Start of " + this.class.getName())

try {
    def instance = Jenkins.getInstance()

    globalNodeProperties = instance.getGlobalNodeProperties()
    envVarsNodePropertyList = globalNodeProperties.getAll(EnvironmentVariablesNodeProperty.class)

    newEnvVarsNodeProperty = null
    envVars = null

    if (envVarsNodePropertyList == null || envVarsNodePropertyList.size() == 0 ) {
      newEnvVarsNodeProperty = new EnvironmentVariablesNodeProperty();
      globalNodeProperties.add(newEnvVarsNodeProperty)
      envVars = newEnvVarsNodeProperty.getEnvVars()
    } else {
      envVars = envVarsNodePropertyList.get(0).getEnvVars()
    }

    envVars.put("ANSIBLE_LOG_PATH", "/var/log/jenkins/ansible.log")

    instance.save()

} catch(Exception ex) {
    logger.info("Catching the exception: " + ex)
}

logger.info("End of " + this.class.getName())
