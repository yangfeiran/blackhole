package com.chinacloud;

import com.chinacloud.dao.JobDao;
import com.chinacloud.model.Job;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/7.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class JobDaoTest {
    @Autowired
    JobDao jobDao;

    @Test
    public void findByQueryTest(){
        String id = "ad421ecc-1010-115d-5dfb-c1bff707b8d8";
        String query = "hh";

        List<Map<String, String>> allTotalCountTogether = jobDao.getAllTotalCountTogether();
        Job byQuery = jobDao.findByQuery(id, query);

    }

}
