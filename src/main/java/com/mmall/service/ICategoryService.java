package com.mmall.service;

import com.mmall.common.ServiceRespose;
import com.mmall.pojo.Category;

import java.util.List;

public interface ICategoryService {
    ServiceRespose addCategory(String categoryName, Integer parentId);
    ServiceRespose updateCategory(String categoryName,Integer categoryId);
    ServiceRespose<List<Category>> getChildrenParallelCategory(Integer categoryId);
    ServiceRespose<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);
}
