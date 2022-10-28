# Quarkus / Oracle / Flyway / Otel Issue Reproducer

## Requirements
- Docker (or working devservices with podman)
- Quarkus 2.13.3
- JDK 17
- GraalVM for native compile

## Testing
Running the application in dev mode with `mvn clean quarkus:dev -Dquarkus.profile=oracle` or 
`mvn clean quarkus:dev -Dquarkus.profile=oracle-otel` will work and all connections will be made
using dev services and the schema will be imported.

Compile to native with `mvn clean package -Pnative -Dquarkus.profile=native-oracle` and it will fail to start with
the following exception because it cannot connect to a database (expected):
```java
2022-10-28 17:01:02,075 WARN  [io.agr.pool] (agroal-11) Datasource 'named': IO Error: The Network Adapter could not establish the connection (CONNECTION_ID=hwUSh4nLSKW9fB0N75Xdjw==)
2022-10-28 17:01:02,078 ERROR [io.qua.run.Application] (main) Failed to start application (with profile native-oracle): java.net.ConnectException: Connection refused
        at com.oracle.svm.jni.JNIJavaCallWrappers.jniInvoke_VA_LIST_ConnectException_constructor_026ed3e065cc052585fca43de83265b2d1381f28(JNIJavaCallWrappers.java:0)
        at sun.nio.ch.Net.connect0(Net.java)
        at sun.nio.ch.Net.connect(Net.java:579)
        at sun.nio.ch.Net.connect(Net.java:586)
        at sun.nio.ch.SocketChannelImpl.connect(SocketChannelImpl.java:853)
        at java.nio.channels.SocketChannel.open(SocketChannel.java:285)
        at oracle.net.nt.TimeoutSocketChannel.connect(TimeoutSocketChannel.java:184)
        at oracle.net.nt.TimeoutSocketChannel.<init>(TimeoutSocketChannel.java:158)
        at oracle.net.nt.TcpNTAdapter.establishSocket(TcpNTAdapter.java:381)
        at oracle.net.nt.TcpNTAdapter.doLocalDNSLookupConnect(TcpNTAdapter.java:304)
        at oracle.net.nt.TcpNTAdapter.connect(TcpNTAdapter.java:266)
        at oracle.net.nt.ConnOption.connect(ConnOption.java:238)
        at oracle.net.nt.ConnStrategy.executeConnOption(ConnStrategy.java:967)
        at oracle.net.nt.ConnStrategy.execute(ConnStrategy.java:648)
        at oracle.net.resolver.AddrResolution.resolveAndExecute(AddrResolution.java:569)
        at oracle.net.ns.NSProtocol.establishConnection(NSProtocol.java:933)
        at oracle.net.ns.NSProtocol.connect(NSProtocol.java:346)
        at oracle.jdbc.driver.T4CConnection.connect(T4CConnection.java:2558)
        at oracle.jdbc.driver.T4CConnection.logon(T4CConnection.java:667)
        at oracle.jdbc.driver.PhysicalConnection.connect(PhysicalConnection.java:1089)
        at oracle.jdbc.driver.T4CDriverExtension.getConnection(T4CDriverExtension.java:90)
        at oracle.jdbc.driver.OracleDriver.connect(OracleDriver.java:733)
        at oracle.jdbc.driver.OracleDriver.connect(OracleDriver.java:649)
        at io.agroal.pool.ConnectionFactory.createConnection(ConnectionFactory.java:226)
        at io.agroal.pool.ConnectionPool$CreateConnectionTask.call(ConnectionPool.java:535)
        at io.agroal.pool.ConnectionPool$CreateConnectionTask.call(ConnectionPool.java:516)
        at java.util.concurrent.FutureTask.run(FutureTask.java:264)
        at io.agroal.pool.util.PriorityScheduledExecutor.beforeExecute(PriorityScheduledExecutor.java:75)
        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1134)
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:635)
        at java.lang.Thread.run(Thread.java:833)
        at com.oracle.svm.core.thread.PlatformThreads.threadStartRoutine(PlatformThreads.java:705)
        at com.oracle.svm.core.posix.thread.PosixPlatformThreads.pthreadStartRoutine(PosixPlatformThreads.java:202)
```

Compile to native with `mvn clean package -Pnative -Dquarkus.profile=native-oracle-otel` and it will fail to start
but the exception will be that it cannot find the driver (not expected):
```java
2022-10-28 17:05:19,359 WARN  [io.agr.pool] (agroal-11) Datasource 'named': Driver does not support the provided URL: jdbc:oracle:thin:@localhost:1521/named
2022-10-28 17:05:19,362 ERROR [io.qua.run.Application] (main) Failed to start application (with profile native-oracle-otel): java.sql.SQLException: Driver does not support the provided URL: jdbc:oracle:thin:@localhost:1521/named
        at io.agroal.pool.ConnectionFactory.connectionSetup(ConnectionFactory.java:242)
        at io.agroal.pool.ConnectionFactory.createConnection(ConnectionFactory.java:226)
        at io.agroal.pool.ConnectionPool$CreateConnectionTask.call(ConnectionPool.java:535)
        at io.agroal.pool.ConnectionPool$CreateConnectionTask.call(ConnectionPool.java:516)
        at java.util.concurrent.FutureTask.run(FutureTask.java:264)
        at io.agroal.pool.util.PriorityScheduledExecutor.beforeExecute(PriorityScheduledExecutor.java:75)
        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1134)
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:635)
        at java.lang.Thread.run(Thread.java:833)
        at com.oracle.svm.core.thread.PlatformThreads.threadStartRoutine(PlatformThreads.java:705)
        at com.oracle.svm.core.posix.thread.PosixPlatformThreads.pthreadStartRoutine(PosixPlatformThreads.java:202)
```
