# idea-remotegroovyconsole-server-portlet
Liferay portlet, incapsulating server for Remote Groovy Console IntelliJ IDEA plugin (https://plugins.jetbrains.com/plugin/5373?pr=)

It allows you to execute Groovy scripts from IntelliJ IDEA remotely in Lifeay Portal.
GroovyConsoleServer does not support execution of selected part of script with attached imports - whole script is
always executed.

# Configuration
Socket serveer is started automatically after deployment. Configuration can be changed in applicationContext.xml:
 
 ```xml
 <bean class="com.nevermindr.groovyconsole.portlet.spring.GroovyShellServiceBean"
          p:port="7777"
          p:host="localhost"
          p:launchAtStart="true"/>
 ```

#TODO:
 - create control panel portlet for configuration, start/stop functions
 - check lr6.0 support
 - maybe include liferay core classes in groovy consoles' default imports