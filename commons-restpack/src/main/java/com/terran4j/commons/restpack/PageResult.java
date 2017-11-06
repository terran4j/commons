package com.terran4j.commons.restpack;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.terran4j.commons.util.Maths;
import com.terran4j.commons.util.error.BusinessException;

public class PageResult<T> {

	private List<T> list;
	
	private Integer pageIndex; 
	
	private Integer pageSize;
	
	private Long total;

	public final List<T> getList() {
		return list;
	}

	public final PageResult<T> setList(List<T> list) {
		this.list = list;
		return this;
	}

	public final Integer getPageIndex() {
		return pageIndex;
	}

	public final PageResult<T> setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
		return this;
	}

	public final Integer getPageSize() {
		return pageSize;
	}

	public final PageResult<T> setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	public final Long getTotal() {
		return total;
	}

	public final PageResult<T> setTotal(Long total) {
		this.total = total;
		return this;
	}
	
	public static interface Convertor<T, K> {
		
		K convertFrom(T from) throws BusinessException;
		
	}
	
	public <K> PageResult<K> convert(Convertor<T, K> convertor) throws BusinessException {
		PageResult<K> result = new PageResult<K>();
		
		List<K> list = new ArrayList<>(this.list.size());
		for (T from : this.list) {
			K to = convertor.convertFrom(from);
			list.add(to);
		}
		result.setList(list);
		
		result.setPageIndex(pageIndex);
		result.setPageSize(pageSize);
		result.setTotal(total);
		
		return result;
	}
	
	public static final class Scope {
		
		public int index;
		
		public int count;
	}
	
	public static Scope toRecordScope(int pageIndex, int pageSize) {
		return toRecordScope(pageIndex, pageSize, 100);
	}
	
	public static Scope toRecordScope(int pageIndex, int pageSize, int maxSize) {
		pageIndex = Maths.limitIn(pageIndex, 1, null);
		pageSize = Maths.limitIn(pageSize, 1, maxSize);
		Scope scope = new Scope();
		scope.index = (pageIndex - 1) * pageSize;
		scope.count = pageSize;
		return scope;
	}
	
	public static String asLikeContent(String fuzzy) {
		if (StringUtils.isEmpty(fuzzy)) {
			fuzzy = "%";
		} else {
			fuzzy = "%" + fuzzy.trim() + "%";
		}
		return fuzzy;
	}
	
}
