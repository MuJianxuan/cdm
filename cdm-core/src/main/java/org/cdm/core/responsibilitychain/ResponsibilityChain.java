package org.cdm.core.responsibilitychain;

import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * 责任链设计
 * @author Rao
 * @Date 2021-11-01
 **/
@Slf4j
public class ResponsibilityChain<T> extends AbstractChainNode<T> implements Chain<T> {

    public ResponsibilityChain(List<AbstractChainNode<T>> chainNodeList) {
        this.init(chainNodeList);
    }

    @Override
    public void execute(T param) {
        if( this.getNextChainNode() == null){
            log.debug("[ResponsibilityChain] execute fail,mainNode is null!");
        }
        Optional.ofNullable( this.getNextChainNode() ).ifPresent( mainNode -> mainNode.execute( param));
    }

    @Override
    public boolean support(T param) {
        return false;
    }

    @Override
    public void buildFinallyChainNode(AbstractChainNode<T> finallyChainNode) {
        AbstractChainNode<T> mainChainNode = this.getNextChainNode();
        if( mainChainNode == null){
            this.setNextChainNode( finallyChainNode);
        }
        // 责任链构建
        else {
            mainChainNode.buildFinallyChainNode( finallyChainNode);
        }
    }

    /**
     * order  基于注解的order排序
     * @param chainNodeList
     */
    private void init(List<AbstractChainNode<T>> chainNodeList)  {
        // 获取
        if (chainNodeList != null && ! chainNodeList.isEmpty()){
            chainNodeList.sort( Comparator.comparingInt(ChainNode::order));
            chainNodeList.forEach(this::buildFinallyChainNode);

            log.debug("[ResponsibilityChain] init success,size:{}",chainNodeList.size() );
        }
    }

}
