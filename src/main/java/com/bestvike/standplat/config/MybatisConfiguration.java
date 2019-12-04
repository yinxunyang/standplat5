package com.bestvike.standplat.config;

import com.github.pagehelper.PageInterceptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@MapperScan(value = "com.bestvike.standplat.dao",
        properties = {
                "type=normal"
        })
public class MybatisConfiguration implements ApplicationContextAware {
    // @Autowired
    // DataSource dataSource;
    protected Log logger = LogFactory.getLog(this.getClass());

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    }

    @Bean(name="dataSource")
    @ConfigurationProperties(prefix = "datasources.standplat")
    @Primary
    public DataSource dataSource() {
        return DataSourceBuilder.create().build(); // .type(DataSource.class).build();
    }

    @Bean(name="sqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactoryBean() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource());
        sqlSessionFactoryBean.setTypeAliasesPackage("com.bestvike.standplat.data");
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mapping/standplat/*.xml"));
        tk.mybatis.mapper.session.Configuration configuration = new tk.mybatis.mapper.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        sqlSessionFactoryBean.setConfiguration(configuration);

       /* // 分页插件
        Interceptor interceptor = new PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("helperDialect", "mariadb");
        // properties.setProperty("offsetAsPageNum", "true");
        properties.setProperty("rowBoundsWithCount", "true");
        properties.setProperty("reasonable", "true");
        properties.setProperty("supportMethodsArguments","true");
        properties.setProperty("params","pageNum=page;pageSize=limit;");
        interceptor.setProperties(properties);
        sqlSessionFactoryBean.setPlugins(new Interceptor[] {interceptor});*/
        return sqlSessionFactoryBean.getObject();
    }
    @Bean(name="sqlSessionTemplate")
    @Primary
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
    @Bean(name = "transactionManager")
    @Primary
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }
}