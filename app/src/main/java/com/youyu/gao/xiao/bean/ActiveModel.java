package com.youyu.gao.xiao.bean;

import java.io.Serializable;

/**
 * @Author zhisiyi
 * @Date 2020.04.20 22:32
 * @Comment
 */
public class ActiveModel implements Serializable {

  public String id;
  public String title;
  public String beginTime;
  public String endTime;
  public String count;
  public int status;
  public int haveJoin;
  public int requireJoin;
  public float totalMoney;

}
