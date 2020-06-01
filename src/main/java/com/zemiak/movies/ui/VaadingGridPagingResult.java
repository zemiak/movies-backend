package com.zemiak.movies.ui;

import java.util.List;

public class VaadingGridPagingResult<T> {
    public long count;
    public List<T> result;

    public VaadingGridPagingResult(long count, List<T> result) {
        this.count = count;
        this.result = result;
    }
}
