package com.youyu.gao.xiao.bean;

/**
 * @Author zhiyukai
 * @Date 2020.09.19 13:51
 * @Comment
 */
public class AdsBean extends BaseResponseBean{

  /**
   * "adsConfig": {//任务中心广告是否开启
   *             "tx": "true",//腾讯广告展示
   *             "csj": "true"，//穿山甲展示
   *             "csjTotal": "1",//任务中心任务穿山甲广告展示次数
   *             "txTotal": "2",//任务中心任务腾讯广告展示次数
   *             "clickAds": "csj"//用户应该点击的广告平台广告
   *         },
   *         "screen": "csj",//开屏广告使用哪家的，csj:穿山甲 bd:百度 tx:腾讯
   *         "content": "测试"//任务中心介绍内容
   */

  public Data data;
  public class Data {
    public AdsConfig adsConfig;
    public String screen;
    public String content;

    @Override
    public String toString() {
      return "Data{" +
        "adsConfig=" + adsConfig +
        ", screen='" + screen + '\'' +
        ", content='" + content + '\'' +
        '}';
    }
  }

  public class AdsConfig {

    public boolean tx;
    public boolean csj;
    public String csjTotal;
    public String txTotal;
    // 0:广点通 1:穿山甲 2:百度 3:adView
    public int clickAds;

    @Override
    public String toString() {
      return "AdsConfig{" +
        "tx=" + tx +
        ", csj=" + csj +
        ", csjTotal='" + csjTotal + '\'' +
        ", txTotal='" + txTotal + '\'' +
        ", clickAds=" + clickAds +
        '}';
    }
  }

  @Override
  public String toString() {
    return "AdsBean{" +
      "data=" + data +
      '}';
  }
}
