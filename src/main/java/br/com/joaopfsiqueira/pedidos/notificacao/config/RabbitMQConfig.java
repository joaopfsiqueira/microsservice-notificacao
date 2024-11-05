package br.com.joaopfsiqueira.pedidos.notificacao.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.queue.name}")
    private String queueName;

    @Value("${rabbitmq.exchange.dlx.name}")
    private String exchangeDlxName;

    @Value("${rabbitmq.queue.dlq.name}")
    private String queueDlqName;

    @Bean
    public FanoutExchange pedidosExchange() {
        return new FanoutExchange(exchangeName);
    }

    @Bean
    public FanoutExchange pedidosExchangeDlx() {
        return new FanoutExchange(exchangeDlxName);
    }

    // Criação da fila
    @Bean
    public Queue notificacaoQueue() {

        // Configuração de argumentos para a fila, onde os argumentos no caso é uma fila deadletter.
        Map<String, Object> argumentos = new HashMap<>();
        argumentos.put("x-dead-letter-exchange", exchangeDlxName);

        return new Queue(queueName, true, false, false, argumentos);
    }


    // Criação da fila deadletter
    @Bean
    public Queue notificacaoQueueDlq() {
        return new Queue(queueDlqName);
    }


    // Criação do binding da fila com o exchange
    @Bean
    public Binding bindingQueue() {
        return BindingBuilder.bind(notificacaoQueue()).to(pedidosExchange());
    }

    // Criação do binding da fila deadletter com o exchange deadletter
    @Bean
    public Binding bindingQueueDlq() {
        return BindingBuilder.bind(notificacaoQueueDlq()).to(pedidosExchangeDlx());
    }

    // Criação do RabbitAdmin para inicializar o RabbitMQ
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory){
        return new RabbitAdmin(connectionFactory);
    }

    // Configuração do MessageConverter para enviar mensagens em JSON
    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    // Configuração do RabbitTemplate para enviar mensagens em JSON e inicializar o RabbitMQ
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    // Inicialização do RabbitMQ ao subir a aplicação
    @Bean
    public ApplicationListener<ApplicationReadyEvent> applicationReadyEventApplicationListener(RabbitAdmin rabbitAdmin){
        return event -> rabbitAdmin.initialize();
    }
}
