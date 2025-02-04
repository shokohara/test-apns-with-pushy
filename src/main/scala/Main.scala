import io.netty.handler.ssl.*

@main def hello() =
  System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "DEBUG")
  println(OpenSsl.isAvailable)
