package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResposeCode;
import com.mmall.common.ServiceRespose;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ICategoryService iCategoryService;
    /**
     * 更新或者新增产品(后台)
     * @param product
     * @return
     *  * 4.调用业务层的saveOrUpdateProduct()方法,传参为Product对象;
     * 5.在业务层,最开始进行对product是否为空判断,如果为空则直接返回提示信息"参数错误";
     * 6.如果product不为空,那么我们进行相对应的操作;
     * 6.1用户会上传图片.在用户上传的后我们进行判断.如果子图不为空,那么我们创建一个String数组用来存放图片.同时以","分割开来;
     *    判断长度是否大于0如果大于0,则把子图的第一幅图给主图;
     * 6.2判断是否有商品id传进来,如果有则是更新商品信息,如果没有则insert商品信息;
     */
    public ServiceRespose saveOrUpdateProduct(Product product) {
        if (product.getCategoryId()==null||product.getName()==null||product.getPrice()==null||product.getStock()==null
        ||product.getStatus()==null) {
            return ServiceRespose.createByErrorMeg("更新或新增产品参数错误");
        } else {
            //如果product子图的不为空,我们就把子图赋值给主图
            if (StringUtils.isNotBlank(product.getSubImages())) {
                //分割子图
                String[] subImagesArray = product.getSubImages().split(",");
                if (subImagesArray.length > 0) {
                    product.setMainImage(subImagesArray[0]);
                }
            }
            //更新产品
            if (product.getId() != null) {
                int updateCount = productMapper.updateByPrimaryKey(product);
                if (updateCount > 0) {
                    return ServiceRespose.createBySuccessMsg("更新产品成功");
                } else {
                    return ServiceRespose.createByErrorMeg("更新产品失败");
                }
            } else {
                int insertCount = productMapper.insert(product);
                if (insertCount > 0) {
                    return ServiceRespose.createBySuccessMsg("新增产品成功");
                }
                return ServiceRespose.createByErrorMeg("新增产品失败");
            }
        }
    }

    /**
     *改变商品状态(在售,下架)(后台)
     * @param productId
     * @param status
     * @return
     * 业务层逻辑分析:1.判断传进来的id和状态是否为空,如果为空返回"商品参数错误",不为空进行业务操作;
     *              2.new一个product对象;
     *              3.把id和status set进product;
     *              4.调用productMapper的update方法;
     *              5.判断跟新的条数是否大于"1" 大于 则返回"更新成功" 否则返回"更新商品状态失败";
     */
    public ServiceRespose<String> setSaleStatus(Integer productId,Integer status){
        if (productId==null||status==null){
            return ServiceRespose.createByErrorMeg("商品参数错误");
        }
        Product product=new Product();
        product.setId(productId);
        product.setStatus(status);
       int updateCount= productMapper.updateByPrimaryKeySelective(product);
       if (updateCount>0){
           return ServiceRespose.createBySuccessMsg("更新商品状态成功");
       }
       return ServiceRespose.createByErrorMeg("更新商品状态失败");
    }

    /**
     * 根据商品id获取商品详情(后台)
     * @param productId
     * @return
     * 逻辑分析:1.判断商品id是否为空,为空返回"ILLEGAL_ARGUMENT"的code和desc;
     *         2.不为空:通过商品id查询数据库;
     *         3.如果未查到返回提示信息"商品已下架或不存在";
     *         4.返回我们想给用户展示的信息. 新增一个assembleProductDetailVo()方法.把Product转换为ProductVo对象;
     */
    public ServiceRespose<ProductDetailVo> getDetail(Integer productId){
        if (productId==null){
            return ServiceRespose.createByErrorCodeMsg(ResposeCode.ILLEGAL_ARGUMENT.getCode(),ResposeCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //根据商品id查找商品
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product==null){
            return ServiceRespose.createByErrorMeg("商品已下架或不存在");
        }
        //返回一个vo对象
        ProductDetailVo productDetailVo=this.assembleProductDetailVo(product);
        return ServiceRespose.createBySuccess(productDetailVo);
    }

    /**
     * 分页查询(后台)
     * @param pageNum
     * @param pageSize
     * @return
     *逻辑分析:分页查询使用了Page插件,我们只需把页码跟每页显示的多少条传进来即可;
     */
    public ServiceRespose<PageInfo> getList(Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList = productMapper.selectList();
        //创建一个数组,存放转换后的数据
        List<ProductListVo> productListVoList= Lists.newArrayList();
        for (Product productItem:productList){
          ProductListVo productListVo=this.assembleProductList(productItem);
          productListVoList.add(productListVo);
         }
         PageInfo pageInfo=new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServiceRespose.createBySuccess(pageInfo);

    }

    /**
     * 模糊查询商品(后台)
     * @param productId
     * @param productName
     * @param pageNum
     * @param pageSize
     * @return
     * 逻辑分析: 我们可以根据商品id或者商品的模糊查询商品名称;
     *          1.判断我们的商品名称是否存在,如果存在,则我们需要进行拼接把"%"拼接上去传给数据库进行模糊查询;
     *          2.调用数据库查询方法;
     *          3.把查询的商品通过循环完成转换以及完成需要显示的格式;
     *          4.把源数据传给PageInfo进行分页;
     *          5.重置PageInfo里面的List来给前端显示;
     */
    public ServiceRespose<PageInfo> searchProduct(Integer productId,String productName,Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        if (StringUtils.isNotBlank(productName)){
            productName=new StringBuilder().append("%").append(productName).append("%").toString();
        }
       List<Product> productList= productMapper.selectProductByNameOrId(productId,productName);
        List<ProductListVo> productListVoList= Lists.newArrayList();
        for (Product productItem:productList){
            ProductListVo productListVo=this.assembleProductList(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageInfoResult=new PageInfo(productList);
        pageInfoResult.setList(productListVoList);
        return ServiceRespose.createBySuccess(pageInfoResult);
    }

    /**
     * 通过商品id获取商品细节(门户)
     * @param productId
     * @return
     * 逻辑分析:管理员点击某一件商品时把商品id传进来;
     *          1.进行商品id判断,如果为空.则返回给客户端提示信息"ILLEGAL_ARGUMENT";
     *          2.不为空,进行查询.如果返回的product为空或者商品的status状态不为1时,那么返回给客户端提示信息"商品已下架或不存在";
     *          3.存在该商品则进行POJO->VO 调用已经写好的assembleProductDetailVo()方法;
     */
    public ServiceRespose<ProductDetailVo> getDetails(Integer productId){
        if (productId==null){
            return ServiceRespose.createByErrorCodeMsg(ResposeCode.ILLEGAL_ARGUMENT.getCode(),ResposeCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //根据商品id查找商品
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product==null||product.getStatus()== Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServiceRespose.createByErrorMeg("商品已下架或不存在");
        }
        //返回一个vo对象
        ProductDetailVo productDetailVo=this.assembleProductDetailVo(product);
        return ServiceRespose.createBySuccess(productDetailVo);
    }

    /**
     * 分页展示商品列表(包含模糊查询展示,点击分类展示商品,还要注意是否有降序or升序)(门户)
     * @param keyword
     * @param categoryId
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     * 逻辑分析:1.判断keyword(关键字),categoryId(类别id)是否为空,若为空返回提示信息"参数错误";
     *         2.如果categoryId不为空,则通过分类id查询数据库;如果为空则并且keyword为空则返回空,这里并不是错误.只是我们没有想要的数据;
     *         3.如果不为空,则调用已经在iCategoryService里面的迭代方法.同时new一个数组来存放迭代后的节点;
     *         4.判断keyword是否为空,如果不为空,我们在keyword前后拼接两个%%来模糊查询;
     *         5.分页查询的数目以及每页显示个数注入;
     *         6.判断orderBy(是否有降序升序),如果有升序或者降序传入;
     *         7.写dao方法来完成数据查询(传入的参数需要进行三目运算);
     *         8.通过for循环完成POJO->VO转换
     *         9.返回给前端客户端
     */
    public ServiceRespose<PageInfo> list(String keyword,Integer categoryId,Integer pageNum,Integer pageSize,String orderBy){
        //创建一个集合用来存放递归查询的id
        List<Integer> categoryIdList=Lists.newArrayList();
        if (categoryId==null&&StringUtils.isBlank(keyword)){
            return ServiceRespose.createByErrorCodeMsg(ResposeCode.ILLEGAL_ARGUMENT.getCode(),ResposeCode.ILLEGAL_ARGUMENT.getDesc());
        }
        if (categoryId!=null){
            Category category=categoryMapper.selectByPrimaryKey(categoryId);
            if (category==null&&StringUtils.isBlank(keyword)){
                //未找到该分类,并且还没有关键字.这个时候并不是错误.只是没有获取到.我们返回空即可
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVo> productListVoList=Lists.newArrayList();
                PageInfo pageInfo=new PageInfo(productListVoList);
                return ServiceRespose.createBySuccess(pageInfo);
            }
            categoryIdList=iCategoryService.selectCategoryAndChildrenById(category.getId()).getData();
        }
        if (StringUtils.isNotBlank(keyword)){
            keyword=new StringBuilder().append("%").append(keyword).append("%").toString();
        }
        PageHelper.startPage(pageNum,pageSize);
        if (StringUtils.isNotBlank(orderBy)){
            if (Const.orderBy.PRICE_ASC_DESC.contains(orderBy)){//如果orderBy下面包含传进来的orderby内容进行分割
               String[] orderByArray= orderBy.split("_");
                PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
            }
        }
       List<Product> productList= productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword)?null:keyword,categoryIdList.size()==0?null:categoryIdList);
        List<ProductListVo> productListVoList=Lists.newArrayList();
        for (Product item:productList){
            ProductListVo productListVo=assembleProductList(item);
            productListVoList.add(productListVo);
        }
        PageInfo pageInfo=new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServiceRespose.createBySuccess(pageInfo);
    }

    /**
     * 类型转换方法(Product->productDetail)
     * @param product
     * @return
     * 逻辑分析:展示我们想给用户看到的商品详情信息
     */
    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo=new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        Category category =categoryMapper.selectByPrimaryKey(product.getId());
        if (category==null){
            productDetailVo.setParentcetagoryId(0);
        }else {
            productDetailVo.setParentcetagoryId(category.getId());
        }
        productDetailVo.setCerateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }

    /**
     * 类型转换方法(Product->ProductList)通用方法
     * @param product
     * @return
     * 逻辑分析:返回的是我们定义过的ProductListVo对象(ProductListVo对象是我们想给客户端看到的字段);
     * 我们调用了BeanUtils的copyProperties方法直接把product的字段复制到源目标
     */
    private ProductListVo assembleProductList(Product product){
        ProductListVo productListVo=new ProductListVo();
        BeanUtils.copyProperties(product,productListVo);
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        return productListVo;
    }


}
