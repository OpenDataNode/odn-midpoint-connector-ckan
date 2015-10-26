/*
 * Copyright (c) 2010-2014 Evolveum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opendata.midpoint.connector.ckan;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConnectorIOException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeInfoBuilder;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.ObjectClassInfoBuilder;
import org.identityconnectors.framework.common.objects.OperationOptions;
import org.identityconnectors.framework.common.objects.Schema;
import org.identityconnectors.framework.common.objects.SchemaBuilder;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.Connector;
import org.identityconnectors.framework.spi.ConnectorClass;
import org.identityconnectors.framework.spi.operations.CreateOp;
import org.identityconnectors.framework.spi.operations.SchemaOp;
import org.identityconnectors.framework.spi.operations.TestOp;

@ConnectorClass(displayNameKey = "ckan.connector.display", configurationClass = CkanConfiguration.class)
public class CkanConnector implements Connector, SchemaOp, CreateOp, TestOp {

	private static final Log LOG = Log.getLog(CkanConnector.class);

	private CkanConfiguration configuration;
	private CkanConnection connection;

	@Override
	public Configuration getConfiguration() {
		return configuration;
	}

	@Override
	public void init(Configuration configuration) {
		LOG.info("Connector init");
		this.configuration = (CkanConfiguration) configuration;
		this.connection = new CkanConnection(this.configuration);
	}

	@Override
	public void dispose() {
		configuration = null;
		if (connection != null) {
			connection.dispose();
			connection = null;
		}
	}
	
	@Override
	public Schema schema() {
		LOG.info("Schema script start");
		SchemaBuilder builder = new SchemaBuilder(CkanConnector.class);
		
		ObjectClassInfoBuilder objectClassInfo = new ObjectClassInfoBuilder();
		objectClassInfo.setType(ObjectClass.GROUP_NAME);

		// mapping of attributes needed to create org:
		// CKAN  | MIDPOINT
		// ----------------
		// id    | name
		// name  | name
		// title | displayName
		
		//group name (title?)
		AttributeInfoBuilder name = new AttributeInfoBuilder("name",String.class);
		name.setMultiValued(false);
		objectClassInfo.addAttributeInfo(name.build());
		
		AttributeInfoBuilder displayName = new AttributeInfoBuilder("displayName",String.class);
		displayName.setMultiValued(false);
		displayName.setCreateable(false);
		displayName.setUpdateable(false);
		objectClassInfo.addAttributeInfo(displayName.build());
		
		builder.defineObjectClass(objectClassInfo.build());

		LOG.info("Schema script done");
		return builder.build();
	}

	private void print(Set<Attribute> attributes, OperationOptions options) {
		for (Attribute attribute : attributes) {
			LOG.info("attribute name = " + attribute.getName());
			for (Object value : attribute.getValue()) {
				LOG.info("attribute value = " + value);
			}
			LOG.info("------");
		}
		
		LOG.info("options = " + options.toString());
	}

	@Override
	public Uid create(ObjectClass objectClass, Set<Attribute> attributes,
			OperationOptions options) {
		// TODO
		LOG.info("Create action start");
		print(attributes, options);
		
		Uid uid = null;
		
		for (Attribute attribute : attributes) {
			if ("id".equals(attribute.getName())) {
				Object uidObj = attribute.getValue() == null ? "" : attribute.getValue().get(0);
				LOG.info("UID = " + uid);
				uid = new Uid((String) uidObj);
				break;
			}
		}
		
		if (objectClass.is(ObjectClass.GROUP_NAME)) {
			// TODO do the job
			// in case of error:
			// throw new ConnectorIOException(e.getMessage(), e);
			// throw new InvalidAttributeValueException("error message");
		} else {
			throw new UnsupportedOperationException("Unsupported object class "+objectClass);
		}
		
		LOG.info("Create action done");
		return uid;
	}

	@Override
	public void test() {
		LOG.info("Test action start");
		
		// url: {ckan_host}/api/action/site_read is used to determine the
		// site url is correct
		String ckanReadUrl = configuration.getCkanUrl() + "/site_read";
		LOG.info("CKAN status show url = " + ckanReadUrl);
		InputStream is = null;
		try {
			/*
			 * example response (JSON):
			 * {
			 *   "help":"...",
			 *   "success": true,
			 *   "result": true
			 * }
			 */
			is = new URL(ckanReadUrl).openStream();
			String str = IOUtils.toString(is, "UTF-8");
			LOG.info("Status show response {0}", str);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			throw new ConnectorIOException(e.getMessage(), e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					throw new ConnectorIOException(e.getMessage(), e);
				}
			}
		}
		
		LOG.info("Test action done");
	}
}
