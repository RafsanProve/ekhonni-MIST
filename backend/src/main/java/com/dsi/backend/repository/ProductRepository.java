package com.dsi.backend.repository;

import com.dsi.backend.model.AppUser;
import com.dsi.backend.model.Product;
import com.dsi.backend.projection.ProductView;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, CustomRepository {

    List<Product> findByIsApprovedByAdminFalse();
    List<Product> findByIsApprovedByAdminTrueAndIsSoldFalse();
    List<ProductView> getByIsApprovedByAdminTrue();
    List<ProductView> findByIsApprovedByAdminIsNullOrderByProductTimeAsc();
    Product findByNameAndDescriptionAndSize(String name, String description, String size);
    List<Product> findByIsApprovedByAdminIsNull();
    List<ProductView> findByIsApprovedByAdminTrueAndIsSoldTrueAndCategoryCategory(String category);
    List<Product> findByIsApprovedByAdminTrueAndIsSoldTrueAndSellerDivision(String division);
    List<Product> findByIsApprovedByAdminTrueAndCategoryCategoryIn(List<String> categories);
    List<Product> findByIsApprovedByAdminTrueAndCategorySubCategoryIn(List<String> subCategory);
    List<Product> findByIsApprovedByAdminTrueAndSellerDivisionIn(List<String> division);
    List<Product> findByIsApprovedByAdminTrueAndStartingPriceBetween(Double minPrice, Double maxPrice);

    List<Product> findByIsApprovedByAdminTrueOrderByStartingPriceAsc();

    List<Product> findByIsApprovedByAdminTrueOrderByStartingPriceDesc();

    List<Product> findByIsApprovedByAdminTrueOrderByProductTimeAsc();

    List<Product> findByIsApprovedByAdminTrueOrderByProductTimeDesc();

    List<Product> findByIsApprovedByAdminTrueAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String searchKey1,String searchKey2);

    Product findProductById(Long id);

    List<ProductView> findAllBySeller(AppUser seller);

    List<ProductView> findAllByIsApprovedByAdminTrueAndIsBidActiveTrueAndIsSoldFalseAndIdIsIn(List<Long> products);

    @Modifying
    @Transactional
    @Query(value = "UPDATE product p SET is_bid_active = NOT(is_bid_active) WHERE p.id = :id", nativeQuery = true) // nativeQuery = true means it implements SQL query based on whatever DB we are using. In our case PostgresSQL
    void toggleIsBidActive(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE product p SET is_visible = NOT(is_visible) WHERE p.id = :id", nativeQuery = true)
    void toggleIsBidVisibility(Long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE product p SET final_buyer_id = :buyer_id WHERE p.id = :product_id", nativeQuery = true)
    void updateFinalBuyerId(Long buyer_id, Long product_id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE product p SET final_buyer_id = NULL WHERE p.id = :product_id", nativeQuery = true)
    void revertFinalBuyerId(Long product_id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE product p SET is_sold = true WHERE p.id = :product_id", nativeQuery = true)
    void changeIsSoldToTrue(Long product_id);

}
