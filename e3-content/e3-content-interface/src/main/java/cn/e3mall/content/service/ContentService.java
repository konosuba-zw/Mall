package cn.e3mall.content.service;

import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.pojo.TbContent;
import cn.e3mall.common.util.E3Result;

import java.util.List;

public interface ContentService {

    E3Result addContent(TbContent content);
    List<TbContent> getContentListByCid(long cid);
    EasyUIDataGridResult getContentList(long categoryId,Integer page,Integer rows);
    E3Result updateContent(TbContent tbContent);
    E3Result deleteContent(List<Long> ids);
}
