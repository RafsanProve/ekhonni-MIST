package com.dsi.backend.service.implententation;

import com.dsi.backend.exception.ProductNotFoundException;
import com.dsi.backend.model.*;

import com.dsi.backend.projection.ProductView;
import com.dsi.backend.repository.AppUserRepository;
import com.dsi.backend.repository.CategoryRepository;
import com.dsi.backend.repository.ProductRepository;
import com.dsi.backend.service.ImageModelService;
import com.dsi.backend.service.JwtTokenService;
import com.dsi.backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

import java.util.stream.Collectors;


@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private ImageModelService imageModelService;

    @Autowired
    private JwtTokenService jwtTokenService;

    public ProductView saveProduct(Product product, MultipartFile[] file, String token) {
        product.setIsApprovedByAdmin(null);
        product.setIsSold(false);
        product.setProductTime(LocalDateTime.now());
        Category category = categoryRepository.findByCategoryAndSubCategory(product.getCategory().getCategory(),product.getCategory().getSubCategory());
        if (category == null) {
            throw new IllegalArgumentException("Invalid category name");
        }
        AppUser seller = appUserRepository.findByEmail(jwtTokenService.getUsernameFromToken(token.substring(7)));
        product.setCategory(category);
        product.setSeller(seller);

        try {
            Set<ImageModel> image = imageModelService.uploadImage(file);
            product.setProductImage(image);

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            return null;
        }
        productRepository.save(product);
        ProjectionFactory projectionFactory = new SpelAwareProxyProjectionFactory();
        return projectionFactory.createProjection(ProductView.class, product);
    }

    public Product updateProduct(Long id, Boolean isApprovedByAdmin) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        existingProduct.setIsApprovedByAdmin(isApprovedByAdmin);
        if(isApprovedByAdmin) existingProduct.setIsBidActive(true); //bid activates immediately after being accepted
        return productRepository.save(existingProduct);
    }

    public List<ProductView> fetchAllRequests() {
        return productRepository.findByIsApprovedByAdminIsNullOrderByProductTimeAsc();
    }

    public ProductView getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(()->new ProductNotFoundException("Product not found by id: "+id));
//        Product product = productRepository.findById(id);
        ProjectionFactory projectionFactory = new SpelAwareProxyProjectionFactory();
        return projectionFactory.createProjection(ProductView.class, product);

    }

    @Override
    public Product fetchProductById(Long id){
        return productRepository.findById(id)
                .orElseThrow(()->new ProductNotFoundException("Product not found by id: "+id));
    }

    public Map<String,Long> countProducts(String division) {
        List<Product> product;
        if(Objects.equals(division, "")) {
            product =productRepository.findByIsApprovedByAdminTrue();
        } else {
            product= productRepository.findByIsApprovedByAdminTrueAndSellerDivision(division);
        }
        return product.stream().collect(Collectors.groupingBy(Product::getCategoryName, Collectors.counting()));
    }

    public List<ProductView> showByCategory(String category){
        return productRepository.findByIsApprovedByAdminTrueAndCategoryCategory(category);
    }

    @Override
    public Category insertCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public ResponseEntity<?> removeCategory(Category category) {
        categoryRepository.delete(category);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public Page<ProductView> filterProduct(FilterRequest filterRequest) {
        return productRepository.filter(filterRequest);
    }

}
