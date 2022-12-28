package org.cdm.core.handler;

/**
 * @author Rao
 * @Date 2022/12/28
 **/
@FunctionalInterface
public interface AbstractHandler<T,R> {

    /**
     * 处理执行
     * @param param 参数
     * @return R 结果
     */
    R handle(T param);

}
