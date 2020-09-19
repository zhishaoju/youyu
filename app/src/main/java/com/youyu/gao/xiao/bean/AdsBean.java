package com.youyu.gao.xiao.bean;

/**
 * @Author zhiyukai
 * @Date 2020.09.19 13:51
 * @Comment
 */
public class AdsBean {
  /*"adsConfig": {
    "tx": "true",
        "csj": "true"
  },
      "screen": "csj",
      "content":*/

  public AdsConfig adsConfig;
  public String screen;
  public String content;

  public class AdsConfig {

    public boolean tx;
    public boolean csj;
  }
}
