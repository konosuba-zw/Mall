package cn.e3mall.fast;

import cn.e3mall.common.util.FastDFSClient;
import org.csource.fastdfs.*;
import org.junit.jupiter.api.Test;

public class FastDfsTest {

    @Test
    public void testUpload() throws Exception{
        //创意一个配置文件。文件名任意。内容就是tracker服务器的地址。
        //使用全局对象加载配置文件
        ClientGlobal.init("D:\\SSM\\e33\\e3-manager-web\\src\\main\\resources\\conf\\client.conf");
        //创建一个TrackerClient对象
        TrackerClient trackerClient = new TrackerClient();
        //通过TrackerClient获得一个TrackerServer对象
        TrackerServer trackerServer = trackerClient.getConnection();
        //创建一个StorageServer的引用，可以是null
        StorageServer storageServer = null;
        //创建一个StorageClient。参数需要TrackerServer和StorageServer
        StorageClient storageClient = new StorageClient(trackerServer,storageServer);
        //使用StorageClient上传文件
        String[] strings = storageClient.upload_file("C:\\Users\\Shinelon\\Desktop\\timg.jpg", "jpg", null);
        for (String string : strings){
            System.out.println(string);
        }
    }

    @Test
    public void testFastDfsClient() throws Exception{
        FastDFSClient fastDFSClient = new FastDFSClient("D:\\SSM\\e33\\e3-manager-web\\src\\main\\resources\\conf\\client.conf");
        String string = fastDFSClient.uploadFile("C:\\Users\\Shinelon\\Desktop\\502f003b4d4f8dd9bcb0ec93517a12ad61a06733.jpg");
        System.out.println(string);
    }
}
