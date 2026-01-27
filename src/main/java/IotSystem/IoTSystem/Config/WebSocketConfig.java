package IotSystem.IoTSystem.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private WebSocketAuthInterceptor webSocketAuthInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple in-memory message broker to carry messages back to the client
        config.enableSimpleBroker("/topic", "/queue");
        // Prefix for messages from client to server
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register the /ws endpoint, enabling SockJS fallback options
        // Disable JSONP transport as it doesn't support origin checking
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS()
                .setTransportHandlers(
                        new org.springframework.web.socket.sockjs.transport.handler.WebSocketTransportHandler(new org.springframework.web.socket.server.support.DefaultHandshakeHandler()),
                        new org.springframework.web.socket.sockjs.transport.handler.XhrStreamingTransportHandler(),
                        new org.springframework.web.socket.sockjs.transport.handler.XhrPollingTransportHandler()
                );
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // Add interceptor for authentication
        registration.interceptors(webSocketAuthInterceptor);
    }
}

