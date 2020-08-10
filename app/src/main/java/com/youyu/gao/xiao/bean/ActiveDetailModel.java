package com.youyu.gao.xiao.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @Author zhisiyi
 * @Date 2020.04.22 21:50
 * @Comment
 */
public class ActiveDetailModel implements Serializable {

    public String activeTitle;
    public String activeBeginTime;
    public String activeEndTime;
    public String require;
    public String participants;
    public String activeCountDownTime;
    public String activeBonus;
    public ArrayList<ActiveFinisher> finishers;
}
