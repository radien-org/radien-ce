/*
 * Copyright (c) 2006-present openappframe.org & its legal owners. All rights reserved.
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
package io.rd.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Comparator;

/**
 * @author Rajesh Gavvala
 * @author Marco Weiland
 */
public class ZoneComparator implements Comparator<ZoneId> {

	private static final long serialVersionUID = 6812608123262000018L;
	@Override
	public int compare(ZoneId zoneId1, ZoneId zoneId2) {
		LocalDateTime now = LocalDateTime.now();
		ZoneOffset offset1 = now.atZone(zoneId1).getOffset();
		ZoneOffset offset2 = now.atZone(zoneId2).getOffset();

		return offset1.compareTo(offset2);
	}
}
