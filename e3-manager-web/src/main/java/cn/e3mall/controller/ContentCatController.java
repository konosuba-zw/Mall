package cn.e3mall.controller;

import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.common.util.E3Result;
import cn.e3mall.content.service.ContentCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 内容管理Controller
 */
@Controller
public class ContentCatController {

    @Autowired
    private ContentCategoryService contentCategoryService;

    @RequestMapping("/content/category/list")
    @ResponseBody
    public List<EasyUITreeNode> getContentCatList(@RequestParam(name = "id",defaultValue = "0") Long parentId){
        List<EasyUITreeNode> list = contentCategoryService.getContentCatList(parentId);
        return list;
    }

    /**
     * 添加分类节点
     */
    @RequestMapping(value = "/content/category/create",method = RequestMethod.POST)
    @ResponseBody
    public E3Result createContentCategory(Long parentId,String name){
        //调用服务添加节点
        E3Result e3Result = contentCategoryService.addContentCategory(parentId,name);
        return e3Result;
    }

    /**
     * 删除分类节点
     */
    @RequestMapping("/content/category/delete/")
    @ResponseBody
    public E3Result deleteContentCategory(Long id){
        E3Result e3Result = contentCategoryService.deleteContentCategory(id);
        return e3Result;
    }

    /**
     * 编辑（改名）分类节点
     */
    @RequestMapping("/content/category/update")
    @ResponseBody
    public E3Result updateContentCategory(Long id,String name){
        E3Result e3Result = contentCategoryService.updateContentCategory(id,name);
        return e3Result;
    }

}
