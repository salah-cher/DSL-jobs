import jenkins.model.*
import java.util.logging.Logger

def logger = Logger.getLogger("")
logger.info("Start of " + this.class.getName())

try {
    def instance = Jenkins.getInstance()

    String name = "proxy-ycm-stg.ycommerce.ycsdev.io"
    int port = 3128
    String userName = ""
    String password = ""
    String noProxyHost = "127.0.0.1"

    def pc = new hudson.ProxyConfiguration(name, port, userName, password, noProxyHost)
    instance.proxy = pc
    pc.save()
    println "Proxy settings updated!"

} catch(Exception ex) {
    logger.info("Catching the exception: " + ex)
}

logger.info("End of " + this.class.getName())
