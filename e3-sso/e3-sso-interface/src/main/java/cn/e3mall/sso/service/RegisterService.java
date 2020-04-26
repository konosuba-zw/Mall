package cn.e3mall.sso.service;

import cn.e3mall.common.pojo.TbUser;
import cn.e3mall.common.util.E3Result;

public interface RegisterService {

    E3Result checkData(String param,int type);
    E3Result register(TbUser user);
}
