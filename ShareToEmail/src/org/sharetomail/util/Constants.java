/*******************************************************************************
 * Copyright 2013 Peter Mihaly Avramucz
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.sharetomail.util;

public class Constants {
	public static final String SHARED_PREFERENCES_NAME = "ShareToEmail_SHARED_PREFERENCES_NAME";
	public static final String SPLIT_REGEXP = ",";
	public static final String MAILTO_SCHEME = "mailto";

	public static final int ADD_EMAIL_ADDRESS_ACTIVITY_REQUEST_CODE = 1;
	public static final int MODIFY_EMAIL_ADDRESS_ACTIVITY_REQUEST_CODE = 2;
	public static final int SETTINGS_ACTIVITY_REQUEST_CODE = 3;

	public static final String NEW_EMAIL_ADDRESS_INTENT_KEY = "NEW_EMAIL_ADDRESS_INTENT_KEY";
	public static final String ORIG_EMAIL_ADDRESS_INTENT_KEY = "ORIG_EMAIL_ADDRESS_INTENT_KEY";
	public static final String AUTO_USE_DEFAULT_EMAIL_ADDRESS_INTENT_KEY = "AUTO_USE_DEFAULT_EMAIL_ADDRESS_INTENT_KEY";
	public static final String EMAIL_SUBJECT_INTENT_KEY = "EMAIL_SUBJECT_INTENT_KEY";

	public static final String EMAIL_ADDRESSES_SHARED_PREFERENCES_KEY = "email_addresses";
	public static final String DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY = "default_email_address";
	public static final String EMAIL_SUBJECT_PREFIX_SHARED_PREFERENCES_KEY = "email_subject_prefix";
	public static final String AUTO_USE_DEFAULT_EMAIL_ADDRESS_SHARED_PREFERENCES_KEY = "auto_use_default_email_address";
	public static final String AUTO_USE_DEFAULT_EMAIL_ADDRESS_DEFAULT_VALUE = Boolean.TRUE
			.toString();

	public static final String CONFIGURATION_BACKUP_FILE = "sharetoemail-backup.properties";
}
