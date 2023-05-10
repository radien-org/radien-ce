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

package io.radien.ms.usermanagement.entities;

import io.radien.ms.usermanagement.client.entities.JobshopItem;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "JBSHPITEM01")
public class JobshopItemEntity extends JobshopItem {

    public JobshopItemEntity() { }

    public JobshopItemEntity(JobshopItem item) {
        setId(item.getId());
        setLabel(item.getLabel());
        setWeight(item.getWeight());
        setCreateDate(item.getCreateDate());
        setCreateUser(item.getCreateUser());
    }

    @Id
    @TableGenerator(name = "GEN_SEQ_JBSHPITEM01", allocationSize = 2000)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "GEN_SEQ_JBSHPITEM01")
    @Override
    public Long getId() { return super.getId(); }

    @Column
    @Override
    public String getLabel() { return super.getLabel(); }

    @Column
    @Override
    public int getWeight() { return super.getWeight(); }

    @Column
    @Override
    public Date getCreateDate() { return super.getCreateDate(); }

    @Column
    @Override
    public Long getCreateUser() { return super.getCreateUser(); }
}
