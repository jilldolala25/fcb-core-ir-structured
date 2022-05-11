package tw.com.fcb.dolala.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableFeignClients
//@EnableAsync
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class FcbCoreIRApplication {

    public static void main(String[] args) {
        SpringApplication.run(FcbCoreIRApplication.class, args);
    }

//    @Bean("myExecutor")
//    public Executor myExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(5);
//        executor.setMaxPoolSize(10);
//        executor.setQueueCapacity(200);
//        executor.setKeepAliveSeconds(60);
//        executor.initialize();
//        return executor;
//    }
//    @Bean("myExecutor2")
//    public Executor myExecutor2() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(5);
//        executor.setMaxPoolSize(10);
//        executor.setQueueCapacity(200);
//        executor.setKeepAliveSeconds(60);
//        executor.initialize();
//        return executor;
//    }
}
