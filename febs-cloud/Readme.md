1)因为febs-cloud模块是项目的父模块，仅用于聚合子模块，所以我们可以把src目录下的内容全部删了，
    只保留pom.xml（febs-cloud.iml为IDEA文件，不能删哦），然后修改pom.xml，引入Spring Boot和Spring Cloud：
     <!--引入springboot 2.0+框架-->
        <parent>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-parent</artifactId>
            <version>2.1.9.RELEASE</version>
            <relativePath/> <!-- lookup parent from repository -->
        </parent>

        <properties>
            <spring-cloud.version>Greenwich.SR1</spring-cloud.version>
        </properties>

        <dependencyManagement>
            <dependencies>
                <dependency>
                    <groupId>org.springframework.cloud</groupId>
                    <artifactId>spring-cloud-dependencies</artifactId>
                    <version>${spring-cloud.version}</version>
                    <type>pom</type>
                    <scope>import</scope>
                </dependency>
            </dependencies>
        </dependencyManagement>

2)pom配置中，我们指定了packaging为pom，表示这是一个纯聚合模块，无需打包为jar或者war；
  name指定为FEBS-Cloud；引入了Spring Boot 2.1.6.RELEASE和Spring Cloud Greenwich.SR1；
  引入了spring-boot-maven-plugin打包插件，用于后续打包子项目。
  至此，父模块搭建完毕，接下来开始搭建通用模块。