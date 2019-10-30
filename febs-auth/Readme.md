1)在pom配置中，我们指定了父项目为febs-cloud，并且引入了通用模块febs-common，
因为通用模块febs-common里已经包含了spring-cloud-starter-oauth2和spring-cloud-starter-security依赖，所以这里无需再次引入。

2)修改febs-cloud模块的pom，在modules标签里引入febs-auth：
    <modules>
        <module>../febs-common</module>
        <module>../febs-register</module>
        <module>../febs-auth</module>
    </modules>


3)因为后续我们需要将认证服务器生成的Token存储到Redis中，并且Redis依赖可能会被多个微服务使用到，于是我们在febs-common模块中引入redis相关依赖
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>

4.此外，我们需要将febs-auth服务注册到上一节搭建的febs-register中，所以我们在febs-common模块里继续添加Eureka相关依赖：
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    往Eureak服务端注册服务的应用称为Eureka客户端，所以上面引入的是Eureka Client相关依赖。

5.在febs-auth的入口类FebsAuthApplication上添加@EnableDiscoveryClient注解，用于开启服务注册与发现功能：
    @EnableDiscoveryClient

6.设置yml文件


7.首先我们需要定义一个WebSecurity类型的安全配置类FebsSecurityConfigure，在cc.mrbird.febs.auth路径下新增configure包，然后在configure包下新增FebsSecurityConfigure类，
    1.该类继承了WebSecurityConfigurerAdapter适配器，重写了几个方法，并且使用@EnableWebSecurity注解标注，开启了和Web相关的安全配置。
    2.代码中，我们首先注入了FebsUserDetailService，这个类下面会介绍到，这里先略过；然后我们定义了一个PasswordEncoder类型的Bean，该类是一个接口，定义了几个和密码加密校验相关的方法，这里我们使用的是Spring Security内部实现好的
    3.接着我们注册了一个authenticationManagerBean，因为密码模式需要使用到这个Bean。
    4.在FebsSecurityConfigure类中，我们还重写了WebSecurityConfigurerAdapter类的configure(HttpSecurity http)方法，其中requestMatchers().antMatchers("/oauth/**")的含义是：FebsSecurityConfigure安全配置类只对/oauth/开头的请求有效。
    5.最后我们重写了configure(AuthenticationManagerBuilder auth)方法，指定了userDetailsService和passwordEncoder
8.虽然我们现在正在搭建的是一个认证服务器，但是认证服务器本身也可以对外提供REST服务，比如通过Token获取当前登录用户信息，注销当前Token等，所以它也是一台资源服务器。于是我们需要定义一个资源服务器的配置类FebsResourceServerConfigurer

    设置FebsSecurityConfigure在FebsResourceServerConfigurer的启动顺序
    在FebsSecurityConfigure类里添加注解:@Order(2)
注：FebsSecurityConfigure用于处理/oauth开头的请求，Spring Cloud OAuth内部定义的获取令牌，刷新令牌的请求地址都是以/oauth/开头的，也就是说FebsSecurityConfigure用于处理和令牌相关的请求；
    FebsResourceServerConfigurer用于处理非/oauth/开头的请求，其主要用于资源的保护，客户端只能通过OAuth2协议发放的令牌来从资源服务器中获取受保护的资源。

9.接着我们定义一个和认证服务器相关的安全配置类。在configure包下新建FebsAuthorizationServerConfigurer
        1.FebsAuthorizationServerConfigurer继承AuthorizationServerConfigurerAdapter适配器，使用@EnableAuthorizationServer注解标注，开启认证服务器相关配置。
        2.由于之前我们在febs-common中引入了spring-boot-starter-data-redis依赖，而febs-auth模块引用了febs-common模块，所以在febs-auth的上下文中已经装配好了Redis相关配置，如RedisConnectionFactory（自动装配特性）。
          在febs-auth中采用的是Redis默认配置，所以你会发现我们并没有在配置类application.yml中编写和Redis有关的配置，但是为了更为直观，建议还是在application.yml中添加如下配置：
        3.在FebsAuthorizationServerConfigurer中，tokenStore使用的是RedisTokenStore，认证服务器生成的令牌将被存储到Redis中。
          defaultTokenServices指定了令牌的基本配置，比如令牌有效时间为60 * 60 * 24秒，刷新令牌有效时间为60 * 60 * 24 * 7秒，setSupportRefreshToken设置为true表示开启刷新令牌的支持。
        4.FebsAuthorizationServerConfigurer配置类中重点需要介绍的是configure(ClientDetailsServiceConfigurer clients)方法。该方法主要配置了：
            1.客户端从认证服务器获取令牌的时候，必须使用client_id为febs，client_secret为123456的标识来获取；
            2.该client_id支持password模式获取令牌，并且可以通过refresh_token来获取新的令牌；
            3.在获取client_id为febs的令牌的时候，scope只能指定为all，否则将获取失败；
            注：如果需要指定多个client，可以继续使用withClient配置。

