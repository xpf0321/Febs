1)搭建注册中心
  运行入口类FebsRegisterApplication的main方法启动项目，启动后访问 http://localhost:8001/register/，
  出现Eureka页面说明微服务注册中心搭建成功，目前还没有微服务实例注册进来，所以列表是空的。
2)使用Security保护微服务注册中心
   目前Eureka服务端是“裸奔着”的，只要知道了Eureka服务端的地址后便可以将微服务注册进来，我们可以引入spring-cloud-starter-security来保护Eureka服务端。
   1.在febs-register模块的pom文件中添加如下依赖：
   <dependency>
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-starter-security</artifactId>
   </dependency>
   2.在com.mxys.febs.register路径下新建configure包，然后在configure包下新建FebsRegisterWebSecurityConfigure配置类：
       1.添加@EnableWebSecurity注解
       2.继承WebSecurityConfigurerAdapter类
       3.实现configure(HttpSecurity http)接口
       4.在return之前添加语句   ======http.csrf().ignoringAntMatchers("/eureka/**");======
       5.修改application.yml文件
            添加：spring.security.user.name=***
                  spring.security.user.password=***
            修改访问路径：http://${spring.security.user.name}${spring.security.user.password}@${eureka.instance.hostname}:${server.port}${server.servlet.context-path}/eureka/


