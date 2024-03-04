package com.dsi.backend.repository;

import com.dsi.backend.model.PaymentGatewayReq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentGatewayReqRepository extends JpaRepository<PaymentGatewayReq,String> {

}
