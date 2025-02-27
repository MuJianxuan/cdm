package org.cdm.spring.boot.configuration;

import org.cdm.spring.boot.CdmHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Rao
 * @Date 2023/3/29
 **/
@Configuration
public class CdmConfiguration {

    @Bean
    @ConditionalOnMissingBean(value = CdmHelper.class)
    public CdmHelper cdmHelper(ApplicationContext applicationContext){
        return new CdmHelper(applicationContext);
    }

}
