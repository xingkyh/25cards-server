package util;


import javax.servlet.ServletContext;  
import javax.servlet.ServletContextEvent;  
import javax.servlet.ServletContextListener;

import thread.Service;  
  
/** 
 * 将socket service随tomcat启动 
 * @author zhangzhongwen 
 * 
 */  
public class SocketServiceLoader implements ServletContextListener{  
    //socket server 线程  
    private Service service;  
      
    @Override  
    public void contextDestroyed(ServletContextEvent arg0) {  
        if(null!=service && !service.isInterrupted())  
           {  
        	service.closeSocketServer();  
            service.interrupt();  
           }  
    }  
  
    @Override  
    public void contextInitialized(ServletContextEvent arg0) {  
        // TODO Auto-generated method stub  
        if(null==service)  
        {  
         //新建线程类  
         service = new Service(null);  
         //启动线程  
         service.start();  
        }  
    }  
  
}  