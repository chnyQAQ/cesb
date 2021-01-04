package com.dah.desb.domain.route.endpoint.expression;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Component
public class ExpressionEvaluator extends StandardEvaluationContext implements InitializingBean {

    private ExpressionParser parser = new SpelExpressionParser();

    @Override
    public void afterPropertiesSet() throws Exception {
        this.registerFunction("header", ExchangeVariableParser.class.getDeclaredMethod("header", String.class));
        this.registerFunction("headerInt", ExchangeVariableParser.class.getDeclaredMethod("headerInt", String.class));
        this.registerFunction("headerFloat", ExchangeVariableParser.class.getDeclaredMethod("headerFloat", String.class));
        this.registerFunction("headerDouble", ExchangeVariableParser.class.getDeclaredMethod("headerDouble", String.class));
        this.registerFunction("property", ExchangeVariableParser.class.getDeclaredMethod("property", String.class));
    }

    public boolean assertTrue(String expression) {
        // 表达式格式：
    	// #注册函数名('参数名')
        return parser.parseExpression(expression).getValue(this, Boolean.class);
    }

}
