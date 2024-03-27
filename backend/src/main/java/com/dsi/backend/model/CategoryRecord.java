package com.dsi.backend.model;

import java.util.List;

public record CategoryRecord(String name, List<String> subCategories) {
}