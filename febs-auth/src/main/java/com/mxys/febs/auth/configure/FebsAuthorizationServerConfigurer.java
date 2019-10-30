package com.mxys.febs.auth.configure;

import com.mxys.febs.auth.properties.FebsAuthProperties;
import com.mxys.febs.auth.properties.FebsClientsProperties;
import com.mxys.febs.auth.services.FebsUserDetailService;
import com.mxys.febs.auth.translator.FebsWebResponseExceptionTranslator;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * FebsAuthorizationServerConfigurer继承AuthorizationServerConfigurerAdapter适配器，使用@EnableAuthorizationServer注解标注，开启认证服务器相关配置。
 */
@Configuration
@EnableAuthorizationServer
public class FebsAuthorizationServerConfigurer extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Autowired
    private FebsUserDetailService userDetailService;

    @Autowired
    private FebsAuthProperties authProperties;

    @Autowired
    private FebsWebResponseExceptionTranslator translator;

    @Override
    public void configure(ClientDetailsServiceConfigurer  clients) throws Exception {
       /* clients.inMemory().withClient("febs").secret(passwordEncoder.encode("123123"))
                .authorizedGrantTypes("password","refresh_token").scopes("all");*/
       FebsClientsProperties[] authArray=authProperties.getClients();
       InMemoryClientDetailsServiceBuilder serviceBuilder=clients.inMemory();

       if(ArrayUtils.isNotEmpty(authArray)){
            for (FebsClientsProperties client: authArray){
                if(StringUtils.isBlank(client.getClient())){
                    throw new Exception("client不能为空");
                }
                if(StringUtils.isBlank(client.getSecret())){
                    throw new Exception("secret不能为空");
                }
                //secret()需要使用passwordEncoder加密，否在报错Encoded password does not look like BCrypt
                String [] grantType=StringUtils.splitByWholeSeparatorPreserveAllTokens(client.getGrantType(),",");
                serviceBuilder.withClient(client.getClient()).secret(passwordEncoder.encode(client.getSecret())).authorizedGrantTypes(grantType)
                        .scopes(client.getScope());
            }
       }
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore()).userDetailsService(userDetailService).
                authenticationManager(authenticationManager).tokenServices(defaultTokenServices()).exceptionTranslator(translator);
    }
    @Bean
    public TokenStore tokenStore(){
        return new RedisTokenStore(redisConnectionFactory);
    }

    @Bean
    @Primary
    public DefaultTokenServices defaultTokenServices(){
        DefaultTokenServices defaultTokenServices=new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        defaultTokenServices.setAccessTokenValiditySeconds(authProperties.getAccessTokenValiditySeconds());
        defaultTokenServices.setRefreshTokenValiditySeconds(authProperties.getAccessTokenValiditySeconds());
        return defaultTokenServices;

    }
}
