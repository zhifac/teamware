################################################################################
####  SHORT DEVELOPER GUIDE - HOW TO START WITH TEAMWARE                   #####
################################################################################
IMPORTANT NOTES:
1: teamware lives svn at
   http://svn.code.sf.net/p/gate/code/teamware/trunk
2: teamware works with mysql database, so you need to have one locally installed
3. teamware requires a Java 6 JDK to build and run.
4. teamware uses ant version 1.8.x for build, so you need to have ant locally
   installed
5. teamware uses perl during build procedure, so you need to have PERL
   installed (this is especially important in MS Windows environments)
6. teamware can be deployed as a number of instances, which is specified in ant
   with ant -Dinstance.name=[INSTANCE] some-ant-target Since, each instance
   should have its specific configuration and resources in
   teamware/installations/[INSTANCE] folder, otherwise resources from default
   instances will be used and that is cloud-tw.  The default instance.name is
   "teamware", which does not correspond to a directory under installations, so
   it will use the settings for cloud-tw.  If you want to use an
   instance-specific setup that is not under installations/something then you
   need to specify -Dinstance.project.dir=/path/to/instance/directory in
   addition to the instance.name setting.

The rest of this guide will assume that cloud-tw instance has been used.

You can build a teamware installer package by performing:
ant -propertyfile install.properties dist
from a newly checked out teamware source code and then run it with:
java -jar dist/install.jar
which will run a wizard that will assist you in setting up a teamware instance.

For development purposes, you can use the steps below:

1. make a local copy of the build.properties file and name it as
   ${instance.name}-build.properties, e.g cloud-tw-build.properties.
   Change entries according to your environment, e.g mysql db user name or
   password
   
2. For the first time installation, database needs to be created and populated.
   Please do fresh checkout from:
svn co http://svn.code.sf.net/p/gate/code/teamware/trunk
(or .../gate/teamware/branches/X if you are working on a branch)

Execute the following from teamware root dir:
ant -Dinstance.name=cloud-tw install

4. In development, when you make some changes and want to call deploy of the all teamware components:
Execute the following from teamware root dir:
ant -Dinstance.name=cloud-tw clean undeploy-all deploy-all
Alternatively, if you are developing the specific component, e.g docservice, or
executive do the following.
Execute the following from component dir, for example:
cd executive
ant -Dinstance.name=cloud-tw clean undeploy deploy
The exceptions are JWS applications where you would need to to do the following:
cd annotator-gui
ant -Dinstance.name=cloud-tw clean war deploy
     
5. start your tomcat6 which lives at teamware/tomcat6, 
cd tomcat6/bin
./catalina.sh run (under linux)
catalina run (under windows)

6. You can access application at: 
http://localhost:8080/cloud-tw/executive



