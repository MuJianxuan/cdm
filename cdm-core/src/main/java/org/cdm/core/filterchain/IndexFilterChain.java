package org.cdm.core.filterchain;


import cn.hutool.core.collection.CollUtil;

import java.util.List;


/**
 * 扩展一下
 * @author Rao
 * @Date 2022/12/28
 **/
public class IndexFilterChain<T> implements FilterChain<T> {

    private final List<Filter<T>> filterList;
    private Integer index = 0;

    public IndexFilterChain(List<Filter<T>> filterList) {
        this.filterList = filterList;
    }

    @Override
    public void doFilter(T param) {
        if(CollUtil.isNotEmpty(filterList)){
            filterList.get( index++).doFilter(param,this);
        }
    }

    /**
     * 可重复执行
     */
    public void repeatExecute(T param){
        this.doFilter(param);
        this.index = 0;
    }

}
