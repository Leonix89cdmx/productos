package com.zionyxerp.productos.services;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.zionyxerp.productos.dto.ProductRequest;
import com.zionyxerp.productos.dto.ProductResponse;
import com.zionyxerp.productos.entity.Product;
import com.zionyxerp.productos.exception.ProductNotFoundException;
import com.zionyxerp.productos.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import com.zionyxerp.productos.event.ProductEvent;
import com.zionyxerp.productos.event.ProductEventType;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;   // ← nuevo

    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse findById(Long id) {
        return toResponse(getProduct(id));
    }

     @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = new Product();
        apply(product, request);
        Product saved = productRepository.save(product);
        eventPublisher.publishEvent(ProductEvent.of(ProductEventType.PRODUCT_CREATED, saved));
        return toResponse(saved);
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = getProduct(id);
        apply(product, request);
        eventPublisher.publishEvent(ProductEvent.of(ProductEventType.PRODUCT_UPDATED, product));
        return toResponse(product);
    }

    @Transactional
    public void delete(Long id) {
        Product product = getProduct(id);
        product.setActive(false);
        eventPublisher.publishEvent(ProductEvent.of(ProductEventType.PRODUCT_DEACTIVATED, product));
    }

    // ---------- helpers ----------

    private Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    private void apply(Product product, ProductRequest request) {
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
    }

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.isActive());
    }
}