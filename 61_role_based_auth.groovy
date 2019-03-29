// Enable role-based authentication
// Based on: https://github.com/Accenture/adop-jenkins/blob/master/resources/init.groovy.d/role_based_auth.groovy
//           https://github.com/Accenture/adop-platform-management/blob/master/projects/groovy/acl_admin.groovy

import hudson.*
import hudson.model.*
import hudson.security.*
import jenkins.*
import jenkins.model.*
import java.util.*
import com.michelin.cio.hudson.plugins.rolestrategy.*
import java.lang.reflect.*
import java.util.logging.Logger

def logger = Logger.getLogger("")
logger.info("Start of " + this.class.getName())

try {
    // Role name definitions
    def globalRoleRead = "read"
    def globalRoleAdmin = "admin"

    // Group definitions
    def groupAuthenticated = "authenticated" // This comes from Jenkins
    def groupSRE = "ACL-STG-CMZJENKINS-SRE"

    // Use Jenkins via instance var
    def instance = Jenkins.getInstance()

    // Set new authentication strategy
    RoleBasedAuthorizationStrategy roleBasedAuthenticationStrategy = new RoleBasedAuthorizationStrategy()
    instance.setAuthorizationStrategy(roleBasedAuthenticationStrategy)

    // Make the method assignRole accessible
    Method assignRoleMethod = RoleBasedAuthorizationStrategy.class.getDeclaredMethod("assignRole", String.class, Role.class, String.class);
    assignRoleMethod.setAccessible(true);


    // ***************** Role definition BEGIN *****************
    // Read access for authenticated users - can view all, and not modify
    Set<Permission> authenticatedPermissions = new HashSet<Permission>();
    authenticatedPermissions.add(Permission.fromId("hudson.model.Hudson.Read"));
    authenticatedPermissions.add(Permission.fromId("hudson.model.Item.Read"));
    // ***************** Role definition END *******************


    // ***************** Role definition BEGIN *****************
    // Create admin set of permissions - enable everything
    Set<Permission> adminPermissions = new HashSet<Permission>();
    // "Overall" permissions
    adminPermissions.add(Permission.fromId("hudson.model.Hudson.Administer"));
    adminPermissions.add(Permission.fromId("hudson.model.Hudson.Read"));
    // "Credentials" permissions
    adminPermissions.add(Permission.fromId("com.cloudbees.plugins.credentials.CredentialsProvider.Create"));
    adminPermissions.add(Permission.fromId("com.cloudbees.plugins.credentials.CredentialsProvider.Delete"));
    adminPermissions.add(Permission.fromId("com.cloudbees.plugins.credentials.CredentialsProvider.ManageDomains"));
    adminPermissions.add(Permission.fromId("com.cloudbees.plugins.credentials.CredentialsProvider.Update"));
    adminPermissions.add(Permission.fromId("com.cloudbees.plugins.credentials.CredentialsProvider.View"));
    // "Agent" permissions
    adminPermissions.add(Permission.fromId("hudson.model.Computer.Build"));
    adminPermissions.add(Permission.fromId("hudson.model.Computer.Configure"));
    adminPermissions.add(Permission.fromId("hudson.model.Computer.Connect"));
    adminPermissions.add(Permission.fromId("hudson.model.Computer.Create"));
    adminPermissions.add(Permission.fromId("hudson.model.Computer.Delete"));
    adminPermissions.add(Permission.fromId("hudson.model.Computer.Disconnect"));
    adminPermissions.add(Permission.fromId("hudson.model.Computer.Provision"));
    // "Job" permissions
    adminPermissions.add(Permission.fromId("hudson.model.Item.Build"));
    adminPermissions.add(Permission.fromId("hudson.model.Item.Cancel"));
    adminPermissions.add(Permission.fromId("hudson.model.Item.Configure"));
    adminPermissions.add(Permission.fromId("hudson.model.Item.Create"));
    adminPermissions.add(Permission.fromId("hudson.model.Item.Delete"));
    adminPermissions.add(Permission.fromId("hudson.model.Item.Discover"));
    adminPermissions.add(Permission.fromId("hudson.model.Item.Move"));
    adminPermissions.add(Permission.fromId("hudson.model.Item.Read"));
    adminPermissions.add(Permission.fromId("hudson.model.Item.Workspace"));
    // "Run" permissions
    adminPermissions.add(Permission.fromId("hudson.model.Run.Delete"));
    adminPermissions.add(Permission.fromId("hudson.model.Run.Replay"));
    adminPermissions.add(Permission.fromId("hudson.model.Run.Update"));
    // "View" Permissions
    adminPermissions.add(Permission.fromId("hudson.model.View.Configure"));
    adminPermissions.add(Permission.fromId("hudson.model.View.Create"));
    adminPermissions.add(Permission.fromId("hudson.model.View.Delete"));
    adminPermissions.add(Permission.fromId("hudson.model.View.Read"));
    // "SCM" Permissions
    adminPermissions.add(Permission.fromId("hudson.scm.SCM.Tag"));
    // ***************** Role definition END *******************


    // ***************** Role definition BEGIN *****************
    // Project access role - enable everything in a project (folder)
    Set<Permission> folderPermissions = new HashSet<Permission>();
    // "Credentials" permissions
    folderPermissions.add(Permission.fromId("com.cloudbees.plugins.credentials.CredentialsProvider.Create"));
    folderPermissions.add(Permission.fromId("com.cloudbees.plugins.credentials.CredentialsProvider.Delete"));
    folderPermissions.add(Permission.fromId("com.cloudbees.plugins.credentials.CredentialsProvider.ManageDomains"));
    folderPermissions.add(Permission.fromId("com.cloudbees.plugins.credentials.CredentialsProvider.Update"));
    folderPermissions.add(Permission.fromId("com.cloudbees.plugins.credentials.CredentialsProvider.View"));
    // "Job" permissions
    folderPermissions.add(Permission.fromId("hudson.model.Item.Build"));
    folderPermissions.add(Permission.fromId("hudson.model.Item.Cancel"));
    folderPermissions.add(Permission.fromId("hudson.model.Item.Configure"));
    folderPermissions.add(Permission.fromId("hudson.model.Item.Create"));
    folderPermissions.add(Permission.fromId("hudson.model.Item.Delete"));
    folderPermissions.add(Permission.fromId("hudson.model.Item.Discover"));
    folderPermissions.add(Permission.fromId("hudson.model.Item.Move"));
    folderPermissions.add(Permission.fromId("hudson.model.Item.Read"));
    folderPermissions.add(Permission.fromId("hudson.model.Item.Workspace"));
    // "Run" permissions
    folderPermissions.add(Permission.fromId("hudson.model.Run.Delete"));
    folderPermissions.add(Permission.fromId("hudson.model.Run.Replay"));
    folderPermissions.add(Permission.fromId("hudson.model.Run.Update"));
    // "SCM" Permissions
    folderPermissions.add(Permission.fromId("hudson.scm.SCM.Tag"));
    // ***************** Role definition END *******************


    // Create the authenticatedRole and assign it to all authenticated users
    Role authenticatedRole = new Role(globalRoleRead, authenticatedPermissions);
    roleBasedAuthenticationStrategy.addRole(RoleBasedAuthorizationStrategy.GLOBAL, authenticatedRole);
    roleBasedAuthenticationStrategy.assignRole(RoleBasedAuthorizationStrategy.GLOBAL, authenticatedRole, groupAuthenticated);
    println "Read role created OK"


    // Create the admin Role and assign to SRE AD group
    Role adminRole = new Role(globalRoleAdmin, adminPermissions);
    roleBasedAuthenticationStrategy.addRole(RoleBasedAuthorizationStrategy.GLOBAL, adminRole);
    roleBasedAuthenticationStrategy.assignRole(RoleBasedAuthorizationStrategy.GLOBAL, adminRole, groupSRE);
    println "Admin role created OK"


    // Add project-based roles
    Role folderRole;

    folderRole = new Role("qa", "^QA.*", folderPermissions);
    roleBasedAuthenticationStrategy.addRole(RoleBasedAuthorizationStrategy.PROJECT, folderRole);
    roleBasedAuthenticationStrategy.assignRole(RoleBasedAuthorizationStrategy.PROJECT, folderRole, "ACL-STG-CMZJENKINS-QA");
    println "Created project role 'qa'"

    // Save the state
    instance.save()
} catch(Exception ex) {
    logger.info("Catching the exception: " + ex)
}

logger.info("End of " + this.class.getName())
