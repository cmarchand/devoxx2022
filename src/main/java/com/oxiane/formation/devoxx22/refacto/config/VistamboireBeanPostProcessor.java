package com.oxiane.formation.devoxx22.refacto.config;

import com.oxiane.formation.devoxx22.refacto.services.rest.data.FactureController;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class VistamboireBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
