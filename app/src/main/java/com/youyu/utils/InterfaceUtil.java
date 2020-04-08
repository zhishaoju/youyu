package com.youyu.utils;


/**
 * @Author zsj
 * @Date 2020.04.08 23:20
 * @Commit
 */
public interface InterfaceUtil {

    interface TaskManager {
        void continueTask(String taskId);

        void cancelTask(String taskId);
    }
}
