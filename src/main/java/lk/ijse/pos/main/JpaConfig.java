package lk.ijse.pos.main;

import lk.ijse.pos.dao.custom.CustomerDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@PropertySource("file:${user.dir}/resources/application.properties")
@EnableTransactionManagement
@EnableJpaRepositories(basePackageClasses = CustomerDAO.class)
public class JpaConfig {
    @Autowired
    public Environment environment;


    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource ds, JpaVendorAdapter jva){
        LocalContainerEntityManagerFactoryBean lcemf = new LocalContainerEntityManagerFactoryBean();
        lcemf.setDataSource(ds);
        lcemf.setJpaVendorAdapter(jva);
        lcemf.setPackagesToScan("lk.ijse.pos");
        return lcemf;
    }


    @Bean
    public DataSource dataSource(){
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setDriverClassName(environment.getRequiredProperty("javax.persistence.jdbc.driver"));
        driverManagerDataSource.setUrl(environment.getRequiredProperty("javax.persistence.jdbc.url"));
        driverManagerDataSource.setUsername(environment.getRequiredProperty("javax.persistence.jdbc.user"));
        driverManagerDataSource.setPassword(environment.getRequiredProperty("javax.persistence.jdbc.password"));
        return driverManagerDataSource;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter(){
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter =  new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setDatabase(Database.MYSQL);
        hibernateJpaVendorAdapter.setDatabasePlatform(environment.getRequiredProperty("hibernate.dialect"));
        hibernateJpaVendorAdapter.setShowSql(Boolean.getBoolean(environment.getRequiredProperty("hibernate.show_sql")));
        hibernateJpaVendorAdapter.setGenerateDdl(environment.getRequiredProperty("hibernate.hbm2ddl.auto").equals("update")?true:false);
        return hibernateJpaVendorAdapter;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf){
        return new JpaTransactionManager(emf);
    }
}
