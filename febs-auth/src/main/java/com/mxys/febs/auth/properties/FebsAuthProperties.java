package com.mxys.febs.auth.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

/**
 * 配置认证属性资源类
 * 注：自定义配置类还需引入spring-boot-configuration-processor依赖，因为这个依赖会在多个微服务子系统里使用到，所以将其添加到febs-common的pom文件中：
 */
@Data
@SpringBootConfiguration//实质上为@Component的派生注解，用于将FebsAuthProperties纳入到IOC容器中。
@PropertySource(value = "classpath:febs-auth.properties")//用于指定读取的配置文件路径；
@ConfigurationProperties(prefix = "febs.auth")//指定了要读取的属性的统一前缀名称为febs.auth
public class FebsAuthProperties {

    /**
     * clients属性类型为上面定义的FebsClientsProperties，因为一个认证服务器可以根据多种Client来发放对应的令牌，所以这个属性使用的是数组形式；
     */
    private FebsClientsProperties[] clients={};
    /**
     * accessTokenValiditySeconds用于指定access_token的有效时间，默认值为60 * 60 * 24秒；
     */
    private int accessTokenValiditySeconds=60*60*24;

    /**
     * refreshTokenValiditySeconds用于指定refresh_token的有效时间，默认值为60 * 60 * 24 * 7秒。
     */
    private int refreshTokenValiditySeconds=60*60*24*7;

}
