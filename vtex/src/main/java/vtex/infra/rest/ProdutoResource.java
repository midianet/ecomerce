package vtex.infra.rest;

import lombok.RequiredArgsConstructor;
import vtex.core.entity.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import vtex.core.usecase.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/produtos")
public class ProdutoResource {
    private final ListarProduto listar;
    private final ObterProdutoPorId obterPorId;
    private final AlterarProduto alterar;
    private final NovoProduto novo;
    private final RemoverProduto remover;
    private final AlterarEstoqueProduto alterarEstoque;
    private final AlterarPrecoProduto alterarPreco;

    @GetMapping("/{id}")
    public Produto get(@PathVariable final String id){
        return obterPorId.execute(id);
    }

    @GetMapping
    public Page<Produto> find(@RequestParam(required = false) final String descricao,
                              @PageableDefault final Pageable pageable) {
        final var example = Produto.builder();
        Optional.ofNullable(descricao).ifPresent(example::descricao);
        return listar.execute(example.build(),pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void post(@Valid @RequestBody final NovoProduto.In produto,
                     final HttpServletResponse response) {
        final var persistent = novo.execute(produto);
        response.setHeader(HttpHeaders.LOCATION,
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(persistent.getId())
                        .toUri().toString()
        );
    }

    @PatchMapping("/{id}/preco")
    public void patchPreco(@PathVariable final String id, @RequestParam Double valor){
        alterarPreco.execute(id,valor);
    }

    @PatchMapping("/{id}/estoque")
    public void patchEstoque(@PathVariable final String id, @RequestParam Double quantidade){
        alterarEstoque.execute(id,quantidade);
    }

    @PutMapping("/{id}")
    public void put(@PathVariable final String id,
                    @Valid @RequestBody final AlterarProduto.In produto) {
        alterar.execute(id, produto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final String id) {
        remover.execute(id);
    }

}