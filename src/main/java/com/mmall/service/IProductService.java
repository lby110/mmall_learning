package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServiceRespose;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

public interface IProductService {
    ServiceRespose saveOrUpdateProduct(Product product);
    ServiceRespose<String> setSaleStatus(Integer productId,Integer status);
    ServiceRespose<ProductDetailVo> getDetail(Integer productId);
    ServiceRespose<PageInfo> getList(Integer pageNum, Integer pageSize);
    ServiceRespose<PageInfo> searchProduct(Integer productId,String productName,Integer pageNum,Integer pageSize);
    ServiceRespose<ProductDetailVo> getDetails(Integer productId);
    ServiceRespose<PageInfo> list(String keyword,Integer categoryId,Integer pageNum,Integer pageSize,String orderBy);
}
