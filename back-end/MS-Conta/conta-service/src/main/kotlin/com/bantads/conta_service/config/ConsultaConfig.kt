package com.bantads.conta_service.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import javax.sql.DataSource

@Configuration
@EnableJpaRepositories(
    basePackages = ["com.bantads.conta_service.repository.leitura"], 
    entityManagerFactoryRef = "queryEntityManager",
    transactionManagerRef = "queryTransactionManager"
)
class ConsultaConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.query")
    fun queryDataSource(): DataSource {
        return DataSourceBuilder.create().build()
    }

    @Bean
    fun queryEntityManager(
        @Qualifier("queryDataSource") dataSource: DataSource
    ): LocalContainerEntityManagerFactoryBean {
        val em = LocalContainerEntityManagerFactoryBean()
        em.dataSource = dataSource
        
        em.setPackagesToScan("com.bantads.conta_service.entity") 
        em.jpaVendorAdapter = HibernateJpaVendorAdapter()
        return em
    }

    @Bean
    fun queryTransactionManager(
        @Qualifier("queryEntityManager") queryEntityManager: LocalContainerEntityManagerFactoryBean
    ): JpaTransactionManager {
        return JpaTransactionManager(queryEntityManager.`object`!!)
    }
}