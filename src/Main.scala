//> using dep com.eatthepath:pushy:0.15.0
//> using dep org.slf4j:slf4j-simple:2.0.16

import io.netty.bootstrap.Bootstrap
import io.netty.channel.{ChannelHandlerAdapter, ChannelHandlerContext, ChannelInitializer}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.http2.{DefaultHttp2Connection, HttpToHttp2ConnectionHandlerBuilder}

@main def hello() =
  System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "DEBUG")

  new ChannelInitializer[SocketChannel]:
    override def initChannel(ch: SocketChannel): Unit =
      val c = new DefaultHttp2Connection(false)
      val handler = new HttpToHttp2ConnectionHandlerBuilder()
      ch.pipeline().addLast()

def case1 =
  val b = new Bootstrap()
  b.group(new NioEventLoopGroup()).channel(classOf[NioSocketChannel]).handler(
    new ChannelInitializer[SocketChannel]:
      override def initChannel(ch: SocketChannel): Unit = ch.pipeline().addLast(
        new ChannelHandlerAdapter:
//          override def channelRead(ctx: ChannelHandlerContext, msg: Object): Unit =
//            println("hoge")
//            ctx.close()
          override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit =
            cause.printStackTrace()
            ctx.close()
      )
  )
  val f = b.connect("api.sandbox.push.apple.com", 443)
  f.channel().closeFuture().sync()
