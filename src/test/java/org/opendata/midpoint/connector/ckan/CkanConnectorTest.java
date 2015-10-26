package org.opendata.midpoint.connector.ckan;

import org.identityconnectors.framework.common.exceptions.ConnectorIOException;
import org.junit.Test;
import org.junit.Assert;

public class CkanConnectorTest {
	
	@Test
	public void printSchema() {
		CkanConnector conn = new CkanConnector();
		System.out.println(conn.schema());
	}
	
	@Test
	public void testOp() {
		CkanConfiguration conf = new CkanConfiguration();
		conf.setCkanUrl("http://demo.ckan.org/api/action");
//		conf.setApiKey(null);
		
		CkanConnector conn = new CkanConnector();
		conn.init(conf);
		
		// this shouldn't throw exception
		conn.test();
		
		// lets try wrong url
		conf.setCkanUrl("http://not_existing_ckan.com");
		
		try {
			conn.test();
			Assert.fail("This should have failed because of wrong url");
		} catch (ConnectorIOException e) {
			// OK 
			System.out.println(e.getMessage());
		}
	}
}
