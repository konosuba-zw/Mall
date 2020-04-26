package cn.e3mall.controller;

import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.pojo.TbItem;
import cn.e3mall.common.util.E3Result;
import cn.e3mall.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ItemController {

    @Autowired
    private ItemService itemService;

    @RequestMapping("/item/{itemId}")
    @ResponseBody
    public TbItem getItemById(@PathVariable Long itemId){
        TbItem tbItem = itemService.getItemById(itemId);
        return tbItem;
    }

    @RequestMapping("/item/list")
    @ResponseBody
    public EasyUIDataGridResult getItemList(Integer page,Integer rows){
        //调用服务查询商品列表
        EasyUIDataGridResult result = itemService.getItemList(page, rows);
        return result;
    }

    /**
     * 商品添加功能
     */
    @RequestMapping(value = "/item/save",method = RequestMethod.POST)
    @ResponseBody
    public E3Result addItem(TbItem item,String desc){
        E3Result result = itemService.addItem(item, desc);
        return result;
    }

    /**
     * 商品更新功能
     */
    @RequestMapping("/rest/item/{method}")
    @ResponseBody
    public E3Result updateItemStatus(@RequestParam(value = "ids")List<Long> ids,@PathVariable String method){
        E3Result result = itemService.updateItem(ids, method);
        return result;
    }

    /**
     * 根据id获取商品描述
     */
    @RequestMapping("/rest/item/query/item/desc/{id}")
    @ResponseBody
    public E3Result getItemDesc(@PathVariable("id") Long id){
        E3Result result = itemService.getItemDesc(id);
        return result;
    }

    @RequestMapping("/rest/item/update")
    @ResponseBody
    public E3Result updateItem(TbItem item,String desc){
        E3Result result = itemService.updateItem2(item, desc);
        return result;
    }
}
