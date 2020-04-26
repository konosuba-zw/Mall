package cn.e3mall.controller;

import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.pojo.TbContent;
import cn.e3mall.common.util.E3Result;
import cn.e3mall.content.service.ContentService;
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
public class ContentController {

    @Autowired
    private ContentService contentService;

    @RequestMapping(value = "/content/save",method = RequestMethod.POST)
    @ResponseBody
    public E3Result addContent(TbContent content){
        //调用服务把内容数据保存到数据库
        E3Result e3Result = contentService.addContent(content);
        return e3Result;
    }

    @RequestMapping("/content/query/list")
    @ResponseBody
    public EasyUIDataGridResult getContextList(Long categoryId,Integer page,Integer rows){
        EasyUIDataGridResult result = contentService.getContentList(categoryId, page, rows);
        return result;
    }

    @RequestMapping("/rest/content/edit")
    @ResponseBody
    public E3Result updateContent(TbContent tbContent){
        E3Result e3Result = contentService.updateContent(tbContent);
        return e3Result;
    }

    @RequestMapping("/content/delete")
    @ResponseBody
    public E3Result deleteContent(@RequestParam(value = "ids") List<Long> ids){
        E3Result e3Result = contentService.deleteContent(ids);
        return e3Result;
    }

}
