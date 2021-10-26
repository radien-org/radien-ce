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
package io.radien.persistence.jdbc;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Marco Weiland
 */
public abstract class AbstractDatasourceFacade implements Serializable {
	protected static final Logger log = LoggerFactory.getLogger(DatasourceFacade.class);
	private static final long serialVersionUID = 1L;
	private static long initCount = 0;
	transient Connection con;
	private long connectionAccessCount = 0;

	protected abstract void initConnection();

	public Connection getConnection() {
		try {
			return con;
		} finally {
			updateConnectionAccessCount();
			log.info("{} connection was obtained {} times!", this.getClass().getSimpleName(),
					getConnectionAccessCount());
		}

	}

	protected void postConstruct() {
		updateInitCount();
		initConnection();
	}

	protected void preDestroy() {
		try {
			con.close();
		} catch (SQLException e) {
			log.error("Error in pre destroy", e);
		} finally {
			log.info("{} |ACTION: -closeConnection|INITCOUNT: {} |CONNECTIONUSECOUNT: {}",
					this.getClass().getSimpleName(), getConnectionAccessCount(), getInitCount());
		}

	}

	private void updateInitCount() {
		initCount++;
	}

	long getInitCount() {
		return initCount;
	}

	private void updateConnectionAccessCount() {
		connectionAccessCount++;
	}

	private long getConnectionAccessCount() {
		return connectionAccessCount;
	}

}
