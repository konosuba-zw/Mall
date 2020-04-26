package cn.e3mall.solrj;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class TestSolrCloud {

    @Test
    public void testAddDocument() throws Exception{
        //创建一个集群的连接，应该使用CloudSolrServer创建
        CloudSolrServer solrServer = new CloudSolrServer("192.168.25.128:2181,192.168.25.128:2182,192.168.25.128:2183");
        //zkHost：zookeeper的地址列表
        //设置一个defaultConllection属性
        solrServer.setDefaultCollection("collection2");
        //创建一个文档对象
        SolrInputDocument document = new SolrInputDocument();
        //向文档中添加索引库
        document.setField("id","solrcloud01");
        document.setField("item_title","测试商品01");
        document.setField("item_price",123);
        //把文件写入索引库
        solrServer.add(document);
        //提交
        solrServer.commit();
    }

    @Test
    public void testQueryDocument() throws Exception{
        CloudSolrServer cloudSolrServer = new CloudSolrServer("192.168.25.128:2181,192.168.25.128:2182,192.168.25.128:2183");
        cloudSolrServer.setDefaultCollection("collection2");
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        QueryResponse queryResponse = cloudSolrServer.query(query);
        SolrDocumentList solrDocumentList = queryResponse.getResults();
        System.out.println(solrDocumentList.getNumFound());
        for(SolrDocument solrDocument : solrDocumentList){
            System.out.println(solrDocument.get("id"));
            System.out.println(solrDocument.get("title"));
            System.out.println(solrDocument.get("item_title"));
            System.out.println(solrDocument.get("item_price"));
        }
    }
}
