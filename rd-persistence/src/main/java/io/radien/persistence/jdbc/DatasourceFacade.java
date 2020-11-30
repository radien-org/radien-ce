/**
 * Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.
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

import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.sql.DataSource;

/**
 * @author Marco Weiland
 *
 */
public @RequestScoped class DatasourceFacade extends AbstractDatasourceFacade {
	private static final long serialVersionUID = 1L;

	@Resource(mappedName = "java:/comp/env/jdbc/OracleDS")
	private DataSource datasource;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
	}

	@Override
	@PreDestroy
	protected void preDestroy() {
		super.preDestroy();
	}

	@Override
	public void initConnection() {
		if (con == null) {
			boolean error = false;
			try {
				con = datasource.getConnection();
			} catch (SQLException e) {
				log.error("Error on init conenction", e);
				error = true;
			} finally {
				if (!error) {
					log.info(String.format("%s connection was opened " + getInitCount() + " times since last reboot",
							this.getClass().getSimpleName()));
				}
			}
		}

	}

}
