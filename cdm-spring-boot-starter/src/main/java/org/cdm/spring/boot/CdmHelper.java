package org.cdm.spring.boot;

import org.cdm.core.eventdrivenstatemachine.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Rao
 * @Date 2023/3/29
 **/
@SuppressWarnings("unchecked")
public class CdmHelper {

    private final ApplicationContext applicationContext;

    public CdmHelper(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 事件驱动状态机
     * @return
     * @param <T>
     */
    public <T extends StateMachineId> EventDrivenStateMachine<T> eventDrivenStateMachine(){
        String[] stateAssistantBeanNames = applicationContext.getBeanNamesForType(ResolvableType.forType(new ParameterizedTypeReference<AbstractStateAssistant<T>>() {}));
        if(stateAssistantBeanNames.length != 1){
            throw new RuntimeException("stateAssistant require one,but found " + stateAssistantBeanNames.length);
        }
        AbstractStateAssistant<T> stateAssistant = (AbstractStateAssistant<T>) applicationContext.getBean(stateAssistantBeanNames[0]);
        ObjectProvider<AbstractEventBehaviorHandler<T>> beanProvider = applicationContext.getAutowireCapableBeanFactory().getBeanProvider(ResolvableType.forType(new ParameterizedTypeReference<AbstractEventBehaviorHandler<T>>() {}));
        List<AbstractEventBehaviorHandler<T>> eventBehaviorHandlerList = beanProvider.stream().filter(eventBehaviorHandler -> Objects.nonNull(eventBehaviorHandler.drivenEvent())).collect(Collectors.toList());
        Map<DrivenEvent, AbstractEventBehaviorHandler<T>> eventBehaviorHandlerPool = eventBehaviorHandlerList.stream().collect(Collectors.toMap(AbstractEventBehaviorHandler::drivenEvent, Function.identity()));
        return new EventDrivenStateMachine<>(stateAssistant, eventBehaviorHandlerPool);
    }

}
