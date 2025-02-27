package org.cdm.core.filterchain;

import java.util.Iterator;
import java.util.List;

/**
 * @author Rao
 * @Date 2021-11-01
 **/
public class IteratorFilterChain<T> implements FilterChain<T> {

    private final Iterator<Filter<T>> filterChain;

    public IteratorFilterChain(List<Filter<T>> filterList) {
        assert filterList != null;
        this.filterChain = filterList.iterator();
    }

    @Override
    public void doFilter(T param) {
        if(filterChain != null && filterChain.hasNext()){
            filterChain.next().doFilter(param,this);
        }
    }
}
