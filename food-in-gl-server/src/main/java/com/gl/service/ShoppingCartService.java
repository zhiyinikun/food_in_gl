package com.gl.service;

import com.gl.dto.ShoppingCartDTO;
import com.gl.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);


    List<ShoppingCart> showshoppingCart();

    void cleanShoppingCart();
}
