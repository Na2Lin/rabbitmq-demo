
### spring amqp 第一个demo
---
- 安装 启动rabbitmq
   - docker 启动 rabbitmq server
   ```
   docker pull rabbitmq:3.8-management
   docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 -v /opt/rabbitmq/conf:/etc/rabbitmq rabbitmq:3.8-management
   ```
   - 进入容器并开启managment
   ```
   docker exec -it rabbitmq bash
   rabbitmq-plugins list # 列出可用的插件
   rabbitmq-plugins enable rabbitmq_management rabbitmq_management_agent rabbitmq_web_dispatch # 开启web管理
   ``` 
  - 本地访问 http://localhost:15672 并输入用户、密码登录
  - 创建交换器（exchange）dead_letter_exchange ,队列（queue） dead_letter_queue 并添加dead_letter_exchange的路由键dead_letter_key到dead_letter_queue；
     创建交换器（exchange）exchange1 ,队列（queue） queue2 ,并设置queue2的x-dead-letter-exchange=dead_letter_exchange，x-dead-letter-routing-key=dead_letter_key  并添加exchange1的路由键test2到queue2；
---
- 创建java gradle项目
   - 创建项目目录
    ```  mkdir rabbit-demo  ```
   - 进入目录，并初始化项目
    ```
    cd rabbit-demo
    spring init --name rabbit-demo --artifactId rabbit-demo --groupId com.tml.test --boot-version 2.4.6 --java-version 11 --type gradle-project --extract 
    ```
- 配置项目
   - vscode 打开项目并添加依赖
    ```
    dependencies {
      implementation 'org.springframework.boot:spring-boot-starter'
      implementation 'org.springframework.boot:spring-boot-starter-amqp'
      implementation 'org.springframework.boot:spring-boot-starter-web'
      testImplementation 'org.springframework.boot:spring-boot-starter-test'
    }
    ```
   - application.yml配置rabbit连接
   ```
    server:
      port: 8080
    spring:
      rabbitmq:
        host: 127.0.0.1 #连接rabbitmq server地址
        port: 5672 # 连接端口 默认5672
        username: user # 用户 默认guest
        password: password # guest用户密码 guest
        virtual-host: /
        listener:
          simple:
            acknowledge-mode: auto # 自动ack
            retry:
              enabled: true # 开启重试
              max-attempts: 5 # 最大重试次数 
              max-interval: 10000 # 最大重试间隔时间
              initial-interval: 2000 # 最小重试间隔时间
              multiplier: 2 # 重试间隔时间增加因子 （上次间隔时间*这个数=本次间隔时间，但不会超过最大间隔时间）
   ```
   - 启动类添加MessageConverter(实现消息对象与json互转)，MessageRecoverer(实现消息重试最大次数后，消息转移到指定的死信队列)
   ```
    @Bean
    MessageConverter createMessageConverter() {
      return new Jackson2JsonMessageConverter();
    }


    @Bean
    MessageRecoverer messageRecoverer(RabbitTemplate rabbitTemplate) {
      // 这里的死信交换器和路由键对应前面创建的dead_letter_exchange交换器和dead_letter_key路由键
      return new RepublishMessageRecoverer(rabbitTemplate,"dead_letter_exchange", "dead_letter_key");
    }
   ```
- 发送消息
   - 创建Message对象
   ```
    public class Message {
        private String name;

        private Date date;

        public String getName() {
            return name;
        }
        public Message(String name, Date date) {
          this.name = name;
          this.date = date;
        }

        public Message() {
        }
        public void setName(String name) {
            this.name = name;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
        
    }
   ```
   - 添加HelloWorldController
   ```
    @RestController
    @RequestMapping(value="/hello")
    public class HelloWorldController {

      private static String EXCHANGE_NAME="exchange1"; // 交换器名称
      private static String ROUTER_KEY="test2"; // 绑定路由键

      @Autowired
      RabbitTemplate rabbitTemplate; // 添加spring boot amqp并配置rabbit连接后自动注入

      @GetMapping
      public String hello() {
          Message message = new Message();
          message.setDate(new  Date());
          message.setName("name");
          rabbitTemplate.convertAndSend(EXCHANGE_NAME, "ROUTER_KEY",message); // 发送消息
          return "Hello World!";
      }
    }
   ```
- 接收消息
   - 创建消息监听
   ```
    @Component
    public class MessageListenner {
      Logger log = LoggerFactory.getLogger(MessageListenner.class);

      // @RabbitHandler
      // queues 指定监听的queue名称， concurrency指定开启多少个线程接收消息
      @RabbitListener(queues = "queue2",concurrency = "1")
      public void helloMessage(Message message) {
        log.info("接收到消息：{}", message);
      }
    }
   ```
---
- 启动测试
   - 打包项目
   ```
   gradle build
   ```
   - 启动项目
   ```
   java -jar build/libs/rabbitmq-demo-0.0.1-SNAPSHOT.jar
   ```
   - 打开浏览器访问 http://localhost:8080/hello, 观察控制台打印信息