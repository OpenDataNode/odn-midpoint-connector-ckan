Midpoint connector to CKAN
---------
This connector should "catch" the organization creation event and create organization and organization user in ODN CKAN.

Deploying and Testing
----------
1. Compiled jar file copy to midpoint lib folder, see midpoint documentation (something like {MIDPOINT_HOME}/lib, there should be another connector jars)

2. After midpoint restart, the connector will be detected automatically (see Configuration -> Repository objects -> Connector)

3. Add new resource, where you configure CKAN api url and API KEY (optional)

4. If configured correctly and you use Test connection option on the resource, all status checks should be green

Notes
----------
The original groovy script were mooved to src/main/groovy folder, but can be deleted

Actual state
----------
This connector is in deployable state, but its not functioning as intended. Its not "catching" organization creation events. So I was not able to continue with implementing the functionality to connect and create organization and user in CKAN.