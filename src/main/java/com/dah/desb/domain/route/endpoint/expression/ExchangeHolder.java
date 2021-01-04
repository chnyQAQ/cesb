package com.dah.desb.domain.route.endpoint.expression;

import org.apache.camel.Exchange;

public class ExchangeHolder {

    private static final ThreadLocal<Exchange> exchangeThreadLocal = new ThreadLocal<>();

    public static Exchange get() {
        return exchangeThreadLocal.get();
    }

    public static void set(Exchange exchange) {
        exchangeThreadLocal.set(exchange);
    }

    public static void remove() {
        exchangeThreadLocal.remove();
    }

}
