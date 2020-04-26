package cn.e3mall.service;

import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.pojo.TbItem;
import cn.e3mall.common.pojo.TbItemDesc;
import cn.e3mall.common.util.E3Result;

import java.util.List;

public interface ItemService {

    TbItem getItemById(long itemId);
    EasyUIDataGridResult getItemList(int page,int rows);
    E3Result addItem(TbItem item,String desc);
    E3Result updateItem(List<Long> ids,String method);
    E3Result getItemDesc(Long id);
    E3Result updateItem2(TbItem item,String desc);
    TbItemDesc getItemDescById(long itemId);
}
