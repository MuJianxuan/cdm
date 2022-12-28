package org.cdm.core.responsibilitychain;

/**
 * @author Rao
 * @Date 2021-11-01
 **/
public abstract class AbstractChainNode<T> implements ChainNode<T> {

    private AbstractChainNode<T> nextChainNode;

    public AbstractChainNode<T> getNextChainNode() {
        return nextChainNode;
    }

    @Override
    public void execute(T param) {

        // R 如何处理

        if( this.support( param)){
            this.handle( param);
        }

        if( nextChainNode != null){
            this.nextChainNode.execute( param);
        }
    }

    /**
     * 业务执行
     * @param param
     */
    protected void handle(T param){}

    /**
     * 构建最后一个 链节点
     * @param finallyChainNode
     */
    @Override
    public void buildFinallyChainNode(AbstractChainNode<T> finallyChainNode) {
        this.setFinallyNextChain( this, finallyChainNode);

    }

    public void setNextChainNode(AbstractChainNode<T> nextChainNode) {
        this.nextChainNode = nextChainNode;
    }

    private void setFinallyNextChain(AbstractChainNode<T> thisChainNode, AbstractChainNode<T> finallyChainNode){
        if( thisChainNode.getNextChainNode() != null){
            this.setFinallyNextChain( thisChainNode.getNextChainNode(),finallyChainNode);
        }else {
            // 设置到当前节点中
            thisChainNode.setNextChainNode(  finallyChainNode);
        }
    }

}
