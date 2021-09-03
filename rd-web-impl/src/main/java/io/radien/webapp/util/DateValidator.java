/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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
 * See the License for the specific language governing actions and
 * limitations under the License.
 */
package io.radien.webapp.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import java.util.Date;

/**
 * @author Newton Carvalho
 */
@FacesValidator("basicDateValidator")
public class DateValidator implements Validator<Date> {

    @Override
    public void validate(FacesContext context, UIComponent component, Date dateFromUI)
            throws ValidatorException {

        if (dateFromUI == null)
            return;

        Date now = DateUtils.truncate(new Date(), java.util.Calendar.DATE);

        String message = (String) component.getAttributes().get("validatorMessage");
        if (StringUtils.isEmpty(message)) {
            message = "Invalid Date";
        }

        if (DateUtils.isSameDay(now, dateFromUI) || dateFromUI.before(now)) {
            throw new ValidatorException(new FacesMessage(message));
        }

    }
}
