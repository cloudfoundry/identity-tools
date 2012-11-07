/*
 * Cloud Foundry 2012.02.03 Beta
 * Copyright (c) [2009-2012] VMware, Inc. All Rights Reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0 (the "License").
 * You may not use this product except in compliance with the License.
 *
 * This product includes a number of subcomponents with
 * separate copyright notices and license terms. Your use of these
 * subcomponents is subject to the terms and conditions of the
 * subcomponent's license, as noted in the LICENSE file.
 */

package org.cloudfoundry.identity.varz.integration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cloudfoundry.identity.uaa.integration.TestProfileEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.codec.Base64;

/**
 * @author Dave Syer
 *
 */
public class VarzTestAccounts {

	private static final Log logger = LogFactory.getLog(VarzTestAccounts.class);

	private Environment environment = TestProfileEnvironment.getEnvironment();

	public static VarzTestAccounts standard() {
		return new VarzTestAccounts();
	}

	public String getVarzAuthorizationHeader() {
		return getAuthorizationHeader("varz", "varz", "varzclientsecret");
	}

	public String getAuthorizationHeader(String prefix, String defaultUsername, String defaultPassword) {
		String username = environment.getProperty(prefix + ".username", defaultUsername);
		String password = environment.getProperty(prefix + ".password", defaultPassword);
		return getAuthorizationHeader(username, password);
	}

	public String getAuthorizationHeader(String username, String password) {
		String credentials = String.format("%s:%s", username, password);
		return String.format("Basic %s", new String(Base64.encode(credentials.getBytes())));
	}

	/**
	 * @return true if this Spring profile is enabled on the server
	 */
	public boolean isProfileActive(String profile) {
		logger.debug(String.format("Checking for %s profile in: [%s]", profile, environment));
		return profile != null && environment.acceptsProfiles(profile);
	}

}
