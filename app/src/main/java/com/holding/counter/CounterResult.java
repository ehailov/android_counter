package com.holding.counter;

public interface CounterResult {

    void onSuccess(CounterModel data);

    void onFailure(String errorMessage);

    void onMessage(String message);

}
