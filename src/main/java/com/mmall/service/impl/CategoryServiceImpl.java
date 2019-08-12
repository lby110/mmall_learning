package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServiceRespose;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;


@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {
    private Logger logger=LoggerFactory.getLogger(CategoryServiceImpl.class);
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 添加商品
     * @param categoryName
     * @param parentId
     * @return
     */
    public ServiceRespose addCategory(String categoryName,Integer parentId){
        if (parentId==null|| StringUtils.isBlank(categoryName)){
            return ServiceRespose.createByErrorMeg("添加商品参数错误");
        }
        Category category=new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
      int rowCount=categoryMapper.insert(category);
      if (rowCount>0){
          return ServiceRespose.createBySuccessMsg("添加商品成功");
      }
      return ServiceRespose.createByErrorMeg("添加商品失败");
    }

    /**
     * 更新商品信息
     * @param categoryName
     * @param categoryId
     * @return
     */
    public ServiceRespose updateCategory(String categoryName,Integer categoryId){
        if (categoryId==null|| StringUtils.isBlank(categoryName)){
            return ServiceRespose.createByErrorMeg("更新商品参数错误");
        }
        Category category=new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int updateCount=categoryMapper.updateByPrimaryKeySelective(category);
        if (updateCount>0){
            return ServiceRespose.createBySuccessMsg("更新商品成功");
        }
        return ServiceRespose.createByErrorMeg("更新商品失败");
    }
    public ServiceRespose<List<Category>> getChildrenParallelCategory(Integer categoryId){
       List<Category> categoryList=categoryMapper.selectCategoryChildrenByParentId(categoryId);
       if (CollectionUtils.isEmpty(categoryList)){
        logger.info("未找到当前分类的子分类");
       }
       return ServiceRespose.createBySuccess(categoryList);
    }

    /**
     *递归查询本节点的id及孩子节点的id
     * @param categoryId
     * @return
     */
    public ServiceRespose<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){
        Set<Category> categorySet= Sets.newHashSet();
        findChildCategory(categorySet,categoryId);
        List<Integer> categoryList =Lists.newArrayList();
        if (categoryId!=null){
            for (Category categoryItem:categorySet){
                categoryList.add(categoryItem.getId());
            }
        }
        return ServiceRespose.createBySuccess(categoryList);
    }

    /**
     * 递归算法算出子节点
      * @param categorySet
     * @param categoryId
     * @return
     */
    public Set<Category> findChildCategory(Set<Category> categorySet,Integer categoryId){
      Category category=  categoryMapper.selectByPrimaryKey(categoryId);
      if (category!=null){
          categorySet.add(category);
      }
      //查找子节点,递归算法一定要有一个退出条件
      List<Category> categoryList=  categoryMapper.selectCategoryChildrenByParentId(categoryId);
      for (Category categoryItem:categoryList){
          findChildCategory(categorySet,categoryItem.getId());
      }
      return categorySet;
    }
}
