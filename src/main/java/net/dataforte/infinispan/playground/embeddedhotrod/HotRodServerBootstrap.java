package net.dataforte.infinispan.playground.embeddedhotrod;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.server.hotrod.HotRodServer;
import org.infinispan.server.hotrod.configuration.HotRodServerConfigurationBuilder;

public class HotRodServerBootstrap implements ServletContextListener {

   // Attributes attached to the ServletContext
   public final static String CACHE_MANAGER = "INFINISPAN_CACHE_MANAGER";
   public final static String SERVER = "INFINISPAN_HOTROD_SERVER";


   public static EmbeddedCacheManager getCacheManager(ServletContext ctx) {
      return (EmbeddedCacheManager) ctx.getAttribute(CACHE_MANAGER);
   }

   public static HotRodServer getHotRodServer(ServletContext ctx) {
      return (HotRodServer) ctx.getAttribute(SERVER);
   }

   @Override
   public void contextInitialized(ServletContextEvent sce) {
      ServletContext ctx = sce.getServletContext();

      EmbeddedCacheManager cacheManager = createCacheManager(ctx);
      ctx.setAttribute(CACHE_MANAGER, cacheManager);
      HotRodServer server = new HotRodServer();
      HotRodServerConfigurationBuilder serverConfig = new HotRodServerConfigurationBuilder();
      // Configure your server here
      // ...

      server.start(serverConfig.build(), cacheManager);
      ctx.setAttribute(SERVER, server);
   }


   private EmbeddedCacheManager createCacheManager(ServletContext ctx) {
      EmbeddedCacheManager cm = null;
      try {
         cm = new DefaultCacheManager(ctx.getResourceAsStream("/WEB-INF/infinispan.xml"));
      } catch (IOException e) {
         // Maybe log the error
         cm = new DefaultCacheManager();
      }
      return cm;
   }

   @Override
   public void contextDestroyed(ServletContextEvent sce) {
      ServletContext ctx = sce.getServletContext();
      HotRodServer server = getHotRodServer(ctx);
      if (server != null) {
         server.stop();
      }
      EmbeddedCacheManager cm = getCacheManager(ctx);
      if (cm != null) {
         cm.stop();
      }
   }

}
