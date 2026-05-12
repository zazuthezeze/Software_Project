package com.alerts;

public class PriorityAlertDecorator extends AlertDecorator {
    private String priority;

    public PriorityAlertDecorator(Alert decoratedAlert, String priority) {
        super(decoratedAlert);
        this.priority = priority;
    }

    @Override
    public String getCondition() {
        return "[" + priority + "] " + decoratedAlert.getCondition();
    }

    public String getPriority() {
        return priority;
    }
}