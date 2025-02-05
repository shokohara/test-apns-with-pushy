import io.netty.bootstrap.Bootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.channel.{
  ChannelHandlerContext,
  ChannelInboundHandlerAdapter,
  ChannelInitializer,
  SimpleChannelInboundHandler
}
import io.netty.handler.codec.http2.*
import io.netty.handler.ssl.*

import java.security.KeyStore
import javax.net.ssl.TrustManagerFactory

// Apnsが実際にHTTP2で通信しているかどうかを調べる
// 設定項目が多く実際にapi.sandbox.push.apple.comに通信するべき

@main def hello() =
  System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "DEBUG")
  assert(!OpenSsl.isAvailable) // SslProviderはJDKを使うことが確定
  val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm)
  trustManagerFactory.init(null.asInstanceOf[KeyStore])
  val sslContext = SslContextBuilder.forClient().sslProvider(SslProvider.JDK)
    .ciphers(Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
//    .keyManager(privateKey, privateKeyPassword, clientCertificate) // Apnsの呼び出しに使っている実際のものを持ってくる必要がある
//    .trustManager(trustManagerFactory) // Apnsでは設定されていない
//    .applicationProtocolConfig(new ApplicationProtocolConfig( // Apnsでは設定されていない
//      Protocol.ALPN,
//      SelectorFailureBehavior.NO_ADVERTISE,
//      SelectedListenerFailureBehavior.FATAL_ALERT,
//      ApplicationProtocolNames.HTTP_2
//    ))
    .build()
  val eventLoopGroup = new NioEventLoopGroup()
  val channelInitializer = new ChannelInitializer[SocketChannel]:
    override def initChannel(ch: SocketChannel): Unit =
      val c = new DefaultHttp2Connection(false)
      val handler = new HttpToHttp2ConnectionHandlerBuilder()
      ch.pipeline().addLast(sslContext.newHandler(ch.alloc(), "api.sandbox.push.apple.com", 443)) // peerHost
      ch.pipeline().addLast(Http2FrameCodecBuilder.forClient().build())
      ch.pipeline().addLast(
        new SimpleChannelInboundHandler[Http2Frame]():
          override def channelRead0(ctx: ChannelHandlerContext, msg: Http2Frame): Unit = msg match
            case settingsFrame: Http2SettingsFrame => println("Received settings frame: " + settingsFrame)
            case other => println("Received other frame: " + other)
      )
      ch.pipeline().addLast(
        new ChannelInboundHandlerAdapter:
          override def channelActive(ctx: ChannelHandlerContext): Unit =
            println("channelActive")
            super.channelActive(ctx)
          override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit =
            println("exceptionCaught")
            super.exceptionCaught(ctx, cause)
      )
  val bootstrap = new Bootstrap()
  bootstrap //
    .group(eventLoopGroup) //
    .channel(classOf[NioSocketChannel]) //
    .handler(channelInitializer)
  val channelFuture = bootstrap.connect("api.sandbox.push.apple.com", 443)
  //  channelFuture.addListener(f => println(f))
  channelFuture.channel().closeFuture().sync()
  channelFuture.cause().printStackTrace()
  println("after channelFuture.channel().closeFuture().sync()")
  eventLoopGroup.shutdownGracefully()
