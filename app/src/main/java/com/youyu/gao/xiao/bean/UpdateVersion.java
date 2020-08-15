package com.youyu.gao.xiao.bean;

/**
 * @Author zhisiyi
 * @Date 2020.08.01 14:17
 * @Comment
 */
public class UpdateVersion {

  public String appDesc;
  public String updateTitle;
  public int appVersion;
  public String downUrl;

  @Override
  public String toString() {
    return "UpdateVersion{" +
        "appDesc='" + appDesc + '\'' +
        ", updateTitle='" + updateTitle + '\'' +
        ", appVersion=" + appVersion +
        ", downUrl='" + downUrl + '\'' +
        '}';
  }
}
