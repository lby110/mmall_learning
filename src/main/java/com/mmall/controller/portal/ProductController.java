package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServiceRespose;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/product/")
public class ProductController {
    @Autowired
    private IProductService iProductService;

    /**
     * 前端获取商品详情
     * @param productId
     * @return
     * 逻辑分析:简单的获取商品细节
     */
    @RequestMapping("get_details.do")
    @ResponseBody
    public ServiceRespose<ProductDetailVo> detail(Integer productId){
        return iProductService.getDetails(productId);
    }

    /**
     * 显示模糊查询or类别查询后的数据
     * @param keyword
     * @param categoryId
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     */
    @RequestMapping("get_list.do")
    @ResponseBody
    public ServiceRespose<PageInfo> list(@RequestParam(value = "keyword",required = false)String keyword,
                                         @RequestParam(value = "categoryId",required = false)Integer categoryId,
                                         @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                                         @RequestParam(value = "keyword",required = false)String orderBy
                               ){
        return iProductService.list(keyword,categoryId,pageNum,pageSize,orderBy);
    }
}
