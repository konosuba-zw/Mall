package cn.e3mall.content.service.impl;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.pojo.TbContent;
import cn.e3mall.common.pojo.TbContentExample;
import cn.e3mall.common.util.E3Result;
import cn.e3mall.common.util.JsonUtils;
import cn.e3mall.content.service.ContentService;
import cn.e3mall.mapper.TbContentMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 内容管理Service
 */
@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private TbContentMapper contentMapper;
    @Autowired
    private JedisClient jedisClient;

    @Value("${CONTENT_LIST}")
    private String CONTENT_LIST;

    @Override
    public E3Result addContent(TbContent content) {
        //将内容数据插入到内容表
        content.setCreated(new Date());
        content.setUpdated(new Date());
        //插入到数据库
        contentMapper.insert(content);
        //缓存同步（实现方式：删除缓存中对应的数据，下次加载后添加新的缓存）
        jedisClient.hdel(CONTENT_LIST,content.getCategoryId().toString());
        return E3Result.ok();
    }

    /**
     * 根据内容分类id查询内容列表，传给前台首页
     * @param cid
     * @return
     */
    @Override
    public List<TbContent> getContentListByCid(long cid) {
        //查询缓存（查询和缓存和添加缓存如果异常，不应该影响业务）
        try {
            //如果缓存中有，直接响应结果
            String json = jedisClient.hget(CONTENT_LIST, cid + "");
            if(StringUtils.isNoneBlank(json)){      //json不为空
                List<TbContent> list = JsonUtils.jsonToList(json, TbContent.class);
                return list;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //如果没有，查询数据库
        TbContentExample example = new TbContentExample();
        TbContentExample.Criteria criteria = example.createCriteria();
        criteria.andCategoryIdEqualTo(cid);
        List<TbContent> list = contentMapper.selectByExampleWithBLOBs(example);
        //把结果添加到缓存
        try {
            jedisClient.hset(CONTENT_LIST,cid + "", JsonUtils.objectToJson(list));
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public EasyUIDataGridResult getContentList(long categoryId, Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        TbContentExample example = new TbContentExample();
        TbContentExample.Criteria criteria = example.createCriteria();
        criteria.andCategoryIdEqualTo(categoryId);
        List<TbContent> list = contentMapper.selectByExample(example);
        PageInfo<TbContent> pageInfo = new PageInfo<>(list);
        EasyUIDataGridResult result = new EasyUIDataGridResult();
        result.setRows(list);
        result.setTotal((int)pageInfo.getTotal());
        return result;
    }

    @Override
    public E3Result updateContent(TbContent tbContent) {
        tbContent.setUpdated(new Date());
        contentMapper.updateByPrimaryKey(tbContent);
        return E3Result.ok();
    }

    @Override
    public E3Result deleteContent(List<Long> ids) {
        for(Long id : ids){
            contentMapper.deleteByPrimaryKey(id);
        }
        return E3Result.ok();
    }
}
