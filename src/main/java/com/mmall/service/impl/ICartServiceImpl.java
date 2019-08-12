package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResposeCode;
import com.mmall.common.ServiceRespose;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.List;

@Service("iCartService")
public class ICartServiceImpl implements ICartService {
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    /**
     *购物车添加商品
     * @param userId
     * @param count
     * @param productId
     * @return
     * 逻辑分析:进行正常的商品id和数量判断.如果不符合条件直接返回提示信息
     *          1.通过用户id和商品id查询购物车
     *          2.如果购物车为空,则insert购物车
     *          3.如果存在则把购物车的商品数量跟新添加的数量相加再update到数据库
     */
    public ServiceRespose<CartVo> add(Integer userId,Integer count,Integer productId){
        if (productId==null||count==null){
            return ServiceRespose.createByErrorCodeMsg(ResposeCode.ILLEGAL_ARGUMENT.getCode(),ResposeCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //查找数据库是否存在该购物车
        Cart cart=cartMapper.selectByUserIdAndProductId(userId,productId);
        if (cart==null){
            Cart cartItem=new Cart();
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setUserId(userId);
            cartItem.setProductId(productId);
            cartMapper.insert(cartItem);
        }else{
            //代表产品已经存在数量相加
            count=cart.getQuantity()+count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);
    }

    /**
     *更新商品数量
     * @param userId
     * @param count
     * @param productId
     * @return
     * 逻辑分析:判断购物车是否为空,如果不为空则更新数据库的count
     */
    public ServiceRespose<CartVo> update(Integer userId,Integer count, Integer productId){
        if (productId==null||count==null){
            return ServiceRespose.createByErrorCodeMsg(ResposeCode.ILLEGAL_ARGUMENT.getCode(),ResposeCode.ILLEGAL_ARGUMENT.getDesc());
        }
       Cart cart= cartMapper.selectByUserIdAndProductId(userId,productId);
        if (cart!=null){
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKeySelective(cart);
        return list(userId);
    }

    /**
     *
     * @param userId
     * @param productIds
     * @return
     * 逻辑分析:
     */
    public ServiceRespose<CartVo> delete(Integer userId,String productIds){
      List<String> productList= Splitter.on(",").splitToList(productIds);

        if (CollectionUtils.isEmpty(productList)){
            return ServiceRespose.createByErrorCodeMsg(ResposeCode.ILLEGAL_ARGUMENT.getCode(),ResposeCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteByUserIdProducts(userId,productList);
        return list(userId);
    }

    /**
     * 查询商品
     * @param userId
     * @return
     */
    public ServiceRespose<CartVo> list(Integer userId){
        CartVo cartVo=this.getCartVoLimit(userId);
        return ServiceRespose.createBySuccess(cartVo);
    }

    /**
     *
     * @param userId
     * @param checked
     * @return
     */
    public ServiceRespose<CartVo> allChecked(Integer userId,Integer checked){
        cartMapper.checkedAllOrUnCheckAll(userId,checked,null);
        return list(userId);
    }

    /**
     *
     * @param userId
     * @param checked
     * @param productId
     * @return
     */
    public ServiceRespose<CartVo> allOneChecked(Integer userId,Integer checked,Integer productId){
        cartMapper.checkedAllOrUnCheckAll(userId,checked,productId);
        return list(userId);
    }

    public ServiceRespose<Integer> selectCount(Integer userId){
        if (userId==null){
            return ServiceRespose.createBySuccess(0);
        }
        return ServiceRespose.createBySuccess(cartMapper.selectTotal(userId)) ;
    }

    /**
     *购物车购买数量和库存比较;商品总价;购物车总价
     * @param userId
     * @return
     */
    public CartVo getCartVoLimit(Integer userId){
        BigDecimal cartTotalPrice=new BigDecimal("0");
        CartVo cartVo=new CartVo();
        List<Cart> cartList= cartMapper.selectByUserId(userId);
       List<CartProductVo> cartProductVoList= Lists.newArrayList();

       for (Cart cartItem:cartList){
           CartProductVo cartProductVo=new CartProductVo();
           cartProductVo.setUserId(userId);
           cartProductVo.setProductId(cartItem.getProductId());
          cartProductVo.setId(cartItem.getId());
          Product product=productMapper.selectByPrimaryKey(cartItem.getProductId());
          if (product!=null){
              cartProductVo.setProductMainImage(product.getMainImage());
              cartProductVo.setProductName(product.getName());
              cartProductVo.setProductSubtitle(product.getSubtitle());
              cartProductVo.setProductStatus(product.getStatus());
              cartProductVo.setProductPrice(product.getPrice());
              cartProductVo.setProductStock(product.getStock());
              //判断库存
              int buyLimitCount=0;
              if (product.getStock()>=cartItem.getQuantity()){
                  //如果库存大于购物车里的数目,说明库存充足
                  buyLimitCount=cartItem.getQuantity();//赋值成我们要买的数量
                  cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
              }else {
                  buyLimitCount=product.getStock();
                  cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                  //购物车更新有效库存
                  Cart cartForQuantity=new Cart();
                  cartForQuantity.setId(cartItem.getId());
                  cartForQuantity.setQuantity(buyLimitCount);
                  cartMapper.updateByPrimaryKeySelective(cartForQuantity);
              }
              cartProductVo.setQuantity(buyLimitCount);
              //计算总价
              cartProductVo.setProductTotalPrice(BigDecimalUtil.mull(product.getPrice().doubleValue(),cartProductVo.getProductPrice().doubleValue()));
              cartProductVo.setProductChecked(cartItem.getChecked());
          }
          if (cartItem.getChecked()==Const.Cart.CHECKED){
              // todo 未解决的问题 这边参数报空
         // cartTotalPrice=BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
          }
          cartProductVoList.add(cartProductVo);
       }
       cartVo.setCartTotalPrice(cartTotalPrice);
       cartVo.setCartProductVoList(cartProductVoList);
       cartVo.setAllChecked(this.getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
       return cartVo;
    }



    /**
     *用来判断是否是全选状态
     * @param userId
     * @return
     */
    private boolean getAllCheckedStatus(Integer userId){
        if (userId==null){
            return false;
        }
        return cartMapper.selectCartPProductCheckedStatusByUserId(userId)==0;
    }
}
