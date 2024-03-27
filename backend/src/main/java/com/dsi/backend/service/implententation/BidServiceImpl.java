package com.dsi.backend.service.implententation;

import com.dsi.backend.model.AppUser;
import com.dsi.backend.model.Bid;
import com.dsi.backend.model.Product;
import com.dsi.backend.projection.ProductView;
import com.dsi.backend.repository.AppUserRepository;
import com.dsi.backend.repository.BidRepository;
import com.dsi.backend.repository.ProductRepository;
import com.dsi.backend.service.BidService;
import com.dsi.backend.service.NotificationService;
import com.dsi.backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BidServiceImpl implements BidService{

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private NotificationService notificationService;

    @Override
    public Bid saveBid(Long id, String buyerEmail, Double offeredPrice) {
        Product product = productRepository.findProductById(id);
        AppUser buyer = appUserRepository.findByEmail(buyerEmail);

        if (Objects.equals(product.getSeller().getEmail(), buyerEmail)){
            return null;
        }

        if (!product.getIsBidActive() || product.getIsSold()){
            return null;
        }

        Bid bid = bidRepository.findByProductIdAndBuyerEmail(id, buyerEmail);
        if (bid != null) {
            bidRepository.delete(bid);
        }
        Bid newBid = new Bid(product, buyer, offeredPrice);
        return bidRepository.save(newBid);
    }

    @Override
    public List<Optional<Bid>> fetchBids(Long id, String email) {

        Product product = productRepository.findProductById(id);

        if(Objects.equals(product.getSeller().getEmail(), email)) {
            return bidRepository.findAllByProductIdOrderByOfferedPriceDesc(product.getId());
        }
        else {
            if (product.getIsVisible()) {
                return bidRepository.findAllByProductIdOrderByOfferedPriceDesc(product.getId());
            }
            else {
                List<Optional<Bid>> bidList = new ArrayList<>();

                Optional<Bid> maxBid = bidRepository.findTopByProductIdOrderByOfferedPriceDesc(id);
                if (maxBid.isEmpty()) {
                    return bidList;
                }
                bidList.add(maxBid);

                Optional<Bid> buyerBid = Optional.ofNullable(bidRepository.findByProductIdAndBuyerEmail(id, email));
                if (buyerBid.isEmpty()) {
                    return bidList;
                }
                bidList.add(buyerBid);

                //bidList.sort(Comparator.comparingDouble(Bid::getOfferedPrice));
                return bidList;
            }
        }
    }

    @Override
    public Boolean changeBidActiveStatus(Long id, String sellerEmail) {
        Product product = productRepository.findProductById(id);

        if (Objects.equals(product.getSeller().getEmail(), sellerEmail)){
            productRepository.toggleIsBidActive(id);
            return !product.getIsBidActive();
        }
        return null;
    }

    @Override
    public Boolean changeBidVisibilityStatus(Long id, String sellerEmail) {
        Product product = productRepository.findProductById(id);

        if (Objects.equals(product.getSeller().getEmail(), sellerEmail)){
            productRepository.toggleIsBidVisibility(id);
            return !product.getIsVisible();
        }
        return null;
    }

    @Override
    public AppUser updateFinalBuyer(Long id, String sellerEmail, String buyerEmail) {
        Product product = productRepository.findProductById(id);
        AppUser buyer = appUserRepository.findByEmail(buyerEmail);

        if (Objects.equals(product.getSeller().getEmail(), sellerEmail)){
            productRepository.updateFinalBuyerId(buyer.getId(), id);
            notificationService.saveAcceptOrRevertBidNotification(buyer, product, false);
            return buyer;
        }
        return null;
    }

    @Override
    public Boolean revertFinalBuyer(Long id, String sellerEmail) {
        Product product = productRepository.findProductById(id);

        if (Objects.equals(product.getSeller().getEmail(), sellerEmail)){
            AppUser buyer = appUserRepository.findById(product.getFinalBuyerId())
                    .orElseThrow(()->new UsernameNotFoundException("Buyer id does not exist"));
            notificationService.saveAcceptOrRevertBidNotification(buyer, product, true);
            productRepository.revertFinalBuyerId(id);
            return true;
        }
        return false;
    }

    @Override
    public Boolean changeIsSold(Long id, String buyerEmail) {
        Product product = productRepository.findProductById(id);
        AppUser buyer = appUserRepository.findByEmail(buyerEmail);

        if (Objects.equals(product.getFinalBuyerId(), buyer.getId())){
            productRepository.changeIsSoldToTrue(id);
            return true;
        }
        return false;
    }

    @Override
    public Bid fetchBidByProductAndBuyer(Long id, String buyerEmail) {
        return bidRepository.findByProductIdAndBuyerEmail(id, buyerEmail);
    }

    @Override
    public List<Bid> buyerBids(String buyerEmail) {
        AppUser buyer = appUserRepository.findByEmail(buyerEmail);
        return bidRepository.findAllByBuyer(buyer);
    }

    @Override
    public ResponseEntity<?> popularProducts() {
        List<Long> productId = bidRepository.allPopularBidsOrderByCountDesc();
        List<ProductView> productViewList = productRepository.findAllByIsApprovedByAdminTrueAndIsBidActiveTrueAndIsSoldFalseAndIdIsIn(productId);
        return ResponseEntity.ok(productViewList);

    }
}
