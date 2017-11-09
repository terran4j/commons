package com.terran4j.commons.api2page;

import java.util.List;

public class ListResult<T> {

    private List<T> list;

    private Integer pageIndex;

    private Integer pageSize;

    private Long total;

    public final List<T> getList() {
        return list;
    }

    public final ListResult<T> setList(List<T> list) {
        this.list = list;
        return this;
    }

    public final Integer getPageIndex() {
        return pageIndex;
    }

    public final ListResult<T> setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
        return this;
    }

    public final Integer getPageSize() {
        return pageSize;
    }

    public final ListResult<T> setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public final Long getTotal() {
        return total;
    }

    public final ListResult<T> setTotal(Long total) {
        this.total = total;
        return this;
    }

}
