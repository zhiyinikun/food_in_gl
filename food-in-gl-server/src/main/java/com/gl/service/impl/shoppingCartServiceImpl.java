package com.gl.service.impl;

import com.gl.context.BaseContext;
import com.gl.dto.ShoppingCartDTO;
import com.gl.entity.Dish;
import com.gl.entity.Setmeal;
import com.gl.entity.ShoppingCart;
import com.gl.mapper.DishMapper;
import com.gl.mapper.SetmealMapper;
import com.gl.mapper.ShoppingCartMapper;
import com.gl.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class shoppingCartServiceImpl implements ShoppingCartService {
    /**
     * 添加购物车
     * @param shoppingCartDTO
     */

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //判新当前加入到购物车中的商品是否已经存在了
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        //通过线程取用户id
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

        //如果已经存在了，只需要将数量加一
        if(list != null && list.size() >0){
            //取数据
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber()+1);
            shoppingCartMapper.updateNumberById(cart);
        }else {
            //如果不存在，需要插入一条购物车数据

            //判断加入的是菜品或者套餐
            Long dishId = shoppingCartDTO.getDishId();
            if(dishId != null){
                //为菜品

                //通过dish表查询菜品的数据
                Dish dish = dishMapper.getById(dishId);

                //分别赋值给需要新增的shoppingCart必要的前端没用传入的数据
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());

                shoppingCart.setNumber(1);
                shoppingCart.setCreateTime(LocalDateTime.now());
            }else {
                //为套餐
                Long setmealId = shoppingCartDTO.getSetmealId();
                Setmeal setmeal = setmealMapper.getById(setmealId);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());

                shoppingCart.setNumber(1);
                shoppingCart.setCreateTime(LocalDateTime.now());
            }
            //统一插入数据
            shoppingCartMapper.insert(shoppingCart);


        }
    }

    /**
     * 查看购物车
     * @return
     */
    @Override
    public List<ShoppingCart> showshoppingCart() {
        Long userId = BaseContext.getCurrentId();

        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(userId)
                .build();
        //复用
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        return list;
    }

    /**
     * 清空购物车
     */
    @Override
    public void cleanShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteByUserId(userId);
    }
}
