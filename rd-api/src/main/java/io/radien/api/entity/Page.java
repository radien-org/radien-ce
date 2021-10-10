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
package io.radien.api.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.json.JsonArray;

/**
 * Page type constructor for the generic show information
 *
 * @author Nuno Santana
 * @author Bruno Gama
 */
public class Page<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = 6812608123262067424L;
    private List<? extends T> results;
    private transient JsonArray jsonValues;
    private int currentPage;
    private int totalResults;
    private int totalPages;

    /**
     * Empty page constructor. Will create an empty page
     */
    public Page() {
        this.results = new ArrayList<>();
        this.currentPage = 0;
        this.totalResults = 0;
        this.totalPages = 0;
    }

    /**
     * Page constructor. Will create a page by the given requested information
     * @param results list of results to be show
     * @param currentPage where the user is requesting to see the information
     * @param totalResults total number of results to be show
     * @param totalPages total number of pages that can be show
     */
    public Page(List<? extends T> results, int currentPage, int totalResults, int totalPages) {
        this.results = results;
        this.currentPage = currentPage;
        this.totalResults = totalResults;
        this.totalPages = totalPages;
    }

    /**
     * Page constructor. Will create a page by the given requested information
     * @param jsonValues JSON Array values
     * @param currentPage where the user is requesting to see the information
     * @param totalResults total number of results to be show
     * @param totalPages total number of pages that can be show
     */
    public Page(JsonArray jsonValues, int currentPage, int totalResults, int totalPages) {
        this.jsonValues = jsonValues;
        this.currentPage = currentPage;
        this.totalResults = totalResults;
        this.totalPages = totalPages;
    }

    /**
     * Page getter for the results
     * @return a list of objects to be used
     */
    public List<? extends T> getResults() {
        return results;
    }

    /**
     * Results setter method. Will overwrite the current results page
     * @param results to be overwritten
     */
    public void setResults(List<? extends T> results) {
        this.results = results;
    }

    /**
     * Page getter for the current page where the user is to see the results
     * @return the page number where the user is
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * Current page setter method. Will overwrite the current page
     * @param currentPage to be set
     */
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    /**
     * Page getter for the total results count
     * @return a count of all the results existent in the result page
     */
    public int getTotalResults() {
        return totalResults;
    }

    /**
     * Total results counter setter.
     * @param totalResults will overwrite the total results
     */
    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    /**
     * Page total pages getter. Will get the number of necessary pages to show all the information.
     * @return the number of necessary pages to show all the information
     */
    public int getTotalPages() {
        return totalPages;
    }

    /**
     * Total pages to be set in the page constructor to be show.
     * @param totalPages to be overwritten. How many pages is it going to be necessary to show all the information
     */
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    /**
     * Page getter for Json Values
     * @return JsonArray object
     */
    public JsonArray getJsonValues() {
        return jsonValues;
    }

    /**
     * Setter for the Page Json Array
     * @param jsonValues to set
     */
    public void setJsonValues(JsonArray jsonValues) {
        this.jsonValues = jsonValues;
    }
}