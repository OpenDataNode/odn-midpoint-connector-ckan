/*
 *
 * Copyright (c) 2013 ForgeRock Inc. All Rights Reserved
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://www.opensource.org/licenses/cddl1.php or
 * OpenIDM/legal/CDDLv1.0.txt
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at OpenIDM/legal/CDDLv1.0.txt.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted 2010 [name of copyright owner]"
 *
 * @author Gael Allioux <gael.allioux@forgerock.com>
 */

// Parameters:
// The connector sends us the following:
// connection : handler to the REST Client
// (see: http://groovy.codehaus.org/modules/http-builder/apidocs/groovyx/net/http/RESTClient.html)
// configuration : handler to the connector's configuration object
// action: String correponding to the action ("CREATE" here)
// log: a handler to the Log facility
// objectClass: a String describing the Object class (__ACCOUNT__ / __GROUP__ / other)
// id: The entry identifier (OpenICF "Name" atribute. (most often matches the uid) - IF action = CREATE
// attributes: an Attribute Map, containg the <String> attribute name as a key
// and the <List> attribute value(s) as value.
// password: password string, clear text
// options: a handler to the OperationOptions Map
//
// Returns: Create must return UID.

log.info("Entering "+action+" Script");

// We can use Groovy template engine to generate the JSON body
def engine = new groovy.text.SimpleTemplateEngine();

def userTemplate = '''
{
  "id": "$id",
  "name": "$name",
  "email": "$email",
  "password": "$password",
  "fullname": "$fullName",
  "about": "$about",
  "openid": "$openid"
 }
 '''

def groupTemplate = '''
{
    "_id": "$id",
    "name": "$name",
	"title" : "$title"
}
'''

def roleTemplate = '''
{
    "id": "$id",
    "object": "$objectName",
	"object_type" : "$objectType",
	"capacity" : "$capacity"
}
'''

switch ( objectClass ) {
	case "__ACCOUNT__":
		if (attributes.get("organization") != null){
			orgCap = attributes.get("organization");

			def binding = [
				objectName: (attributes.get("_id") == null)? "": attributes.get("_id").get(0),
				objectType: "user",
				id: orgCap.tokenize('%')[0],
				capacity : orgCap.tokenize('%')[1]
			];

			template =engine.createTemplate(roleTemplate).make(binding).toString();
			response = connection.post(path : '/api/action/member_create', body : template);
		} else {

			def binding = [
				name: (attributes.get("_id") == null)? "": attributes.get("_id").get(0),
				email: (attributes.get("email") == null)? "": attributes.get("email").get(0),
				password: "weDoNotNeedPassword",
				id: (attributes.get("_id") == null)? "": attributes.get("_id").get(0),
				fullName: (attributes.get("fullName") == null)? "fullName": attributes.get("fullName").get(0),
				about: (attributes.get("about") == null)? "": attributes.get("about").get(0),
				openid: (attributes.get("openid") == null)? "": attributes.get("openid").get(0)
			];

			template =engine.createTemplate(userTemplate).make(binding).toString();
			System.out.println(template);
			response = connection.post(path : '/api/action/user_create', body : template);
		}
		break

	case "__GROUP__":

		def binding = [
			name: (attributes.get("_id") == null)? "": attributes.get("_id").get(0),
			title: (attributes.get("title") == null)? "": attributes.get("title").get(0),
			id: (attributes.get("_id") == null)? "": attributes.get("_id").get(0),
		];

		template =engine.createTemplate(groupTemplate).make(binding).toString();
		response = connection.post(path : '/api/action/organization_create', body : template);
		break
}
return id;
