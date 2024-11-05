package br.com.joaopfsiqueira.pedidos.notificacao.listener;

import br.com.joaopfsiqueira.pedidos.notificacao.entity.Pedido;
import br.com.joaopfsiqueira.pedidos.notificacao.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PedidoListener {

    private final Logger logger = LoggerFactory.getLogger(PedidoListener.class);

    private final EmailService emailService;

    public PedidoListener(EmailService emailService) {
        this.emailService = emailService;
    }


    // metodo que será chamado quando uma mensagem for recebida
    @RabbitListener(queues = "pedidos.v1.notificacao")
    public void enviarNotificacao(Pedido pedido){
        emailService.enviarEmail(pedido);
        logger.info("Notificacao gerada: {}", pedido.toString());
    }
}
