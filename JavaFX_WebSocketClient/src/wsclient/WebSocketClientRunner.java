package wsclient;

/*
* Copyright 2014 The Netty Project
*
* The Netty Project licenses this file to you under the Apache License,
* version 2.0 (the "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at:
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations
* under the License.
*/

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.ssl.SslHandler;
import application.Browser;
import application.LonLat;
import java.net.URI;
import java.util.Observable;
import java.util.Observer;

import javax.net.ssl.SSLEngine;

public final class WebSocketClientRunner extends Thread implements Observer {

    private final URI uri;
    private Browser browser;
    private String sendlonlat;
    
    private Object mon = new Object();

    public WebSocketClientRunner(URI uri, Browser browser) {
        this.uri = uri;
        this.browser = browser;
    }

    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // Connect with V13 (RFC 6455 aka HyBi-17). You can change it to V08 or V00.
            // If you change it to V00, ping is not supported and remember to change
            // HttpResponseDecoder to WebSocketHttpResponseDecoder in the pipeline.
            final WebSocketClientHandler handler =
                    new WebSocketClientHandler(
                            WebSocketClientHandshakerFactory.newHandshaker(
                                    uri, WebSocketVersion.V13, null, false, new DefaultHttpHeaders()), browser);

            final String protocol = uri.getScheme();
            int defaultPort;
            ChannelInitializer<SocketChannel> initializer;

            // Normal WebSocket
            if ("ws".equals(protocol)) {
                initializer = new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                          .addLast("http-codec", new HttpClientCodec())
                          .addLast("aggregator", new HttpObjectAggregator(8192))
                          .addLast("ws-handler", handler);
                    }
                };

                defaultPort = 80;
            // Secure WebSocket
            } else if ("wss".equals(protocol)) {
                initializer = new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        SSLEngine engine = WebSocketSslClientContextFactory.getContext().createSSLEngine();
                        engine.setUseClientMode(true);

                        ch.pipeline()
                          .addFirst("ssl", new SslHandler(engine))
                          .addLast("http-codec", new HttpClientCodec())
                          .addLast("aggregator", new HttpObjectAggregator(8192))
                          .addLast("ws-handler", handler);
                    }
                };

                defaultPort = 443;
            } else {
                throw new IllegalArgumentException("Unsupported protocol: " + protocol);
            }

            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .handler(initializer);

            int port = uri.getPort();
            // If no port was specified, we'll try the default port: https://tools.ietf.org/html/rfc6455#section-1.7
            if (uri.getPort() == -1) {
                port = defaultPort;
            }

            Channel ch = null;
			try {
				ch = b.connect(uri.getHost(), port).sync().channel();
				handler.handshakeFuture().sync();
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            

            // BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                synchronized(mon) {	
                	mon.wait();
                	WebSocketFrame frame = new TextWebSocketFrame(sendlonlat);
                	System.out.println("frame: "+frame);
                	System.out.println("sendlonlat: "+sendlonlat);
                	ch.writeAndFlush(frame);
                }
                
            }
        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
           group.shutdownGracefully();
        }
    }

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		LonLat lonlat = (LonLat) o;
		
		synchronized(mon) {
			sendlonlat = lonlat.getLon() + "," + lonlat.getLat();
			System.out.println("Observer update : " + sendlonlat);
			mon.notify();
		}
	}
}
