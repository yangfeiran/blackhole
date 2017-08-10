package com.chinacloud;

import com.chinacloud.controller.DatafeedController;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/10.
 */
public class ControllerTest {
    @Test
    public void testupdateinfo(){
        Map<String, Object> updateInfo = Maps.newHashMap();

        List<String> list = Lists.newArrayList();
        list.add("shit");
        updateInfo.put("lables",list);
        updateInfo.put("jobInfo","");

        DatafeedController datafeedController = new DatafeedController();
        datafeedController.updateJobInfo("12",updateInfo);
    }
}
