/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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
package io.radien.webapp.i18n;

import io.radien.webapp.AbstractLocaleManager;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.JSFUtilAndFaceContextMessagesTest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
/**
 * Class that aggregates UnitTest cases for LocaleManager
 *
 * @author Rajesh Gavvala
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({JSFUtil.class, FacesContext.class, ExternalContext.class})
public class LocaleManagerTest {
    @InjectMocks
    LocaleManager localeManager;

    @Mock
    private AjaxBehaviorEvent ajaxBehaviorEvent;

    @Mock
    private ValueChangeEvent valueChangeEvent;

    @Mock
    private AbstractLocaleManager abstractLocaleManager;

    @Mock
    private FacesContext context;


    List<String> defaultLocalList = new ArrayList<>();
    Map<String, Locale> defaultLocalList1 = new HashMap<>();

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);


        defaultLocalList.add("en");
        defaultLocalList.add("new_language");

        defaultLocalList1.put("en", Locale.forLanguageTag("en"));
        defaultLocalList1.put("new_language", Locale.forLanguageTag("new_language"));

        when(abstractLocaleManager.getClientTzOffset()).thenReturn(getOffset());
    }

    @Test(expected = NullPointerException.class)
    public void testLanguageChanged() {
        when(valueChangeEvent.getNewValue()).thenReturn("new_language");
        when(abstractLocaleManager.getSupportedLanguages()).thenReturn(defaultLocalList);
        localeManager.languageChanged(valueChangeEvent);
    }

    @Test(expected = ExceptionInInitializerError.class)
    public void timeZoneChangedListener_getClientOffset_exception_test() {
        when( Objects.requireNonNull( JSFUtil.getExternalContext()).getRequestLocale()).thenReturn(Locale.forLanguageTag("en-US"));
        localeManager.timezoneChangedListener(ajaxBehaviorEvent);
    }

    @Test
    public void timeZoneChangedListener_getTimeZone_test() {
        abstractLocaleManager.setClientTzOffset(anyString());
        String tzOffset = abstractLocaleManager.getClientTzOffset();
        when(localeManager.getClientTzOffset()).thenReturn(tzOffset);
        assertNotNull(TimeZone.getTimeZone(tzOffset).getDisplayName());
    }

    @Test
    public void getUserLanguage_test() {
        String userLanguage = localeManager.getUserLanguage();
        assertNull(userLanguage);
    }

    @Test
    public void testGetOAF(){
        assertNull(localeManager.getOAF());
    }

    private String getOffset() {
        Calendar instance = Calendar.getInstance(Locale.forLanguageTag("en-US"));
        String tzID = instance.getTimeZone().getID();
        return String.format("%s (%s)", tzID,
                LocalDateTime.now().atZone( ZoneId.of(tzID)).getOffset().getId().replace("Z", "+00:00"));
    }

}