INSTALLATION GUIDE
==================

PRECONDITIONS:
1. Ant 1.6.5+.
2. copy junit.jar into your $ANT_HOME/lib directory.
3. Tomcat 5.0.x+ installed,
4. SMTP server around.
5. MySQL DB installed

PROCEDURE:
1. Create empty schema in your MySQL DB
2. copy build.properties to executive-build.properties
3. modify entries in executive-build.properties to match your environment
4. execute ant setup-db (that will create tables and data in your DB)
5. execute ant deploy
6. start tomcat

