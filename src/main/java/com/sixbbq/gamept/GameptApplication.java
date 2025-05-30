package com.sixbbq.gamept;

import com.sixbbq.gamept.exception.ServerDownException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GameptApplication {

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new ShutDownHook());
        SpringApplication.run(GameptApplication.class, args);
    }

    /*
     * 서버가 종료될 시 발동되는 메서드
     *
     * 다음과 같이 프로세스가 종료되는 경우에는 shutdown hook이 정상적으로 실행됩니다.
     * - System.exit() 에 의한 종료
     * - 사용자 인터럽트(ctrl+c)에 의한 JVM 종료
     * - 사용자 로그오프 또는 시스템 셧다운에 의한 종료
     * - SIGTERM 신호에 의한 종료
     * - handled되지 않은 Exception 발생으로 인한 프로세스 종료
     *
     * 그러나 다음과 같은 경우에는 shutdown hook이 실행되지 않고 프로세스가 종료되어 버리므로 주의해야 합니다.
     * - Runtime.halt() 에 의한 종료
     * - SIGKILL에 의한 종료 (kill -9 명령어 같은)을 받는 경우
     * - JVM에 문제가 발생하여 종료되는 경우
     */
    public static class ShutDownHook extends Thread {

        @Override
        public void run() {
            throw new ServerDownException("시스템 다운");
        }
    }
}
