package com.terran4j.commons.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Expressions {

    private static Logger log = LoggerFactory.getLogger(Expressions.class);

    private static final ExpressionParser parser = new SpelExpressionParser();

    private static final Map<String, Expression> cache = new ConcurrentHashMap<>();

    public static final Expression getExpression(String expEL) {
        Expression exp = cache.get(expEL);
        if (exp != null) {
            return exp;
        }
        synchronized(Expressions.class) {
            exp = cache.get(expEL);
            if (exp != null) {
                return exp;
            }
            exp = parser.parseExpression(expEL);
            if (log.isInfoEnabled()) {
                log.info("parseExpression done: {}", expEL);
            }
            cache.put(expEL, exp);
            return exp;
        }
    }

    public static final <T> T parse(String el, Map<String, Object> params, Class<T> resultType) {
        Expression expression = getExpression(el);
        T value = expression.getValue(buildContext(params), resultType);
        return value;
    }

    public static final Object parse(String el, Map<String, Object> params) {
        Expression expression = getExpression(el);
        Object value = expression.getValue(buildContext(params));
        return value;
    }

    private static final EvaluationContext buildContext(Map<String, Object> params) {
        EvaluationContext context = new StandardEvaluationContext();
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                Object value = params.get(key);
                context.setVariable(key, value);
            }
        }
        return context;
    }

}
