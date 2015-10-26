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

import static org.identityconnectors.common.StringUtil.isBlank;

import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.spi.AbstractConfiguration;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.spi.ConfigurationProperty;

public class CkanConfiguration extends AbstractConfiguration {

    private static final Log LOG = Log.getLog(CkanConfiguration.class);

    private String ckanUrl;
    
    private String apiKey;

    @Override
    public void validate() {
    	validateNotBlank(ckanUrl, "ckan.config.ckanUrl.blank");
//    	validateNotBlank(apiKey, "ckan.config.apiKey.blank");
    }
    
    private void validateNotBlank(String value, String errorKey) {
        if (isBlank(value)) {
        	throwConfigurationError(errorKey);
        }
    }
    
    private void throwConfigurationError(String errorKey) {
    	throw new ConfigurationException(getConnectorMessages().format(errorKey, null));
    }

    @ConfigurationProperty(displayMessageKey = "ckan.config.ckanUrl",
            helpMessageKey = "ckan.config.ckanUrl.help")
	public String getCkanUrl() {
		return ckanUrl;
	}

	public void setCkanUrl(String ckanUrl) {
		this.ckanUrl = ckanUrl;
	}

	@ConfigurationProperty(displayMessageKey = "ckan.config.apiKey",
            helpMessageKey = "ckan.config.apiKey.help")
	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
}