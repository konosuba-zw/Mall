package cn.e3mall.service.impl;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.*;
import cn.e3mall.common.util.E3Result;
import cn.e3mall.common.util.IDUtils;
import cn.e3mall.common.util.JsonUtils;
import cn.e3mall.mapper.TbItemDescMapper;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.service.ItemService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.jms.*;
import java.util.Date;
import java.util.List;

/**
 * 商品管理Service
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private TbItemMapper tbItemMapper;
    @Autowired
    private TbItemDescMapper itemDescMapper;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Resource
    private Destination topicDestination;
    @Autowired
    private JedisClient jedisClient;

    @Value("${REDIS_ITEM_PRE}")
    private String REDIS_ITEM_PRE;
    @Value("${ITEM_CACHE_EXPIRE}")
    private Integer ITEM_CACHE_EXPIRE;

    @Override
    public TbItem getItemById(long itemId) {
        //查询缓存
        try{
            String json = jedisClient.get(REDIS_ITEM_PRE + ":" + itemId + ":BASE");
            if(StringUtils.isNotBlank(json)){
                TbItem tbItem = JsonUtils.jsonToPojo(json,TbItem.class);
                return tbItem;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //缓存中没有，查询数据库
        //根据主键查询
        //TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);
        //设置example条件
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        //设置查询条件
        criteria.andIdEqualTo(itemId);
        //执行查询
        List<TbItem> list = tbItemMapper.selectByExample(example);
        if(list != null && list.size()>0){
            //b把结果添加到缓存
            try {
                jedisClient.set(REDIS_ITEM_PRE+":"+itemId+":BASE", JsonUtils.objectToJson(list.get(0)));
                //设置过期时间
                jedisClient.expire(REDIS_ITEM_PRE+":"+itemId+":BASE",ITEM_CACHE_EXPIRE);
            }catch (Exception e){
                e.printStackTrace();
            }
            return list.get(0);
        }
        return null;
    }

    @Override
    public EasyUIDataGridResult getItemList(int page, int rows) {
        //设置分页信息
        PageHelper.startPage(page, rows);
        //执行查询
        TbItemExample example = new TbItemExample();
        List<TbItem> list = tbItemMapper.selectByExample(example);
        //创建一个返回值对象
        EasyUIDataGridResult result = new EasyUIDataGridResult();
        result.setRows(list);
        //取分页结果
        PageInfo<TbItem> pageInfo = new PageInfo<>(list);
        long total = pageInfo.getTotal();
        result.setTotal(total);
        return result;
    }

    @Override
    public E3Result addItem(TbItem item, String desc) {
        //生产商品id
        final long itemId = IDUtils.genItemId();
        //补全item属性
        item.setId(itemId);
        //1-正常，2-下架，3-删除
        item.setStatus((byte) 1);
        item.setCreated(new Date());
        item.setUpdated(new Date());
        //向商品表插入数据
        tbItemMapper.insert(item);
        //创建一个商品描述表对应的pojo对象
        TbItemDesc itemDesc = new TbItemDesc();
        //补全属性
        itemDesc.setItemId(itemId);
        itemDesc.setItemDesc(desc);
        itemDesc.setCreated(new Date());
        itemDesc.setUpdated(new Date());
        //向商品描述表插入数据
        itemDescMapper.insert(itemDesc);
        //发送一个商品添加消息
        jmsTemplate.send(topicDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage textMessage = session.createTextMessage(itemId + "");
                return textMessage;
            }
        });
        //返回成功
        return E3Result.ok();
    }

    /**
     * 根据id，更新商品状态，1-正常，2-下架，3-删除
     * @param ids
     * @param method
     * @return
     */
    @Override
    public E3Result updateItem(List<Long> ids, String method) {
        TbItem tbItem = new TbItem();
        if(method.equals("reshelf")){
            tbItem.setStatus((byte) 1);
        }else if(method.equals("instock")){
            tbItem.setStatus((byte) 2);
        }else if(method.equals("delete")){
            tbItem.setStatus((byte) 3);
        }
        for (Long id : ids){
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andIdEqualTo(id);
            tbItemMapper.updateByExampleSelective(tbItem,example);
        }
        return E3Result.ok();
    }

    @Override
    public E3Result getItemDesc(Long id) {
        TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(id);
        return  E3Result.ok(itemDesc);
    }

    @Override
    public E3Result updateItem2(TbItem item, String desc) {
        Long id = item.getId();
        TbItemExample tbItemExample = new TbItemExample();
        TbItemExample.Criteria criteria = tbItemExample.createCriteria();
        criteria.andIdEqualTo(id);
        tbItemMapper.updateByExampleSelective(item,tbItemExample);

        TbItemDesc itemDesc = new TbItemDesc();
        itemDesc.setItemDesc(desc);
        TbItemDescExample tbItemDescExample = new TbItemDescExample();
        TbItemDescExample.Criteria criteria1 = tbItemDescExample.createCriteria();
        criteria1.andItemIdEqualTo(id);
        itemDescMapper.updateByExampleSelective(itemDesc,tbItemDescExample);

        return E3Result.ok();
    }

    @Override
    public TbItemDesc getItemDescById(long itemId) {
        //先查缓存
        try{
            String json = jedisClient.get(REDIS_ITEM_PRE + ":" + itemId + ":DESC");
            if(StringUtils.isNotBlank(json)){
                TbItemDesc tbItemDesc = JsonUtils.jsonToPojo(json,TbItemDesc.class);
                return tbItemDesc;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(itemId);
        try {
            jedisClient.set(REDIS_ITEM_PRE+":"+itemId+":DESC", JsonUtils.objectToJson(itemDesc));
            //设置过期时间
            jedisClient.expire(REDIS_ITEM_PRE+":"+itemId+":DESC",ITEM_CACHE_EXPIRE);
        }catch (Exception e){
            e.printStackTrace();
        }
        return itemDesc;
    }
}
