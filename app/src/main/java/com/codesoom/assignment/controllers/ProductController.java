// REST
// /products -> Create, Read
// /products/{id} -> Read, Update, Delete

package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.application.ProductService;
import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.dto.ProductData;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/products")
@CrossOrigin
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> list() {
        return productService.getProducts();
    }

    @GetMapping("{id}")
    public Product detail(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public Product create(@RequestBody @Valid ProductData productData) {
        return productService.createProduct(productData);
    }

    @PatchMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public Product update(
            @PathVariable Long id,
            @RequestBody @Valid ProductData productData) {
        return productService.updateProduct(id, productData);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public void destroy(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
