package org.cdm.core.filterchain;

/**
 * @author Rao
 * @Date 2021/8/2
 **/
@FunctionalInterface
public interface FilterChain<T> {
    /**
     * 执行过滤
     * @param param
     */
    void doFilter(T param);

}
