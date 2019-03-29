import jenkins.model.*
import java.util.logging.Logger

def logger = Logger.getLogger("")
logger.info("Start of " + this.class.getName())

try {
    def plugins = [ "cloudbees-folder",
                    "credentials-binding",
                    "email-ext",
                    "git",
                    "ldap",
                    "mailer",
                    "matrix-auth",
                    "pam-auth",
                    "pipeline-stage-view",
                    "ssh-slaves",
                    "timestamper",
                    "workflow-aggregator",
                    "ws-cleanup",
                    "job-dsl",
                    "greenballs",
                    "chucknorris",
                    "ansible",
                    "ansicolor",
                    "rebuild",
                    "active-directory",
                    "role-strategy",
                    "mask-passwords",
                    "extensible-choice-parameter",
                    "uno-choice",
                    "antisamy-markup-formatter",
                    "script-security"]

    def installed = false
    def initialized = false

    logger.info("" + plugins)

    def instance = Jenkins.getInstance()
    def pm = instance.getPluginManager()
    def uc = instance.getUpdateCenter()

    plugins.each {
      logger.info("Checking " + it)
      if (!pm.getPlugin(it)) {
        logger.info("Looking UpdateCenter for " + it)
        if (!initialized) {
          uc.updateAllSites()
          initialized = true
        }
        def plugin = uc.getPlugin(it)
        if (plugin) {
          logger.info("Installing " + it)
            def installFuture = plugin.deploy()
          while(!installFuture.isDone()) {
            logger.info("Waiting for plugin install: " + it)
            sleep(3000)
          }
          installed = true
        }
      }
    }

    if (installed) {
      logger.info("Plugins installed, initializing a restart!")
      instance.save()
      instance.restart()
    }
} catch(Exception ex) {
    logger.info("Catching the exception: " + ex)
}

logger.info("End of " + this.class.getName())