10.定义好这三个配置类后，我们还需要定义一个用于校验用户名密码的类，也就是上面提到的FebsUserDetailService;新增service包，然后在service包下新增FebsUserDetailService类
    FebsUserDetailService实现了UserDetailsService接口的loadUserByUsername方法。loadUserByUsername方法返回一个UserDetails对象，该对象也是一个接口，包含一些用于描述用户信息的方法
        getAuthorities获取用户包含的权限，返回权限集合，权限是一个继承了GrantedAuthority的对象；
        getPassword和getUsername用于获取密码和用户名；
        isAccountNonExpired方法返回boolean类型，用于判断账户是否未过期，未过期返回true反之返回false；
        isAccountNonLocked方法用于判断账户是否未锁定；
        isCredentialsNonExpired用于判断用户凭证是否没过期，即密码是否未过期；
        isEnabled方法用于判断用户是否可用。
    在FebsUserDetailService的loadUserByUsername方法中，我们模拟了一个用户，用户名为用户输入的用户名，123123（后期再改造为从数据库中获取用户），然后返回org.springframework.security.core.userdetails.User。这里使用的是User类包含7个参数的构造器，其还包含一个三个参数的构造器

11.FebsUserDetailService中FebsAuthUser为我们自定义的用户实体类，代表我们从数据库中查询出来的用户。新增entity包，然后在entity包下新增FebsAuthUser

12.最后定义一个Controller，对外提供一些REST服务。在com.mxys.febs.auth路径下新增controller包，在controller包下新增SecurityController
    其中currentUser用户获取当前登录用户，signout方法通过ConsumerTokenServices来注销当前Token。
    FebsResponse为系统的统一相应格式，我们在febs-common模块中定义它，在febs-common模块的cc.mrbird.febs.common.entity路径下新增FebsResponse类：

13.FebsAuthException为自定义异常，在febs-common模块com.mxys.febs.common路径下新增exception包，然后在该包下新增FebsAuthException

PostMan测试:

    值为Basic加空格加(client_id:client_secret加密后的数据)（就是在FebsAuthorizationServerConfigurer类configure(ClientDetailsServiceConfigurer clients)方法中定义的client和secret）经过base64加密后的值（可以使用http://tool.oschina.net/encrypt?type=3）:




-----------------------------------------------------------------------------------------------------------------------
                                                配置化优化
在搭建febs-auth的时候，我们在认证服务器配置类FebsAuthorizationServerConfigurer里使用硬编码的形式配置了client_id，client_secret等信息。硬编码的形式不利于代码维护和升级，所以我们需要将它改造为可配置的方式。
1.在febs-auth模块的cc.mrbird.febs.auth路径下新建properties包，然后在该包下新建一个Client配置类FebsClientsProperties：
            private String client;
            private String secret;
            private String grantType = "password,authorization_code,refresh_token";
            private String scope = "all";
    client对应client_id，secret对应client_secret，grantType对应当前令牌支持的认证类型，scope对应认证范围。grantType和scope包含默认值。

2.接着新建一个和Auth相关的配置类FebsAuthProperties：
            @Data
            @SpringBootConfiguration
            @PropertySource(value = {"classpath:febs-auth.properties"})
            @ConfigurationProperties(prefix = "febs.auth")
            public class FebsAuthProperties {

                private FebsClientsProperties[] clients = {};
                private int accessTokenValiditySeconds = 60 * 60 * 24;
                private int refreshTokenValiditySeconds = 60 * 60 * 24 * 7;
            }

    clients属性类型为上面定义的FebsClientsProperties，因为一个认证服务器可以根据多种Client来发放对应的令牌，所以这个属性使用的是数组形式；accessTokenValiditySeconds用于指定access_token的有效时间，默认值为60 * 60 * 24秒；refreshTokenValiditySeconds用于指定refresh_token的有效时间，默认值为60 * 60 * 24 * 7秒。
    @PropertySource(value = {"classpath:febs-auth.properties"})用于指定读取的配置文件路径；
    @ConfigurationProperties(prefix = "febs.auth")指定了要读取的属性的统一前缀名称为febs.auth；
    @SpringBootConfiguration实质上为@Component的派生注解，用于将FebsAuthProperties纳入到IOC容器中。

3.自定义配置类还需引入spring-boot-configuration-processor依赖，因为这个依赖会在多个微服务子系统里使用到，所以将其添加到febs-common的pom文件中：
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-configuration-processor</artifactId>
                <optional>true</optional>
            </dependency>

