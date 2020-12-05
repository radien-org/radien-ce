/*
 * Copyright (c) 2016-present radien.io & its legal owners. All rights reserved.
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
package io.radien.test.persistence;

import java.util.List;
import java.util.Properties;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;

import org.junit.Test;

import io.radien.persistence.entities.system.Appframe;
import io.radien.persistence.service.system.AppframeService;
import junit.framework.TestCase;

/**
 * @author Marco Weiland <m.weiland@radien.io>
 *
 */
public class AppframeServiceTest extends TestCase {
	
	Properties p;
	
	AppframeService appframeService;
	
	public AppframeServiceTest() throws Exception  {
		p = new Properties();
        p.put("appframeDatabase", "new://Resource?type=DataSource");
        p.put("appframeDatabase.JdbcDriver", "org.hsqldb.jdbcDriver");
        p.put("appframeDatabase.JdbcUrl", "jdbc:hsqldb:mem:appframedb");
        p.put("appframeDatabase.userName", "sa");
        p.put("appframeDatabase.password", "");	
        
        
        final Context context = EJBContainer.createEJBContainer(p).getContext();
        
        appframeService = (AppframeService) context.lookup("java:global/rd-persistence//AppframeService");
	}
	
	@Test
	public void testAddAppframe() throws Exception {
		appframeService.addAppframe(new Appframe(1L,"1.0"));
        appframeService.addAppframe(new Appframe(2L,"2.0"));
        appframeService.addAppframe(new Appframe(3L,"3.0"));

        List<Appframe> list = appframeService.getAppframes();
        assertEquals("List.size()", 3, list.size());

        for (Appframe movie : list) {
        	appframeService.deleteAppframe(movie);
        }

        assertEquals("AppframeService.getAppframe()", 0, appframeService.getAppframes().size());
    }
	
	@Test
    public void testDeleteAppframe(Appframe appframe) throws Exception {
    	appframeService.addAppframe(new Appframe(1L,"1.0"));
        appframeService.addAppframe(new Appframe(2L,"2.0"));
        appframeService.addAppframe(new Appframe(3L,"3.0"));

        List<Appframe> list = appframeService.getAppframes();
        assertEquals("List.size()", 3, list.size());

        for (Appframe movie : list) {
        	appframeService.deleteAppframe(movie);
        }

        assertEquals("AppframeService.getAppframe()", 0, appframeService.getAppframes().size());
    }

	@Test
    public void testGetAppframes() throws Exception {
    	appframeService.addAppframe(new Appframe(1L,"1.0"));
        appframeService.addAppframe(new Appframe(2L,"2.0"));
        appframeService.addAppframe(new Appframe(3L,"3.0"));

        List<Appframe> list = appframeService.getAppframes();
        assertEquals("List.size()", 3, list.size());

        for (Appframe movie : list) {
        	appframeService.deleteAppframe(movie);
        }

        assertEquals("AppframeService.getAppframe()", 0, appframeService.getAppframes().size());
    }
}
