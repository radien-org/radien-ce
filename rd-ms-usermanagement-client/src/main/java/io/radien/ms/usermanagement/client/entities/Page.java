package io.radien.ms.usermanagement.client.entities;

import java.util.List;

public class Page<T> {

    List<T> results;
    int currentPage;
    int totalResults;
    int totalPages;


    public Page(List<T> results, int currentPage, int totalResults, int totalPages) {
        this.results = results;
        this.currentPage = currentPage;
        this.totalResults = totalResults;
        this.totalPages = totalPages;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
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
