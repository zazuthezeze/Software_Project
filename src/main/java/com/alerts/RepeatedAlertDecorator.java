package com.alerts;

public class RepeatedAlertDecorator extends AlertDecorator {
    private int repeatCount;

    public RepeatedAlertDecorator(Alert decoratedAlert, int repeatCount) {
        super(decoratedAlert);
        this.repeatCount = repeatCount;
    }

    @Override
    public String getCondition() {
        return decoratedAlert.getCondition() + " [Repeated x" + repeatCount + "]";
    }

    public int getRepeatCount() {
        return repeatCount;
    }
}
