package org.cdm.core.responsibilitychain;

/**
 * @author Rao
 * @Date 2021-11-01
 **/
public interface ChainNode<T> {

    /**
     * 执行
     * @param param
     * @return
     */
    void execute(T param);

    /**
     * 支持
     * @param param
     * @return
     */
    boolean support(T param);


    /**
     * 构建最后一个 链节点
     * @param finallyChainNode
     */
    default void buildFinallyChainNode(AbstractChainNode<T> finallyChainNode) { }

    /**
     * 排序
     * @return
     */
    default int order(){return 0;}

}
