package com.dsi.backend.repository.implementation;

import com.dsi.backend.model.CategoryRecord;
import com.dsi.backend.model.FilterRequest;
import com.dsi.backend.model.Product;
import com.dsi.backend.projection.ProductView;
import com.dsi.backend.repository.CustomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CustomRepositoryImpl implements CustomRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<ProductView> filter(FilterRequest f) {
        Pageable pageable = PageRequest.of(f.pageNumber(), 5);
        StringBuilder sql = new StringBuilder("where p.isApprovedByAdmin=true ");

        if (f.startPrice() < f.endPrice()) {
            sql.append("and (p.startingPrice between :start_price and :end_price) ");
        }

        List<String> categoryNames = new ArrayList<>();
        List<String> subCategoryNames = new ArrayList<>();

        if (f.categories() != null && !f.categories().isEmpty()) {
            for (CategoryRecord c : f.categories()) {
                if (c.subCategories() != null && !c.subCategories().isEmpty()) {
                    subCategoryNames.addAll(c.subCategories());
                } else {
                    categoryNames.add(c.name());
                }
            }

            if (!categoryNames.isEmpty() && !subCategoryNames.isEmpty()) {
                sql.append("and (p.category.category in(:category_names) or p.category.subCategory in(:sub_category_names)) ");
            } else if(categoryNames.isEmpty()) {
                sql.append("and p.category.subCategory in(:sub_category_names) ");
            } else {
                sql.append("and p.category.category in(:category_names) ");
            }
        }

        if (f.division() != null && !f.division().isEmpty()) {
            sql.append("and p.seller.division in(:divisions) ");
        }

        if (StringUtils.hasText(f.search())) {
            sql.append("and lower(p.name) like :search ");
        }

        TypedQuery<Long> totalQuery = em.createQuery("select count(p) from Product p " + sql, Long.class);
        populateParams(totalQuery, f,  categoryNames, subCategoryNames);
        Long total = totalQuery.getSingleResult();

        if(StringUtils.hasText(f.sort())) {
            switch (f.sort()) {
                case "High_to_low" -> sql.append("order by p.startingPrice desc ");
                case "Low_to_high" -> sql.append("order by p.startingPrice ");
                case "Old_to_new" -> sql.append("order by p.productTime desc ");
                case "New_to_Old" -> sql.append("order by p.productTime ");
                default -> sql.append("order by p.name ");
            }
        }

        TypedQuery<Product> productQuery = em.createQuery("select p from Product p " + sql, Product.class);
        populateParams(productQuery, f, categoryNames, subCategoryNames);
        productQuery.setFirstResult((int) pageable.getOffset());
        productQuery.setMaxResults(pageable.getPageSize());
        List<Product> products = productQuery.getResultList();

        List<ProductView> productViews = products.stream()
                .map(product -> new SpelAwareProxyProjectionFactory().createProjection(ProductView.class,product))
                .toList();

        return new PageImpl<>(productViews, pageable, total);
    }

    private void populateParams(TypedQuery<?> query, FilterRequest f, List<String> categoryNames, List<String> subCategoryNames ) {

        if (f.startPrice() < f.endPrice()) {
            query.setParameter("start_price", f.startPrice());
            query.setParameter("end_price", f.endPrice());
        }

        if (f.categories() != null && !f.categories().isEmpty()) {
            if (!categoryNames.isEmpty() && !subCategoryNames.isEmpty()) {
                query.setParameter("category_names", categoryNames);
                query.setParameter("sub_category_names", subCategoryNames);
            } else if(categoryNames.isEmpty()) {
                query.setParameter("sub_category_names", subCategoryNames);
            } else {
                query.setParameter("category_names", categoryNames);
            }
        }

        if (f.division() != null && !f.division().isEmpty()) {
            query.setParameter("divisions", f.division());
        }

        if (StringUtils.hasText(f.search())) {
            query.setParameter("search", "%" + f.search().trim().toLowerCase() + "%");
        }
    }
}
