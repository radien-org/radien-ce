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

package io.radien.ms.usermanagement.client.entities;

import io.radien.api.model.AbstractModel;
import io.radien.api.model.jobshop.SystemStudentIdentity;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class StudentIdentity extends AbstractModel implements SystemStudentIdentity {
    private static final long serialVersionUID = -3127707819233727730L;

    @NotBlank(message = "O nome não pode estar vazio")
    private String name;
    @Email(message = "O email não tem formato correcto")
    @NotBlank(message = "O email não pode estar vazio")
    private String email;
    @Pattern(regexp = "[0-9]{9}", message = "O telefone têm de ter 9 digitos")
    private String phoneNumber;
    @NotBlank(message = "O curso não pode estar vazio")
    private String course;
    private String interests;
    @Pattern(regexp = "[0-9][0-9][0-9][0-9]?", message = "O ano de graduação deverá de ser composto unicamente por digitos")
    private String graduationYear;


    @Override
    public String getName() {
        return name;
    }
    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getEmail() {
        return email;
    }
    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }
    @Override
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String getCourse() {
        return course;
    }
    @Override
    public void setCourse(String course) {
        this.course = course;
    }

    @Override
    public String getInterests() {
        return interests;
    }
    @Override
    public void setInterests(String interests) {
        this.interests = interests;
    }

    @Override
    public String getGraduationYear() {
        return graduationYear;
    }
    @Override
    public void setGraduationYear(String graduationYear) {
        this.graduationYear = graduationYear;
    }
}