4.接下来在febs-auth的resources路径下新建配置文件febs-auth.properties：
            febs.auth.accessTokenValiditySeconds=86400
            febs.auth.refreshTokenValiditySeconds=604800

            febs.auth.clients[0].client=febs
            febs.auth.clients[0].secret=123456
            febs.auth.clients[0].grantType=password,authorization_code,refresh_token
            febs.auth.clients[0].scope=all
   去除febs.auth前缀，剩下部分和FebsAuthProperties配置类属性名称对应上的话，就会被读取到FebsAuthProperties相应的属性中。数组形式的属性值使用[]加元素下标表示，具体可以参考properties文件的语法


5.定义好FebsAuthProperties配置类后，我们就可以在认证服务器配置类FebsAuthorizationServerConfigurer中注入使用了，改造FebsAuthorizationServerConfigurer
             @Autowired
             private FebsAuthProperties authProperties;
             @Override
                 public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
                     FebsClientsProperties[] clientsArray = authProperties.getClients();
                     InMemoryClientDetailsServiceBuilder builder = clients.inMemory();
                     if (ArrayUtils.isNotEmpty(clientsArray)) {
                         for (FebsClientsProperties client : clientsArray) {
                             if (StringUtils.isBlank(client.getClient())) {
                                 throw new Exception("client不能为空");
                             }
                             if (StringUtils.isBlank(client.getSecret())) {
                                 throw new Exception("secret不能为空");
                             }
                             String[] grantTypes = StringUtils.splitByWholeSeparatorPreserveAllTokens(client.getGrantType(), ",");
                             builder.withClient(client.getClient())
                                     .secret(passwordEncoder.encode(client.getSecret()))
                                     .authorizedGrantTypes(grantTypes)
                                     .scopes(client.getScope());
                         }
                     }
                 }
             @Primary
                 @Bean
                 public DefaultTokenServices defaultTokenServices() {
                        DefaultTokenServices tokenServices = new DefaultTokenServices();
                        tokenServices.setTokenStore(tokenStore());
                        tokenServices.setSupportRefreshToken(true);
                        tokenServices.setAccessTokenValiditySeconds(authProperties.getAccessTokenValiditySeconds());
                        tokenServices.setRefreshTokenValiditySeconds(authProperties.getRefreshTokenValiditySeconds());
                        return tokenServices;
                 }
    configure(ClientDetailsServiceConfigurer clients)方法由原先硬编码的形式改造成了从配置文件读取配置的形式，并且判断了client和secret不能为空；
    .secret(passwordEncoder.encode(client.getSecret()))需要加密,否则报：Encoded password does not look like BCrypt
    defaultTokenServices方法指定有效时间也从原先硬编码的形式改造成了从配置文件读取配置的形式。

---------------------------------------------------------
                        认证异常翻译
1.定义一个异常翻译器，将这些认证类型异常翻译为友好的格式。在febs-auth模块cc.mrbird.febs.auth路径下新建translator包，然后在该包下新建FebsWebResponseExceptionTranslator：
        @Slf4j
        @Component
        public class FebsWebResponseExceptionTranslator implements WebResponseExceptionTranslator {

            @Override
            public ResponseEntity translate(Exception e) {
                ResponseEntity.BodyBuilder status = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
                FebsResponse response = new FebsResponse();
                String message = "认证失败";
                log.error(message, e);
                if (e instanceof UnsupportedGrantTypeException) {
                    message = "不支持该认证类型";
                    return status.body(response.message(message));
                }
                if (e instanceof InvalidGrantException) {
                    if (StringUtils.containsIgnoreCase(e.getMessage(), "Invalid refresh token")) {
                        message = "refresh token无效";
                        return status.body(response.message(message));
                    }
                    if (StringUtils.containsIgnoreCase(e.getMessage(), "locked")) {
                        message = "用户已被锁定，请联系管理员";
                        return status.body(response.message(message));
                    }
                    message = "用户名或密码错误";
                    return status.body(response.message(message));
                }
                return status.body(response.message(message));
            }
        }

2.让这个异常翻译器生效，我们还需在认证服务器配置类FebsAuthorizationServerConfigurer的configure(AuthorizationServerEndpointsConfigurer endpoints)方法里指定它：

  @Configuration
  @EnableAuthorizationServer
  public class FebsAuthorizationServerConfigurer extends AuthorizationServerConfigurerAdapter {

      ......

      @Autowired
      private FebsWebResponseExceptionTranslator exceptionTranslator;

      ......

      @Override
      @SuppressWarnings("all")
      public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
          endpoints.tokenStore(tokenStore())
                  .userDetailsService(userDetailService)
                  .authenticationManager(authenticationManager)
                  .tokenServices(defaultTokenServices())
                  .exceptionTranslator(exceptionTranslator);
      }
      ......
  }


