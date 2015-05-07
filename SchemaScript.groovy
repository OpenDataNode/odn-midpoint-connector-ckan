/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013 ForgeRock AS. All Rights Reserved
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://forgerock.org/license/CDDLv1.0.html
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at http://forgerock.org/license/CDDLv1.0.html
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * @author Gael Allioux <gael.allioux@forgerock.com>
 *
 */
import org.identityconnectors.framework.common.objects.AttributeInfo;
import org.identityconnectors.framework.common.objects.AttributeInfo.Flags;
import org.identityconnectors.framework.common.objects.AttributeInfoBuilder;
import org.identityconnectors.framework.common.objects.ObjectClassInfo;
import org.identityconnectors.framework.common.objects.ObjectClassInfoBuilder;
import org.identityconnectors.framework.common.objects.OperationalAttributeInfos;

// Parameters:
// The connector sends the following:
// connection: handler to the REST Client 
// (see: http://groovy.codehaus.org/modules/http-builder/apidocs/groovyx/net/http/RESTClient.html)
// configuration : handler to the connector's configuration object
// action: a string describing the action ("SCHEMA" here)
// log: a handler to the Log facility
// builder: SchemaBuilder instance for the connector
//
// The connector will make the final call to builder.build()
// so the scipt just need to declare the different object types.

// This sample shows how to create 2 basic ObjectTypes: __ACCOUNT__ and __GROUP__.
// It works with OpenDJ 2.6 REST sample attribute:
// http://docs.forgerock.org/en/opendj/2.6.0/admin-guide/index/appendix-rest2ldap.html


log.info("Entering "+action+" Script");

// Declare the __ACCOUNT__ attributes
// _id
idAIB = new AttributeInfoBuilder("_id",String.class);
idAIB.setRequired(true);
idAIB.setCreateable(true);
idAIB.setMultiValued(false);
idAIB.setUpdateable(false);

// email
emailNameAIB = new AttributeInfoBuilder("email",String.class);
emailNameAIB.setCreateable(false);
emailNameAIB.setMultiValued(false);
emailNameAIB.setUpdateable(false);

// fullName
fullNameNameAIB = new AttributeInfoBuilder("fullName",String.class);
fullNameNameAIB.setRequired(true);
fullNameNameAIB.setMultiValued(false);

// group displayName
grpDisplayNameAIB = new AttributeInfoBuilder("displayName",String.class);
grpDisplayNameAIB.setMultiValued(false);
grpDisplayNameAIB.setCreateable(false);
grpDisplayNameAIB.setUpdateable(false);

//group title
title = new AttributeInfoBuilder("title",String.class);
title.setMultiValued(false);

//role object_type
roleObjectType = new AttributeInfoBuilder("objectType",String.class);
roleObjectType.setMultiValued(false);

//role object
roleObject = new AttributeInfoBuilder("object",String.class);
roleObject.setMultiValued(false);

//role object
roleCapacity = new AttributeInfoBuilder("capacity",String.class);
roleCapacity.setMultiValued(false);

// about
aboutNameAIB = new AttributeInfoBuilder("about",String.class);
aboutNameAIB.setRequired(false);
aboutNameAIB.setMultiValued(false);

// givenName
openidAIB = new AttributeInfoBuilder("openid",String.class);
openidAIB.setMultiValued(false);

// telephoneNumber
telephoneNumberAIB = new AttributeInfoBuilder("telephoneNumber",String.class);
telephoneNumberAIB.setMultiValued(false);

organizationAIB = new AttributeInfoBuilder("organization",String.class);
organizationAIB.setMultiValued(true);

// emailAddress
emailAddressAIB = new AttributeInfoBuilder("emailAddress",String.class);
emailAddressAIB.setMultiValued(false);

// members
membersAIB = new AttributeInfoBuilder("members",String.class);
membersAIB.setMultiValued(true);

// groups
groupsAIB = new AttributeInfoBuilder("groups",String.class);
groupsAIB.setMultiValued(true);

//created
createdAIB = new AttributeInfoBuilder("created",String.class);
createdAIB.setCreateable(false);
createdAIB.setMultiValued(false);
createdAIB.setUpdateable(false);

//lastModified
lastModifiedAIB = new AttributeInfoBuilder("lastModified",String.class);
lastModifiedAIB.setCreateable(false);
lastModifiedAIB.setMultiValued(false);
lastModifiedAIB.setUpdateable(false);

accAttrsInfo = new HashSet<AttributeInfo>();
accAttrsInfo.add(idAIB.build());
accAttrsInfo.add(emailNameAIB.build());
accAttrsInfo.add(fullNameNameAIB.build());
accAttrsInfo.add(aboutNameAIB.build());
accAttrsInfo.add(openidAIB.build());
accAttrsInfo.add(organizationAIB.build());
account = new ObjectClassInfoBuilder().setType("__ACCOUNT__");
account.addAttributeInfo(OperationalAttributeInfos.PASSWORD);
// Create the __ACCOUNT__ Object class
final ObjectClassInfo ociAccount = account.addAllAttributeInfo(accAttrsInfo).build();
builder.defineObjectClass(ociAccount);

// __GROUP__ attributes
grpAttrsInfo = new HashSet<AttributeInfo>();
grpAttrsInfo.add(idAIB.build());
grpAttrsInfo.add(title.build());
grpAttrsInfo.add(roleObject.build());
grpAttrsInfo.add(roleObjectType.build());
grpAttrsInfo.add(roleCapacity.build());
// Create the __GROUP__ Object class
final ObjectClassInfo ociGroup = new ObjectClassInfoBuilder().setType("__GROUP__").addAllAttributeInfo(grpAttrsInfo).build();
builder.defineObjectClass(ociGroup);

// Role attributes
//roleAttrsInfo = new HashSet<AttributeInfo>();
//roleAttrsInfo.add(idAIB.build());
//roleAttrsInfo.add(roleObject.build());
//roleAttrsInfo.add(roleObjectType.build());
//roleAttrsInfo.add(roleCapacity.build());
// Create the Role Object class
//final ObjectClassInfo ociRole = new ObjectClassInfoBuilder().setType("Role").addAllAttributeInfo(roleAttrsInfo).build();
//builder.defineObjectClass(ociRole);

log.info("Schema script done");