package com.example.meetapp.callbacks;


public interface OnClickInCalender {
    /**
     * a method called when a click has happened in calendar
     * @param value represent some value
     * @param action represent the action that is needed
     * @param millis represent the milliseconds of the date clicked
     * @param i represent an index in a list
     */
    void onClickIInCalender(Object value,String action,long millis ,int i);
}
