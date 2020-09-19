package com.youyu.gao.xiao.utils;

public interface Contants {

  boolean DEBUG = true;

  interface Net {

    String BASE_URL = "http://118.31.224.13:81";
    String REGISTER = "/youyu/api/user/register";
    String CODE = "/youyu/api/user/code";
    String LOGIN = "/youyu/api/user/login";
    String ACTIVE_DETAIL = "/youyu/api/activity/info";
    String POST_COMMENT_LIST = "/youyu/api/post/list";
    String COMMENT_DETAIL = "/youyu/api/post/info";
    String COMMENT_LIST = "/youyu/api/comment/list";
    String SEND_COMMENT = "/youyu/api/comment/save";
    String ACTIVITY_LIST = "/youyu/api/activity/list";
    String USER_INFO = "/youyu/api/user/info";
    String POST_UPDATE = "/youyu/api/post/update";
    String COLLECTION_LIST = "/youyu/api/collection/list";
    String COLLECTION_ADD = "/youyu/api/collection/add";
    String RECORD_LIST = "/youyu/api/record/list";
    String RECORD_ADD = "/youyu/api/video/record/add";
    String VERSION_INFO = "/youyu/api/version/info";
    String FEEDBACK = "/youyu/api/feedback/add";
    String NOTICE_ADS = "/youyu/api/notice/ads";
  }

  interface QiNiuKey {

    String AccessKey = "SGWgdjKSAL7Bufw-4OAccER14FM5JsY40INsQju6";//此处填你自己的AccessKey
    String SecretKey = "suzhiYBLtTmeH4bGzogs0rmabQ6MDuJ11h6VrXOT";//此处填你自己的SecretKey
    String PIC_UPLOAD_BASE_URL = "http://image.paidan2018.cn/";
  }

  interface NetStatus {

    int OK = 0;         // 操作成功
    int USER_NOT_EXIST = 1; // 用户不存在
    int USER_EXIST = 2; // 用户已经存在
  }

  String TOKEN = "token";
  String PHONE = "phone";
  String PASS = "pass";
  String BUYER_ID = "buyerId";

  String BANK_CODE = "bank_code";
  String TAO_BAO_CODE = "tao_bao_code";
  String BIND_BANK_FLAG = "bind_bank_flag";
  String BIND_TAO_BAO_FLAG = "bind_tao_bao_flag";

  String DATA_TOTAL = "data_total";

  String TASK_ID = "taskId";

//  String ORDER_REMARK = "order_remark";

  String TASK_CAPTAIL = "task_captail";

//  String PRAISE_TYPE = "praise_type";
//  String PRAISE_TEXT = "praise_text";
//  String PRAISE_PICPATH1 = "praise_picpath1";
//  String PRAISE_PICPATH2 = "praise_picpath2";
//  String PRAISE_PICPATH3 = "praise_picpath3";

  int PER_PAGE_NUM = 10;

  String COMPARE_GOODS_PIC_FLAG = "compare_goods_pic_flag";
  String TI_XIAN_FLAG = "ti_xian_flag";

  int REQUEST_PERMISSION_CODE = 101;

  int COUNT_DOWN_60 = 60;
  int DELAY_TIME = 1000;
  int COUNT_DOWN_WHAT = 100;
  int COUNT_DOWN_END_WHAT = 101;


  int CONTINUE_TASK = 2;
  int CANCEL_TASK = 1;
  int NET_REQUEST_TASK = 3;

  String USER_ID = "userId";
  String USER_PHONE = "user_phone";
  String USER_PASSWORD = "password";
  String POST_ID = "postId";

  String CSJ = "csj";
  String TX = "tx";

  interface BroadcastConst {

    String RECORD_ACTION = "record_action";
    String RECORD_STATUS = "recordStatus";
  }

  int PAGE_SIZE = 20;

  String PACKAGE_NAME = "com.youyu.gao.xiao";

  String APP_ID = "5101765";
  String APP_NAME = "APP测试媒体";
  String AD_BANNER = "887376831";
  String AD_CHUAN_SHA_JIA_REWARD_WAIT = "945477236";
  String AD_CHUAN_SHA_JIA_REWARD_TASK = "945493144";

  //开屏广告加载超时时间,建议大于3000,这里为了冷启动第一次加载到广告并且展示,示例设置了3000ms
  int AD_TIME_OUT = 3000;

  //
  String AD_TENCENT_DISPLAY = "4031932348485139";
  String AD_TENCENT_REWARD_WAIT = "5021733318699198";
  String AD_TENCENT_REWARD_TASK = "7091335308493349";


}
