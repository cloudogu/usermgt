<configuration debug="false">
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <!-- encoders are  by default assigned the type
             ch.qos.logback.classic.encoder.PatternLayoutEncoder -->
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger - %msg%n</pattern>
        </encoder>
    </appender>

    <logger name="de.triology" level="{{ .Env.Get "LOGBACK_LEVEL" }}"/>

    <!-- Security -->
    <logger name="org.apache.shiro" level="INFO"/>

    <!-- RestEasy  -->
    <logger name="org.jboss" level="{{ .Env.Get "LOGBACK_LEVEL" }}" />

    <logger name="org.jasig.cas" level="{{ .Env.Get "LOGBACK_LEVEL" }}" />

    <logger name="org.hibernate" level="{{ .Env.Get "LOGBACK_LEVEL" }}" />

    <root level="INFO">
        <appender-ref ref="STDOUT"/>
    </root>

</configuration>