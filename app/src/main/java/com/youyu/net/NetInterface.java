package com.youyu.net;

/**
 * @author zhisiyi
 * @date 20.04.08 23:17
 * @comment
 */
public interface NetInterface {
    interface RequestResponse {
        void failure(Exception e);

        void success(String data);
    }
}
