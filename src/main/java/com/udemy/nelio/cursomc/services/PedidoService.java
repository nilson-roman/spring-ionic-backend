package com.udemy.nelio.cursomc.services;

import com.udemy.nelio.cursomc.domain.Cliente;
import com.udemy.nelio.cursomc.domain.ItemPedido;
import com.udemy.nelio.cursomc.domain.PagamentoComBoleto;
import com.udemy.nelio.cursomc.domain.Pedido;
import com.udemy.nelio.cursomc.domain.enums.EstadoPagamento;
import com.udemy.nelio.cursomc.repositories.ItemPedidoRepository;
import com.udemy.nelio.cursomc.repositories.PagamentoRepository;
import com.udemy.nelio.cursomc.repositories.PedidoRepository;
import com.udemy.nelio.cursomc.security.UserSS;
import com.udemy.nelio.cursomc.services.exceptions.AuthorizationException;
import com.udemy.nelio.cursomc.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

import static org.springframework.data.domain.Sort.Direction;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository repository;

    @Autowired
    private BoletoService boletoService;

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private EmailService emailService;

    public Pedido find(Integer id) {
        Optional<Pedido> obj = repository.findById(id);
        return obj.orElseThrow(() -> new ObjectNotFoundException(
                "Objeto não encontrado! Id: " + id + ", Tipo: " + Pedido.class.getName()));
    }

    public Pedido insert(Pedido obj) {
        obj.setId(null);
        obj.setInstante(new Date());
        obj.setCliente(clienteService.find(obj.getCliente().getId()));
        obj.getPagamento().setEstado(EstadoPagamento.PENDENTE);
        obj.getPagamento().setPedido(obj);
        if (obj.getPagamento() instanceof PagamentoComBoleto) {
            PagamentoComBoleto pagto = (PagamentoComBoleto) obj.getPagamento();
            boletoService.preencherPagamentoComBoleto(pagto, obj.getInstante());
        }
        obj = repository.save(obj);
        pagamentoRepository.save(obj.getPagamento());
        for (ItemPedido ip : obj.getItens()) {
            ip.setDesconto(0.0);
            ip.setProduto(produtoService.find(ip.getProduto().getId()));
            ip.setPreco(ip.getProduto().getPreco());
            ip.setPedido(obj);
        }
        itemPedidoRepository.saveAll(obj.getItens());
        emailService.sendOrderConfirmationHtmlEmail(obj);
        return obj;
    }

    public Page<Pedido> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
        UserSS user = UserService.authenticated();
        if (user == null) {
            throw new AuthorizationException("Acesso negado");
        }
        PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
        Cliente cliente =  clienteService.find(user.getId());
        return repository.findByCliente(cliente, pageRequest);
    }
}
