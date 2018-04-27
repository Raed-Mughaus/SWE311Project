package com.raed.swe311project;

/**
 * This interface is designed to provide a protocol for tasks that needs to be executed
 * asynchronously.
 * An instance of this interface must implement 2 methods, one for data handling and another
 * method for error handling.
 * At the end of a task only one of these methods should be called.
 */

public interface TaskCallback<T>{

    /**
     * This method will be invoked once the task is executed successfully.
     * @param t the data resulted from executing this task.
     */
    void onTaskExecuted(T t);

    /**
     * This method will be invoked when an error happens.
     * @param e the error that causes the task to fail.
     **/
    void onError(Exception e);
}
