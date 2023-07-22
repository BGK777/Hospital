package com.atguigu.yygh.order.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "weixinpay")
public class WeixinPay {
    private String appid;
    private String partner;
    private String partnerkey;
}
