package com.example.meetapp.callbacks;

public interface OnClickInRecyclerView {
    /**
     * a method called in a fragment/activity when a click has happened in some recycler view
     * @param value represent some value
     * @param action represent the action that is needed
     * @param i represent an index in a list
     */
    void onClickInRecyclerView(Object value, String action , Integer i);
}
