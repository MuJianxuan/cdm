package org.cdm.core.action;

/**
 * Do Action
 * @author Rao
 * @Date 2022/12/28
 **/
@FunctionalInterface
public interface Action<T,R> {

    /**
     * 处理执行
     * @param param 参数
     * @return R 结果
     */
    R doAction(T param);

    /**
     * 空执行
     * @param param
     */
    default void execute(T param){
        this.doAction(param);
    }

}
