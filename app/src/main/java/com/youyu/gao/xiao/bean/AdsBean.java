package com.youyu.gao.xiao.bean;

/**
 * @Author zhiyukai
 * @Date 2020.09.19 13:51
 * @Comment 0:广点通 1:穿山甲 2:百度 3:adView
 */
public class AdsBean extends BaseResponseBean {

  public Data data;

  public class Data {
    public int taskOne;
    public String taskOneType;
    public int taskTwo;
    public String taskTwoType;
    public int taskThree;
    public int clickAds;
    public int adType;
    public boolean videoDetail;
    public int screen;
    public String content;

    @Override
    public String toString() {
      return "Data{" +
              "taskOne=" + taskOne +
              ", taskTwo=" + taskTwo +
              ", taskThree=" + taskThree +
              ", clickAds=" + clickAds +
              ", adType=" + adType +
              ", videoDetail=" + videoDetail +
              ", screen=" + screen +
              ", content='" + content + '\'' +
              '}';
    }
  }
}
