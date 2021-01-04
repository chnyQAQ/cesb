package com.dah.desb.domain.route.endpoint.expression;

public class ExchangeVariableParser {

    public static Object header(String key) {
        return ExchangeHolder.get().getIn().getHeader(key);
    }

    public static Object headerInt(String key) {
        return Integer.parseInt(ExchangeHolder.get().getIn().getHeader(key).toString());
    }

    public static Object headerFloat(String key) {
        return Float.parseFloat(ExchangeHolder.get().getIn().getHeader(key).toString());
    }

    public static Object headerDouble(String key) {
        return Double.parseDouble(ExchangeHolder.get().getIn().getHeader(key).toString());
    }

    public static Object property(String key) {
        return ExchangeHolder.get().getProperty(key);
    }

}
