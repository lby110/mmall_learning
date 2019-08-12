package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResposeCode;
import com.mmall.common.ServiceRespose;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/manage/product/")
public class ProductManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;

    /**
     * 保存商品
     * @param session:用来判断用户是否登录
     * @param product:提交的商品
     * @return
     * 思路分析:controller层:只需要判断用户是否登录,如果没有登录则返回我们已经定义的NEED_LOGIN的code和我们自己的msg
     * 1.如果用户已经登录而且是管理员我们进行业务操作.
     * 2.如果用户没有登录那么则直接返回给客户端提示信息:"无权限操作"
     * 3.在1.1的条件下进行调用service层业务逻辑
     */
    @RequestMapping(value = "save_product.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceRespose saveProduct(HttpSession session, Product product){
        //后台管理需要强制登陆
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServiceRespose.createByErrorCodeMsg(ResposeCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        /*进行是否管理员判断*/
        if (iUserService.checkAdminRole(user).isSuccess()){

          return   iProductService.saveOrUpdateProduct(product);
        }
        return ServiceRespose.createByErrorMeg("无权限操作");
    }

    /**
     * 根据商品id更新商品状态
     * @param session
     * @param productId
     * @param status
     * @return
     * 思路分析:根据名称可想而知只需要商品id跟状态两个参数即可
     * 1.依旧是判断用户是否登录,登录的账户是否是管理员
     * 2.进行业务层代码
     */
    @RequestMapping("set_sale_status.do")
    @ResponseBody
   public ServiceRespose<String> setSaleStatus(HttpSession session,Integer productId,Integer status){
       //后台管理需要强制登陆
       User user= (User) session.getAttribute(Const.CURRENT_USER);
       if (user==null){
           return ServiceRespose.createByErrorCodeMsg(ResposeCode.NEED_LOGIN.getCode(),"用户未登录");
       }
       /*进行是否管理员判断*/
       if (iUserService.checkAdminRole(user).isSuccess()){
           return   iProductService.setSaleStatus(productId,status);
       }
       return ServiceRespose.createByErrorMeg("无权限操作");
   }
    /**
     *获取产品详情
     * @param session
     * @param productId
     * @return
     * 业务分析:判断用户是否登录和是否是管理员用户
     *          2.调用业务层
     */
    @RequestMapping("get_detail.do")
    @ResponseBody
   public ServiceRespose getDetail(HttpSession session,Integer productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceRespose.createByErrorMeg("用户未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.getDetail(productId);
        }
        return ServiceRespose.createByErrorMeg("无权限操作");
   }

    /**
     * 分页查询商品
     * @param session
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("get_list.do")
    @ResponseBody
   public ServiceRespose getList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,@RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceRespose.createByErrorMeg("用户未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.getList(pageNum,pageSize);
        }
        return ServiceRespose.createByErrorMeg("无权限操作");
   }

    /**
     *模糊查询
     * 通过商品id或者商品名获取商品
     * @param session
     * @param productId
     * @param productName
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("search_product.do")
    @ResponseBody
   public ServiceRespose searchProduct(HttpSession session,Integer productId,String productName,@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,@RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceRespose.createByErrorMeg("用户未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.searchProduct(productId,productName,pageNum,pageSize);
        }
        return ServiceRespose.createByErrorMeg("无权限操作");
   }

    /**
     * 文本上传
     * @param session
     * @param file
     * @param request
     * @return
     */
    @RequestMapping("upload.do")
    @ResponseBody
   public ServiceRespose upload(HttpSession session,@RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceRespose.createByErrorMeg("用户未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            String path=request.getSession().getServletContext().getRealPath("upload");
            String targetFileName=iFileService.upload(file,path);
            String url= PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            Map fileMap= Maps.newHashMap();
            fileMap.put("uri",targetFileName);
            fileMap.put("url",url);
            return ServiceRespose.createBySuccess(fileMap);
        }
        return ServiceRespose.createByErrorMeg("无权限操作");
   }

    /**
     * 富文本上传
     * @param session
     * @param file
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("upload_img.do")
    @ResponseBody
   public Map richtextImgUpload(HttpSession session, @RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        Map resultMap=Maps.newHashMap();
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            resultMap.put("success",false);
            resultMap.put("msg","请登录管理员");
            return resultMap;
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            String path=request.getSession().getServletContext().getRealPath("upload");
            String targetFileName=iFileService.upload(file,path);
            if (StringUtils.isBlank(targetFileName)){
                resultMap.put("success",false);
                resultMap.put("msg","上传失败");
                response.addHeader("Access-Control-Allow-Headers","X-File-Name");
                return resultMap;
            }
            String url= PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            resultMap.put("success",true);
            resultMap.put("msg","上传成功");
            resultMap.put("file_path",url);
            return resultMap;
        }
        resultMap.put("success",false);
        resultMap.put("msg","无权限操作");
        return resultMap;
   }
}
