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
//
// action: String correponding to the action (UPDATE/ADD_ATTRIBUTE_VALUES/REMOVE_ATTRIBUTE_VALUES)
//   - UPDATE : For each input attribute, replace all of the current values of that attribute
//     in the target object with the values of that attribute.
//   - ADD_ATTRIBUTE_VALUES: For each attribute that the input set contains, add to the current values
//     of that attribute in the target object all of the values of that attribute in the input set.
//   - REMOVE_ATTRIBUTE_VALUES: For each attribute that the input set contains, remove from the current values
//     of that attribute in the target object any value that matches one of the values of the attribute from the input set.

// log: a handler to the Log facility
// objectClass: a String describing the Object class (__ACCOUNT__ / __GROUP__ / other)
// uid: a String representing the entry uid
// attributes: an Attribute Map, containg the <String> attribute name as a key
// and the <List> attribute value(s) as value.
// password: password string, clear text (only for UPDATE)
// options: a handler to the OperationOptions Map

log.info("Entering "+action+" Script");

// we assume UPDATE action

// We can use Groovy template engine to generate the JSON body
def engine = new groovy.text.SimpleTemplateEngine();

def userTemplate = '''
{
  "_id": "$id",
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
       orgCap = attributes.get("organization").get(0);
	   def binding = [
        objectName: uid,
		objectType : "user",
		id: orgCap.tokenize('%')[0],
		capacity : orgCap.tokenize('%')[1]
		];
	
		template =engine.createTemplate(roleTemplate).make(binding).toString();
		switch (action) {
			case "REMOVE_ATTRIBUTE_VALUES" :
				response = connection.post(path : '/api/action/member_delete', body : template);
				break
			default:
				response = connection.post(path : '/api/action/member_create', body : template);
				break
		}
    } else {
		result = null;
		if (attributes.get("_id") != null){
			val = attributes.get("_id").get(0);
			result = result + val + ','
		} 
		if (attributes.get("__NAME__") != null){
			val = attributes.get("__NAME__").get(0);
			result = result + val + ','
		} 
		if (attributes.get("email") != null){
			val = attributes.get("email").get(0);
			result = result + val + ','
		} 
		if (attributes.get("fullName") != null){
			val = attributes.get("fullName").get(0);
			result = result + val + ','
		} 
		if (attributes.get("about") != null){
			val = attributes.get("about").get(0);
			result = result + val
		} 
		
		if (result != null){
			connection.put( path : '/api/action/user_update', body: '{' + result + '}');
		}
	}
	break

case "__GROUP__":
		def binding = [
        name: (attributes.get("_id") == null)? "": attributes.get("_id").get(0),
        title: "some organization",
		id: (attributes.get("_id") == null)? "": attributes.get("_id").get(0),
    ];
	
	template =engine.createTemplate(groupTemplate).make(binding).toString();
	response = connection.post(path : '/api/action/organization_create', body : template);
	break
	
}
return uid;
