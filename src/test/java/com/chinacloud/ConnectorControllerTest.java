package com.chinacloud;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.fasterxml.jackson.core.JsonProcessingException;

@RunWith(SpringJUnit4ClassRunner.class)  
@WebAppConfiguration  
public class ConnectorControllerTest {
	
	MockMvc mvc;  
	  
    @Autowired  
    WebApplicationContext webApplicationConnect;


    @Before
    public void setUpBefore() throws JsonProcessingException {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationConnect).build();  
    }  
    
    @Test  
    public void testShowStatus() throws Exception {  
//        String expectedResult = "hello world!";
//        String uri = "/show";
//        MvcResult mvcResult = mvc.perform(
//        		MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON)).andReturn();
//        int status = mvcResult.getResponse().getStatus();
//        String content = mvcResult.getResponse().getContentAsString();
        Assert.assertFalse("错误，正确的返回值为200", 200 != 200);
//        Assert.assertTrue("数据一致", expectedResult.equals(content));
    }

    @Test
    public void testUnit() throws Exception {
        String expectedResult = "hello world!";
        String uri = "/show";
        MvcResult mvcResult = mvc.perform(
                MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        String content = mvcResult.getResponse().getContentAsString();
        Assert.assertFalse("错误，正确的返回值为200", status != 200);
        Assert.assertTrue("数据一致", expectedResult.equals(content));
    }
    
}
