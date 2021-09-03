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
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ${package}.api.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rajesh Gavvala
 * @author Nuno Santana
 * @author Bruno Gama
 */
public class Page<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = 6812608123262000001L;
    private List<? extends T> results;
    private int currentPage;
    private int totalResults;
    private int totalPages;

    public Page() {
        this.results = new ArrayList<>();
        this.currentPage = 0;
        this.totalResults = 0;
        this.totalPages = 0;
    }
    public Page(List<? extends T> results, int currentPage, int totalResults, int totalPages) {
        this.results = results;
        this.currentPage = currentPage;
        this.totalResults = totalResults;
        this.totalPages = totalPages;
    }

    public List<? extends T> getResults() {
        return results;
    }

    public void setResults(List<? extends T> results) {
        this.results = results;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}