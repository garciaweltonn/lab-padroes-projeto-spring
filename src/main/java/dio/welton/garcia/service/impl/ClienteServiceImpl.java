package dio.welton.garcia.service.impl;

import dio.welton.garcia.model.Cliente;
import dio.welton.garcia.model.Endereco;
import dio.welton.garcia.repository.ClienteRepository;
import dio.welton.garcia.repository.EnderecoRepository;
import dio.welton.garcia.service.ClienteService;
import dio.welton.garcia.service.ViaCepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

/**
 * Implementação da <b>Strategy</b> {@link ClienteService}, a qual pode ser
 * injetada pelo Spring (via {@link Autowired}). Com isso, como essa classe é um
 * {@link Service}, ela será tratada como um <b>Singleton</b>.
 *
 * @author falvojr e weltongarcia
 */

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    ClienteRepository clienteRepository;
    @Autowired
    EnderecoRepository enderecoRepository;
    @Autowired
    ViaCepService viaCepService;

    @Override
    public Iterable<Cliente> buscarTodos() {
        return clienteRepository.findAll();
    }

    @Override
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Cliente não encontrado."));
    }

    @Override
    public void inserir(Cliente cliente) {
        salvarClienteComCep(cliente);
    }

    @Override
    public void atualizar(Long id, Cliente cliente) {
        clienteRepository.findById(id).ifPresent(
                clienteBd -> {
                    salvarClienteComCep(cliente);
                });
    }

    @Override
    public String deletar(Long id) {
      return clienteRepository.findById(id).map(cliente -> {
          clienteRepository.deleteById(cliente.getId());
          return "Cliente deletado com sucesso.";
      }).orElseThrow(() -> new RuntimeException("Cliente não encontrado."));
    }

    private void salvarClienteComCep(Cliente cliente) {
        String cep = cliente.getEndereco().getCep();
        Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
            Endereco novoEndereco = viaCepService.consultarCep(cep);
            enderecoRepository.save(novoEndereco);
            return novoEndereco;
        });
        cliente.setEndereco(endereco);
        clienteRepository.save(cliente);
    }
}
