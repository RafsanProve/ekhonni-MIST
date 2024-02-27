package com.dsi.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.OneToOne;

public interface AppUserView {

    String getId();
    String getName();
    String getEmail();
    String getContact();
    String getAddress();
    String getDivision();
    String getClientStatus();
}