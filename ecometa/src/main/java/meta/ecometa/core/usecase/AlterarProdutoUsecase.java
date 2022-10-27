package meta.ecometa.core.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meta.ecometa.core.entity.Produto;
import meta.ecometa.infra.database.ProdutoRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlterarProdutoUsecase {
    private final ProdutoRepository repository;

    @Transactional
    public void execute(@NonNull final Produto produto){
        repository.findById(produto.getId()).ifPresentOrElse(persistent -> {
            BeanUtils.copyProperties(produto , persistent,"id");
            repository.save(persistent);
        },() -> log.warn("Produto não econtrado: {0}",produto.getId()));
    }

}