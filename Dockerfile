FROM amazoncorretto:17

# 컨테이너 기본 시간대를 한국으로 고정. amazoncorretto 기본값은 UTC 라
# LocalDateTime.now() (JPA Auditing createdAt/updatedAt) 가 9시간 밀려 저장됨.
ENV TZ=Asia/Seoul

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} /app.jar
COPY env/prod.env /env/prod.env

EXPOSE 8080
# 908MB 박스에 MySQL 이 같이 올라가므로 JVM 이 알아서 크게 잡지 못하게 못박는다.
#  -Xmx240m       : 컨테이너 기본값(RAM 의 25% ≈ 227m)과 사실상 같은 수준. 낮추는 게 아니라
#                   "MySQL 이 메모리를 가져가도 JVM 이 따라 늘지 않게" 고정하는 목적.
#  MaxMetaspace   : 클래스 로딩이 많아 상한이 없으면 계속 자란다.
#  UseSerialGC    : 1GB/2vCPU 급에서 G1 은 region 메타데이터로 수십 MB 를 더 쓴다.
#  MaxDirectMemory: S3 업로드·WebSocket 의 다이렉트 버퍼 상한.
# -Duser.timezone 으로 JVM 기본 시간대까지 KST 로 못박음 (TZ 미반영 환경 대비).
ENTRYPOINT ["java", \
  "-Xms160m", \
  "-Xmx240m", \
  "-XX:MaxMetaspaceSize=128m", \
  "-XX:+UseSerialGC", \
  "-XX:MaxDirectMemorySize=48m", \
  "-Duser.timezone=Asia/Seoul", \
  "-jar", "/app.jar"]
