package cn.e3mall.content.service.impl;

import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.common.pojo.TbContentCategory;
import cn.e3mall.common.pojo.TbContentCategoryExample;
import cn.e3mall.common.util.E3Result;
import cn.e3mall.content.service.ContentCategoryService;
import cn.e3mall.mapper.TbContentCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 内容分类管理
 */
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {

    @Autowired
    private TbContentCategoryMapper contentCategoryMapper;

    @Override
    public List<EasyUITreeNode> getContentCatList(long parentId) {
        //根据parentId查询子节点列表
        TbContentCategoryExample example = new TbContentCategoryExample();
        TbContentCategoryExample.Criteria criteria = example.createCriteria();
        //设置查询条件
        criteria.andParentIdEqualTo(parentId);
        //执行查询
        List<TbContentCategory> catList = contentCategoryMapper.selectByExample(example);
        //转换成EasyUITreeNode的列表
        List<EasyUITreeNode> nodeList = new ArrayList<>();
        for (TbContentCategory tbContentCategory : catList){
            EasyUITreeNode node = new EasyUITreeNode();
            node.setId(tbContentCategory.getId());
            node.setText(tbContentCategory.getName());
            node.setState(tbContentCategory.getIsParent()?"closed":"open");
            //添加到列表
            nodeList.add(node);
        }
        return nodeList;
    }

    @Override
    public E3Result addContentCategory(long patentId, String name) {
        //创建一个tb_content_category表对应的pojo对象
        TbContentCategory contentCategory = new TbContentCategory();
        //设置pojo属性
        contentCategory.setParentId(patentId);
        contentCategory.setName(name);
        contentCategory.setStatus(1);
        //默认排序为1
        contentCategory.setSortOrder(1);
        //新添加的节点一定是叶子节点
        contentCategory.setIsParent(false);
        contentCategory.setCreated(new Date());
        contentCategory.setUpdated(new Date());
        //插入数据库
        contentCategoryMapper.insert(contentCategory);
        //判断父节点的isparent属性，如果不是true改为true
        //根据parentId查询父节点
        TbContentCategory parent = contentCategoryMapper.selectByPrimaryKey(patentId);
        if(!parent.getIsParent()){
            parent.setIsParent(true);
            //更新到数据库中
            contentCategoryMapper.updateByPrimaryKey(parent);
        }
        //返回结果，返回E3result包含pojo
        return E3Result.ok(contentCategory);
    }

    @Override
    public E3Result deleteContentCategory(long id) {
        //查询父节点
        long parentId = contentCategoryMapper.selectByPrimaryKey(id).getParentId();
        //删除节点
        contentCategoryMapper.deleteByPrimaryKey(id);
        //查看父节点是否有其他子节点，没有则将isparent设为false
        TbContentCategoryExample example = new TbContentCategoryExample();
        TbContentCategoryExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);
        if(list.isEmpty()){
            TbContentCategory parentCat = contentCategoryMapper.selectByPrimaryKey(parentId);
            parentCat.setIsParent(false);
            contentCategoryMapper.updateByPrimaryKey(parentCat);
        }
        return E3Result.ok();
    }

    @Override
    public E3Result updateContentCategory(long id, String name) {
        TbContentCategory contentCategory = contentCategoryMapper.selectByPrimaryKey(id);
        contentCategory.setName(name);
        contentCategoryMapper.updateByPrimaryKey(contentCategory);
        return E3Result.ok();
    }
}
