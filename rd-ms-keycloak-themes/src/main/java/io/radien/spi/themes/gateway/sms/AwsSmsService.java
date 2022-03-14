/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.radien.spi.themes.gateway.sms;

import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;

public class AwsSmsService implements SmsService {

	private static final SnsClient sns = SnsClient.create();

	private final String senderId;

	AwsSmsService(Map<String, String> config) {
		senderId = config.get("senderId");
	}

	@Override
	public void send(String phoneNumber, String message) {
		Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
		messageAttributes.put("AWS.SNS.SMS.SenderID",
			MessageAttributeValue.builder().stringValue(senderId).dataType("String").build());
		messageAttributes.put("AWS.SNS.SMS.SMSType",
			MessageAttributeValue.builder().stringValue("Transactional").dataType("String").build());

		sns.publish(builder -> builder
			.message(message)
			.phoneNumber(phoneNumber)
			.messageAttributes(messageAttributes));
	}

}
