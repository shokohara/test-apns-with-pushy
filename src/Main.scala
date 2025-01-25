//> using dep com.eatthepath:pushy:0.15.0

import io.netty.bootstrap.Bootstrap
import io.netty.channel.{ChannelHandlerAdapter, ChannelHandlerContext, ChannelInitializer}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel

@main def hello() =
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
